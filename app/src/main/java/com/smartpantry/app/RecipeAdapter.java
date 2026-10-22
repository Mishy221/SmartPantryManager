package com.smartpantry.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Custom RecyclerView adapter that lists recipes (used for both the strict list and the Almost There list). */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface Listener {
        void onRecipeClicked(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final Map<Long, String> missingText = new HashMap<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    /** @param missing optional map recipeId -> "Missing: butter (need 10 g)"; may be null */
    public void setData(List<Recipe> newRecipes, Map<Long, String> missing) {
        recipes.clear();
        recipes.addAll(newRecipes);
        missingText.clear();
        if (missing != null) {
            missingText.putAll(missing);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        final Recipe r = recipes.get(position);
        h.name.setText(r.getName());
        h.info.setText(r.getIngredients().size() + " ingredients");
        String missing = missingText.get(r.getId());
        if (missing == null) {
            h.missing.setVisibility(View.GONE);
        } else {
            h.missing.setText(missing);
            h.missing.setVisibility(View.VISIBLE);
        }
        h.itemView.setOnClickListener(v -> listener.onRecipeClicked(r));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView info;
        final TextView missing;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvRecipeName);
            info = itemView.findViewById(R.id.tvRecipeInfo);
            missing = itemView.findViewById(R.id.tvRecipeMissing);
        }
    }
}
