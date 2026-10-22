package com.smartpantry.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Local SQLite database (via SQLiteOpenHelper).
 * Tables: pantry (CRUD by the user), recipes + recipe_ingredients (seeded on first run).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // pantry table
    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_P_ID = "_id";
    public static final String COL_P_NAME = "name";
    public static final String COL_P_QTY = "quantity";
    public static final String COL_P_UNIT = "unit";
    public static final String COL_P_EXPIRY = "expiry";

    // recipes table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_R_ID = "_id";
    public static final String COL_R_NAME = "name";
    public static final String COL_R_STEPS = "steps";

    // recipe_ingredients table
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "_id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "name";
    public static final String COL_RI_QTY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " ("
                + COL_P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_P_NAME + " TEXT NOT NULL, "
                + COL_P_QTY + " REAL NOT NULL, "
                + COL_P_UNIT + " TEXT NOT NULL, "
                + COL_P_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
                + COL_R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_R_NAME + " TEXT NOT NULL, "
                + COL_R_STEPS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RI_RECIPE_ID + " INTEGER NOT NULL, "
                + COL_RI_NAME + " TEXT NOT NULL, "
                + COL_RI_QTY + " REAL NOT NULL, "
                + COL_RI_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_R_ID + ") ON DELETE CASCADE)");

        RecipeSeeder.seed(db); // pre-load the recipe collection on first run
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ------------------------------------------------------------------ CREATE

    public long insertPantryItem(PantryItem item) {
        return getWritableDatabase().insert(TABLE_PANTRY, null, toValues(item));
    }

    // ------------------------------------------------------------------ READ

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(TABLE_PANTRY, null, null, null, null, null,
                COL_P_NAME + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                list.add(fromCursor(c));
            }
        }
        return list;
    }

    public PantryItem getPantryItem(long id) {
        try (Cursor c = getReadableDatabase().query(TABLE_PANTRY, null, COL_P_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? fromCursor(c) : null;
        }
    }

    // ------------------------------------------------------------------ UPDATE

    public int updatePantryItem(PantryItem item) {
        return getWritableDatabase().update(TABLE_PANTRY, toValues(item), COL_P_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    // ------------------------------------------------------------------ DELETE

    public int deletePantryItem(long id) {
        return getWritableDatabase().delete(TABLE_PANTRY, COL_P_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteAllPantryItems() {
        getWritableDatabase().delete(TABLE_PANTRY, null, null);
    }

    // ------------------------------------------------------------------ recipes (read only)

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_RECIPES, null, null, null, null, null, COL_R_NAME + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                Recipe r = new Recipe(c.getLong(c.getColumnIndexOrThrow(COL_R_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_R_NAME)),
                        c.getString(c.getColumnIndexOrThrow(COL_R_STEPS)));
                loadIngredients(db, r);
                list.add(r);
            }
        }
        return list;
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_RECIPES, null, COL_R_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null)) {
            if (!c.moveToFirst()) {
                return null;
            }
            Recipe r = new Recipe(c.getLong(c.getColumnIndexOrThrow(COL_R_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_R_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_R_STEPS)));
            loadIngredients(db, r);
            return r;
        }
    }

    private void loadIngredients(SQLiteDatabase db, Recipe recipe) {
        try (Cursor c = db.query(TABLE_RECIPE_INGREDIENTS, null, COL_RI_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipe.getId())}, null, null, COL_RI_ID + " ASC")) {
            while (c.moveToNext()) {
                recipe.getIngredients().add(new RecipeIngredient(
                        c.getString(c.getColumnIndexOrThrow(COL_RI_NAME)),
                        c.getDouble(c.getColumnIndexOrThrow(COL_RI_QTY)),
                        c.getString(c.getColumnIndexOrThrow(COL_RI_UNIT))));
            }
        }
    }

    // ------------------------------------------------------------------ helpers

    private ContentValues toValues(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put(COL_P_NAME, item.getName());
        v.put(COL_P_QTY, item.getQuantity());
        v.put(COL_P_UNIT, item.getUnit());
        v.put(COL_P_EXPIRY, item.getExpiry());
        return v;
    }

    private PantryItem fromCursor(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow(COL_P_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_P_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(COL_P_QTY)),
                c.getString(c.getColumnIndexOrThrow(COL_P_UNIT)),
                c.getString(c.getColumnIndexOrThrow(COL_P_EXPIRY)));
    }
}
