package com.smartpantry.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CORE BUSINESS LOGIC - the Strict-Matching Rule (assignment Section 2.3).
 *
 * A recipe is only "suggested" when EVERY ingredient it needs is in the pantry in at least the
 * required quantity. To be robust against everyday messiness the matcher:
 *   1. normalises names  (case, spaces, plural -> singular, a few synonyms: "tomatoes" == "tomato")
 *   2. converts units    (kg <-> g, l <-> ml, tbsp/tsp/cup -> ml) before comparing quantities
 *   3. adds up several pantry rows of the same ingredient (e.g. 2 packs of rice)
 */
public final class IngredientMatcher {

    private static final double EPSILON = 1e-9;

    private static final Map<String, String> SYNONYMS = new HashMap<>();
    static {
        SYNONYMS.put("yoghurt", "yogurt");
        SYNONYMS.put("courgette", "zucchini");
        SYNONYMS.put("aubergine", "eggplant");
        SYNONYMS.put("chilli", "chili");
        SYNONYMS.put("spaghetti", "pasta");
        SYNONYMS.put("penne", "pasta");
        SYNONYMS.put("macaroni", "pasta");
    }

    private IngredientMatcher() { }

    // ---------------------------------------------------------------- name normalising

    /** "  Tomatoes " -> "tomato", "Cherries" -> "cherry", "EGGS" -> "egg". */
    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z ]", " ").trim().replaceAll("\\s+", " ");
        if (s.isEmpty()) {
            return s;
        }
        // Only the last word is singularised: "cherry tomatoes" -> "cherry tomato"
        int lastSpace = s.lastIndexOf(' ');
        String head = lastSpace >= 0 ? s.substring(0, lastSpace + 1) : "";
        String last = singular(s.substring(lastSpace + 1));
        if (SYNONYMS.containsKey(last)) {
            last = SYNONYMS.get(last);
        }
        return head + last;
    }

    private static String singular(String w) {
        if (w.length() <= 3) {
            return w;
        }
        if (w.endsWith("ies")) {
            return w.substring(0, w.length() - 3) + "y";          // berries -> berry
        }
        if (w.endsWith("oes") || w.endsWith("ches") || w.endsWith("shes") || w.endsWith("xes")) {
            return w.substring(0, w.length() - 2);                // tomatoes -> tomato
        }
        if (w.endsWith("ss") || w.endsWith("us") || w.endsWith("is")) {
            return w;                                             // hummus, asparagus
        }
        if (w.endsWith("s")) {
            return w.substring(0, w.length() - 1);                // eggs -> egg
        }
        return w;
    }

    // ---------------------------------------------------------------- unit handling

    /** Maps whatever the user typed to one of: g, kg, ml, l, tsp, tbsp, cup, pcs. */
    public static String canonicalUnit(String raw) {
        String u = raw == null ? "" : raw.toLowerCase(Locale.ROOT).trim().replace(".", "");
        switch (u) {
            case "g": case "gram": case "grams": return "g";
            case "kg": case "kilogram": case "kilograms": return "kg";
            case "ml": case "milliliter": case "milliliters": case "millilitre": case "millilitres": return "ml";
            case "l": case "liter": case "liters": case "litre": case "litres": return "l";
            case "tsp": case "teaspoon": case "teaspoons": return "tsp";
            case "tbsp": case "tablespoon": case "tablespoons": return "tbsp";
            case "cup": case "cups": return "cup";
            default: return "pcs"; // pcs, piece, pieces, whole, each, unknown ...
        }
    }

    /** "mass", "volume" or "count" - quantities can only be compared inside the same dimension. */
    public static String dimensionOf(String unit) {
        switch (canonicalUnit(unit)) {
            case "g": case "kg": return "mass";
            case "ml": case "l": case "tsp": case "tbsp": case "cup": return "volume";
            default: return "count";
        }
    }

    /** Converts a quantity to its base unit: grams (mass), millilitres (volume) or pieces (count). */
    public static double toBase(double qty, String unit) {
        switch (canonicalUnit(unit)) {
            case "kg": return qty * 1000.0;
            case "l": return qty * 1000.0;
            case "tsp": return qty * 5.0;
            case "tbsp": return qty * 15.0;
            case "cup": return qty * 240.0;
            default: return qty; // g, ml, pcs
        }
    }

    private static String baseUnitLabel(String dimension) {
        switch (dimension) {
            case "mass": return "g";
            case "volume": return "ml";
            default: return "pcs";
        }
    }

    private static String key(String name, String unit) {
        return normalizeName(name) + "|" + dimensionOf(unit);
    }

    // ---------------------------------------------------------------- matching

    /** Adds up the whole pantry into one map: "egg|count" -> 6.0, "flour|mass" -> 500.0 ... */
    public static Map<String, Double> buildPantryTotals(List<PantryItem> items) {
        Map<String, Double> totals = new HashMap<>();
        for (PantryItem item : items) {
            String k = key(item.getName(), item.getUnit());
            double add = toBase(item.getQuantity(), item.getUnit());
            Double old = totals.get(k);
            totals.put(k, (old == null ? 0.0 : old) + add);
        }
        return totals;
    }

    /** How much of this recipe ingredient does the pantry hold (in base units)? */
    public static double amountInPantry(RecipeIngredient ing, Map<String, Double> totals) {
        Double have = totals.get(key(ing.getName(), ing.getUnit()));
        return have == null ? 0.0 : have;
    }

    /** True when the pantry has at least the required quantity of this one ingredient. */
    public static boolean hasIngredient(RecipeIngredient ing, Map<String, Double> totals) {
        double need = toBase(ing.getQuantity(), ing.getUnit());
        return amountInPantry(ing, totals) + EPSILON >= need;
    }

    /** Returns a human readable list of every ingredient the pantry cannot cover (empty = can cook). */
    public static List<String> findMissing(Recipe recipe, Map<String, Double> totals) {
        List<String> missing = new ArrayList<>();
        for (RecipeIngredient ing : recipe.getIngredients()) {
            if (!hasIngredient(ing, totals)) {
                missing.add(ing.getName() + " (need " + formatQty(ing.getQuantity()) + " " + ing.getUnit() + ")");
            }
        }
        return missing;
    }

    /** THE STRICT RULE: every single ingredient must be available in the required quantity. */
    public static boolean isStrictMatch(Recipe recipe, Map<String, Double> totals) {
        if (recipe.getIngredients().isEmpty()) {
            return false; // a recipe with no ingredients is never suggested
        }
        return findMissing(recipe, totals).isEmpty();
    }

    // ---------------------------------------------------------------- formatting

    /** 2.0 -> "2", 0.5 -> "0.5" */
    public static String formatQty(double q) {
        if (Math.abs(q - Math.rint(q)) < 1e-9) {
            return String.valueOf((long) Math.rint(q));
        }
        return String.format(Locale.US, "%.2f", q).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    /** Used only for documentation / debugging output. */
    public static String describeBase(double baseQty, String unit) {
        return formatQty(baseQty) + " " + baseUnitLabel(dimensionOf(unit));
    }
}
