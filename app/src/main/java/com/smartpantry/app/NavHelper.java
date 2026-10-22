package com.smartpantry.app;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Wires up the bottom navigation bar that is shared by the three main screens. */
public final class NavHelper {

    private NavHelper() { }

    public static void setup(final Activity activity, final int selectedItemId) {
        BottomNavigationView nav = activity.findViewById(R.id.bottomNav);
        nav.setSelectedItemId(selectedItemId);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedItemId) {
                return true; // already on this screen
            }
            if (id == R.id.nav_pantry) {
                // Go back to the existing Pantry screen instead of stacking a new one
                Intent intent = new Intent(activity, PantryListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            } else {
                Class<?> target = (id == R.id.nav_recipes) ? SuggestedRecipesActivity.class : SettingsActivity.class;
                activity.startActivity(new Intent(activity, target));
                if (!(activity instanceof PantryListActivity)) {
                    activity.finish(); // keep the back stack short: Pantry -> current tab
                }
            }
            activity.overridePendingTransition(0, 0);
            return true;
        });
    }
}
