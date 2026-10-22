package com.smartpantry.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

/** Screen 5: settings (stored with SharedPreferences) plus two pantry helper tools. */
public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        db = new DatabaseHelper(this);

        SwitchCompat switchExpiry = findViewById(R.id.switchExpiry);
        switchExpiry.setChecked(Prefs.expiryAlertsEnabled(this));
        switchExpiry.setOnCheckedChangeListener((button, checked) -> Prefs.setExpiryAlerts(this, checked));

        SwitchCompat switchAlmost = findViewById(R.id.switchAlmost);
        switchAlmost.setChecked(Prefs.showAlmostThere(this));
        switchAlmost.setOnCheckedChangeListener((button, checked) -> Prefs.setShowAlmostThere(this, checked));

        Button btnSample = findViewById(R.id.btnSample);
        btnSample.setOnClickListener(v -> addSampleIngredients());

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Clear pantry")
                .setMessage("Delete ALL ingredients from your pantry?")
                .setPositiveButton("Delete all", (dialog, which) -> {
                    db.deleteAllPantryItems();
                    Toast.makeText(this, "Pantry cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show());

        NavHelper.setup(this, R.id.nav_settings);
    }

    /** Handy for demos and testing: fills the pantry with a typical set of leftovers. */
    private void addSampleIngredients() {
        db.insertPantryItem(new PantryItem(0, "Eggs", 6, "pcs", ExpiryUtils.datePlusDays(2)));
        db.insertPantryItem(new PantryItem(0, "Bread", 8, "pcs", ""));
        db.insertPantryItem(new PantryItem(0, "Butter", 250, "g", ""));
        db.insertPantryItem(new PantryItem(0, "Cheese", 200, "g", ""));
        db.insertPantryItem(new PantryItem(0, "Milk", 1, "l", ExpiryUtils.datePlusDays(5)));
        db.insertPantryItem(new PantryItem(0, "Flour", 500, "g", ""));
        db.insertPantryItem(new PantryItem(0, "Sugar", 500, "g", ""));
        db.insertPantryItem(new PantryItem(0, "Tomatoes", 4, "pcs", ""));
        db.insertPantryItem(new PantryItem(0, "Onions", 3, "pcs", ""));
        db.insertPantryItem(new PantryItem(0, "Pasta", 500, "g", ""));
        db.insertPantryItem(new PantryItem(0, "Garlic", 6, "pcs", ""));
        db.insertPantryItem(new PantryItem(0, "Olive oil", 250, "ml", ""));
        Toast.makeText(this, "12 sample ingredients added", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
