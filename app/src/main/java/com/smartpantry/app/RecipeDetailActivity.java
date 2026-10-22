package com.smartpantry.app;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

/** Screen 4: full ingredient list (with a have / missing marker) and method for one recipe. */
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        long recipeId = getIntent().getLongExtra(SuggestedRecipesActivity.EXTRA_RECIPE_ID, -1);
        DatabaseHelper db = new DatabaseHelper(this);
        Recipe recipe = db.getRecipe(recipeId);
        Map<String, Double> totals = IngredientMatcher.buildPantryTotals(db.getAllPantryItems());
        db.close();

        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle(recipe.getName());
        ((TextView) findViewById(R.id.tvDetailName)).setText(recipe.getName());

        StringBuilder sb = new StringBuilder();
        boolean canCook = true;
        for (RecipeIngredient ing : recipe.getIngredients()) {
            boolean have = IngredientMatcher.hasIngredient(ing, totals);
            canCook &= have;
            sb.append(have ? "\u2713  " : "\u2717  ")
                    .append(IngredientMatcher.formatQty(ing.getQuantity())).append(' ')
                    .append(ing.getUnit()).append("  ").append(ing.getName())
                    .append(have ? "" : "   (not enough in pantry)")
                    .append('\n');
        }
        ((TextView) findViewById(R.id.tvDetailIngredients)).setText(sb.toString().trim());
        ((TextView) findViewById(R.id.tvDetailStatus)).setText(
                canCook ? "You have everything you need!" : "Some ingredients are missing from your pantry.");
        ((TextView) findViewById(R.id.tvDetailSteps)).setText(recipe.getSteps());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
