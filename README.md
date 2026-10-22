# Smart Pantry Manager

A native **Java Android application** that helps reduce food waste. The user tracks the ingredients they
actually have at home (their "pantry") and the app suggests recipes they can cook **strictly** from those
leftovers - a recipe is only suggested when *every* ingredient it needs is in the pantry in at least the required
quantity. No shopping trip required.

Built for **Mobile App Development 700 - Practical Assignment** (Richfield Graduate Institute of Technology).

## Features

- **Pantry management** - add, edit and delete ingredients (name, quantity, unit, optional expiry date) with input validation.
- **Pantry list** - RecyclerView with a custom adapter, bound to the SQLite database; expired / expiring-soon items are highlighted.
- **20 pre-loaded recipes** - seeded into the database on first run.
- **Suggested Recipes** - strict-matching logic (`IngredientMatcher.java`): 4 of 5 ingredients = *not* suggested.
  Robust to singular/plural names (`tomato` = `tomatoes`), upper/lower case, and unit differences (`kg` vs `g`, `tbsp` vs `ml`).
- **Recipe detail** - full ingredient list (with a tick/cross for what you have) and method.
- **Settings** - expiring-soon alerts toggle, optional "Almost There" bonus list (recipes missing exactly one ingredient, kept clearly separate), plus helper buttons to add sample ingredients / clear the pantry.
- Friendly message when no recipes match: *"No recipes match your pantry yet - add more ingredients"*.
- Bottom navigation bar (Pantry / Recipes / Settings).
- **No** Google Maps, mapping SDKs, GPS or location services are used.

## Database choice: SQLite

I chose **SQLite** (via `SQLiteOpenHelper`) because the data is small and personal to one device, it works fully
offline, needs no account or server set-up, persists after the app is closed, and supports the full CRUD cycle
with simple SQL. Tables: `pantry`, `recipes`, `recipe_ingredients` (foreign key to `recipes`).

## Project structure

```
app/src/main/java/com/smartpantry/app/
  PantryListActivity.java          Screen 1 - pantry list (launcher)
  AddEditIngredientActivity.java   Screen 2 - add / edit ingredient + validation
  SuggestedRecipesActivity.java    Screen 3 - suggested recipes (strict matching)
  RecipeDetailActivity.java        Screen 4 - recipe detail
  SettingsActivity.java            Screen 5 - settings
  IngredientMatcher.java           Core strict-matching business logic
  DatabaseHelper.java / RecipeSeeder.java   SQLite + seeded recipes
  PantryAdapter.java / RecipeAdapter.java   RecyclerView adapters
  PantryItem / Recipe / RecipeIngredient    Model classes
  NavHelper / Prefs / ExpiryUtils           Small helpers
app/src/test/.../IngredientMatcherTest.java Unit tests for the matching rule
```

## Setup and run instructions

**Requirements:** Android Studio (Hedgehog or newer), internet connection for the first Gradle sync.

1. Clone or download this repository:
   `git clone https://github.com/Mishy221/SmartPantryManager.git`
2. Open Android Studio -> **File > Open** -> select the `SmartPantryManager` folder (the one containing `settings.gradle`).
3. Wait for **Gradle Sync** to finish (bottom status bar). Accept any prompt to install missing SDK components.
4. Create an emulator: **Tools > Device Manager > Create Device** (e.g. Pixel 6, API 34), or plug in a physical phone with USB debugging enabled.
5. Press the green **Run** button. The app installs and opens on the Pantry screen.
6. Quick test: open **Settings > Add sample ingredients**, then open the **Recipes** tab.

Run the unit tests: right-click `IngredientMatcherTest` > **Run**.

Minimum Android version: 7.0 (API 24). Language: Java 8.
