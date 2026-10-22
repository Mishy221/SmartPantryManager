package com.smartpantry.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Screen 3: runs the strict-matching rule and lists ONLY the recipes the user can cook right now. */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private DatabaseHelper db;
    private RecipeAdapter strictAdapter;
    private RecipeAdapter almostAdapter;
    private TextView tvHeader;
    private TextView tvNoMatch;
    private TextView tvAlmostHeader;
    private RecyclerView rvAlmost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        setTitle("Suggested Recipes");

        db = new DatabaseHelper(this);
        tvHeader = findViewById(R.id.tvSuggestedHeader);
        tvNoMatch = findViewById(R.id.tvNoMatch);
        tvAlmostHeader = findViewById(R.id.tvAlmostHeader);
        rvAlmost = findViewById(R.id.rvAlmost);

        RecyclerView rvStrict = findViewById(R.id.rvSuggested);
        rvStrict.setLayoutManager(new LinearLayoutManager(this));
        strictAdapter = new RecipeAdapter(this);
        rvStrict.setAdapter(strictAdapter);

        rvAlmost.setLayoutManager(new LinearLayoutManager(this));
        almostAdapter = new RecipeAdapter(this);
        rvAlmost.setAdapter(almostAdapter);

        NavHelper.setup(this, R.id.nav_recipes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions(); // pantry may have changed since we were last here
    }

    private void loadSuggestions() {
        Map<String, Double> totals = IngredientMatcher.buildPantryTotals(db.getAllPantryItems());

        List<Recipe> strict = new ArrayList<>();
        List<Recipe> almost = new ArrayList<>();
        Map<Long, String> almostMissing = new HashMap<>();

        for (Recipe recipe : db.getAllRecipes()) {
            List<String> missing = IngredientMatcher.findMissing(recipe, totals);
            if (IngredientMatcher.isStrictMatch(recipe, totals)) {
                strict.add(recipe);                    // every ingredient present -> suggested
            } else if (missing.size() == 1) {
                almost.add(recipe);                    // exactly one missing -> optional bonus list only
                almostMissing.put(recipe.getId(), "Missing: " + missing.get(0));
            }                                          // 2+ missing (or partial) -> never shown
        }

        strictAdapter.setData(strict, null);
        tvHeader.setText("Recipes you can make now (" + strict.size() + ")");
        tvNoMatch.setVisibility(strict.isEmpty() ? View.VISIBLE : View.GONE);

        boolean showAlmost = Prefs.showAlmostThere(this) && !almost.isEmpty();
        almostAdapter.setData(almost, almostMissing);
        tvAlmostHeader.setVisibility(showAlmost ? View.VISIBLE : View.GONE);
        rvAlmost.setVisibility(showAlmost ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
