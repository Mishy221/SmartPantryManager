package com.smartpantry.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Local unit tests for the strict-matching rule (run with: Right-click the file > Run). */
public class IngredientMatcherTest {

    private Recipe cheeseToastie() {
        Recipe r = new Recipe(1, "Cheese Toastie", "");
        r.getIngredients().add(new RecipeIngredient("bread", 2, "pcs"));
        r.getIngredients().add(new RecipeIngredient("cheese", 60, "g"));
        r.getIngredients().add(new RecipeIngredient("butter", 10, "g"));
        return r;
    }

    @Test
    public void pluralAndCaseAreIgnored() {
        assertEquals("tomato", IngredientMatcher.normalizeName("  Tomatoes "));
        assertEquals("egg", IngredientMatcher.normalizeName("EGGS"));
        assertEquals("berry", IngredientMatcher.normalizeName("berries"));
    }

    @Test
    public void missingOneIngredientMeansNotSuggested() {
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem(1, "Breads", 4, "pcs", ""));
        pantry.add(new PantryItem(2, "Cheese", 0.2, "kg", "")); // 200 g
        Map<String, Double> totals = IngredientMatcher.buildPantryTotals(pantry);
        assertFalse(IngredientMatcher.isStrictMatch(cheeseToastie(), totals));
        assertEquals(1, IngredientMatcher.findMissing(cheeseToastie(), totals).size());
    }

    @Test
    public void allIngredientsPresentMeansSuggested() {
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem(1, "Bread", 2, "pcs", ""));
        pantry.add(new PantryItem(2, "Cheese", 60, "g", ""));
        pantry.add(new PantryItem(3, "Butter", 0.01, "kg", "")); // 10 g
        assertTrue(IngredientMatcher.isStrictMatch(cheeseToastie(), IngredientMatcher.buildPantryTotals(pantry)));
    }

    @Test
    public void notEnoughQuantityMeansNotSuggested() {
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem(1, "Bread", 2, "pcs", ""));
        pantry.add(new PantryItem(2, "Cheese", 60, "g", ""));
        pantry.add(new PantryItem(3, "Butter", 5, "g", "")); // needs 10 g
        assertFalse(IngredientMatcher.isStrictMatch(cheeseToastie(), IngredientMatcher.buildPantryTotals(pantry)));
    }

    @Test
    public void unitConversionWorks() {
        Recipe r = new Recipe(2, "Test", "");
        r.getIngredients().add(new RecipeIngredient("olive oil", 2, "tbsp"));
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem(1, "Olive oil", 250, "ml", ""));
        assertTrue(IngredientMatcher.isStrictMatch(r, IngredientMatcher.buildPantryTotals(pantry)));
    }
}
