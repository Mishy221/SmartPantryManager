package com.smartpantry.app;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** Pre-loads (seeds) the recipe collection the first time the database is created. */
final class RecipeSeeder {

    private RecipeSeeder() { }

    static void seed(SQLiteDatabase db) {
        add(db, "Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Whisk the eggs with a pinch of salt.\n3. Melt the butter in a pan and stir the eggs gently until just set.\n4. Spoon onto the toast and serve.",
                "egg:2:pcs", "bread:2:pcs", "butter:10:g");
        add(db, "Cheese Omelette",
                "1. Beat the eggs.\n2. Melt the butter in a pan on medium heat.\n3. Pour in the eggs and let them set slightly.\n4. Sprinkle over the cheese, fold in half and cook for 1 more minute.",
                "egg:3:pcs", "cheese:50:g", "butter:10:g");
        add(db, "Tomato Pasta",
                "1. Boil the pasta until al dente.\n2. Fry chopped onion and garlic in olive oil.\n3. Add chopped tomatoes and simmer for 10 minutes.\n4. Toss the drained pasta in the sauce.",
                "pasta:200:g", "tomato:3:pcs", "onion:1:pcs", "garlic:2:pcs", "olive oil:2:tbsp");
        add(db, "Egg Fried Rice",
                "1. Cook the rice and let it cool.\n2. Fry the chopped onion, then push to the side and scramble the eggs.\n3. Add the rice and soy sauce.\n4. Stir-fry for 3-4 minutes and serve hot.",
                "rice:200:g", "egg:2:pcs", "onion:1:pcs", "soy sauce:2:tbsp");
        add(db, "Fluffy Pancakes",
                "1. Mix the flour and sugar.\n2. Whisk in the milk and egg until smooth.\n3. Pour small rounds into a hot greased pan.\n4. Flip when bubbles appear and cook until golden.",
                "flour:150:g", "milk:250:ml", "egg:1:pcs", "sugar:1:tbsp");
        add(db, "Creamy Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash with the butter.\n3. Stir in the warm milk and salt until creamy.",
                "potato:4:pcs", "butter:30:g", "milk:100:ml", "salt:1:tsp");
        add(db, "Simple Vegetable Soup",
                "1. Chop the potatoes, carrots and onion.\n2. Fry the onion in a pot for 3 minutes.\n3. Add the other vegetables, salt and enough water to cover.\n4. Simmer for 25 minutes and blend if you like it smooth.",
                "potato:2:pcs", "carrot:2:pcs", "onion:1:pcs", "salt:1:tsp");
        add(db, "Banana Oat Porridge",
                "1. Heat the milk and oats in a pot, stirring for 5 minutes.\n2. Slice the banana on top.\n3. Drizzle with honey and serve.",
                "oats:80:g", "milk:250:ml", "banana:1:pcs", "honey:1:tbsp");
        add(db, "Cheese Toastie",
                "1. Butter the outside of the bread.\n2. Put the cheese between the slices.\n3. Fry in a pan for 2-3 minutes per side until golden and melted.",
                "bread:2:pcs", "cheese:60:g", "butter:10:g");
        add(db, "Tuna Mayo Sandwich",
                "1. Drain the tuna and mix it with the mayonnaise.\n2. Spread onto a slice of bread.\n3. Top with the second slice and cut in half.",
                "bread:2:pcs", "tuna:100:g", "mayonnaise:1:tbsp");
        add(db, "Chicken Rice Bowl",
                "1. Season the chicken with salt and fry until cooked through.\n2. Cook the rice.\n3. Fry the sliced onion until soft.\n4. Serve the chicken and onion over the rice.",
                "chicken:300:g", "rice:200:g", "onion:1:pcs", "salt:1:tsp");
        add(db, "Easy Lentil Curry",
                "1. Fry the chopped onion until soft.\n2. Stir in the curry powder for 30 seconds.\n3. Add the lentils, chopped tomatoes and enough water to cover.\n4. Simmer for 25 minutes until thick.",
                "lentil:200:g", "onion:1:pcs", "tomato:2:pcs", "curry powder:2:tsp");
        add(db, "French Toast",
                "1. Whisk the eggs, milk and sugar in a shallow dish.\n2. Soak each slice of bread for 20 seconds per side.\n3. Fry in a hot pan until golden brown on both sides.",
                "bread:4:pcs", "egg:2:pcs", "milk:100:ml", "sugar:1:tbsp");
        add(db, "Avocado Toast",
                "1. Toast the bread.\n2. Mash the avocado with the salt.\n3. Spread on the toast and serve.",
                "avocado:1:pcs", "bread:2:pcs", "salt:1:tsp");
        add(db, "Baked Beans on Toast",
                "1. Toast and butter the bread.\n2. Warm the beans in a small pot for 3 minutes.\n3. Spoon the beans over the toast.",
                "beans:200:g", "bread:2:pcs", "butter:10:g");
        add(db, "Roast Potatoes",
                "1. Heat the oven to 220 C.\n2. Cut the potatoes into chunks and toss with olive oil and salt.\n3. Roast for 40 minutes, turning once, until crispy.",
                "potato:4:pcs", "olive oil:2:tbsp", "salt:1:tsp");
        add(db, "Vegetable Stir Fry",
                "1. Slice the carrots, onion and pepper into strips.\n2. Fry the garlic for 30 seconds.\n3. Add the vegetables and stir-fry for 5 minutes.\n4. Finish with the soy sauce.",
                "carrot:2:pcs", "onion:1:pcs", "bell pepper:1:pcs", "soy sauce:2:tbsp", "garlic:2:pcs");
        add(db, "Fruit Yogurt Bowl",
                "1. Spoon the yogurt into a bowl.\n2. Slice the banana on top.\n3. Sprinkle with oats and drizzle with honey.",
                "yogurt:200:g", "banana:1:pcs", "honey:1:tbsp", "oats:30:g");
        add(db, "Spaghetti Aglio e Olio",
                "1. Boil the pasta until al dente.\n2. Gently fry sliced garlic and chili flakes in olive oil.\n3. Toss the pasta in the garlic oil and serve.",
                "pasta:200:g", "garlic:4:pcs", "olive oil:3:tbsp", "chili flakes:1:tsp");
        add(db, "Homemade Tomato Soup",
                "1. Fry the onion and garlic in butter.\n2. Add the chopped tomatoes and salt.\n3. Simmer for 20 minutes, then blend until smooth.",
                "tomato:6:pcs", "onion:1:pcs", "garlic:2:pcs", "butter:20:g", "salt:1:tsp");
    }

    /** Inserts one recipe and its ingredients. Each ingredient string looks like "name:quantity:unit". */
    private static void add(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues rv = new ContentValues();
        rv.put(DatabaseHelper.COL_R_NAME, name);
        rv.put(DatabaseHelper.COL_R_STEPS, steps);
        long recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, rv);

        for (String line : ingredients) {
            String[] p = line.split(":");
            ContentValues iv = new ContentValues();
            iv.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId);
            iv.put(DatabaseHelper.COL_RI_NAME, p[0]);
            iv.put(DatabaseHelper.COL_RI_QTY, Double.parseDouble(p[1]));
            iv.put(DatabaseHelper.COL_RI_UNIT, p[2]);
            db.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, iv);
        }
    }
}
