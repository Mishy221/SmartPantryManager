package com.smartpantry.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/** Screen 2: add a new ingredient, or edit an existing one when EXTRA_ITEM_ID is passed in the Intent. */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";
    private static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs"};

    private DatabaseHelper db;
    private TextInputLayout tilName;
    private TextInputLayout tilQuantity;
    private TextInputEditText etName;
    private TextInputEditText etQuantity;
    private TextInputEditText etExpiry;
    private Spinner spUnit;
    private long editId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = new DatabaseHelper(this);
        tilName = findViewById(R.id.tilName);
        tilQuantity = findViewById(R.id.tilQuantity);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnClearExpiry = findViewById(R.id.btnClearExpiry);

        spUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UNITS));

        editId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1); // read data passed by the Intent
        if (editId != -1) {
            setTitle("Edit Ingredient");
            btnSave.setText("Update");
            PantryItem item = db.getPantryItem(editId);
            if (item != null) {
                etName.setText(item.getName());
                etQuantity.setText(IngredientMatcher.formatQty(item.getQuantity()));
                etExpiry.setText(item.getExpiry());
                spUnit.setSelection(indexOfUnit(item.getUnit()));
            }
        } else {
            setTitle("Add Ingredient");
        }

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnClearExpiry.setOnClickListener(v -> etExpiry.setText(""));
        btnSave.setOnClickListener(v -> save());
    }

    private int indexOfUnit(String unit) {
        String canonical = IngredientMatcher.canonicalUnit(unit);
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equals(canonical)) {
                return i;
            }
        }
        return 0;
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                etExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Validates the form; saves (insert or update) only when every field is valid. */
    private void save() {
        tilName.setError(null);
        tilQuantity.setError(null);

        String name = etName.getText() == null ? "" : etName.getText().toString().trim();
        String qtyText = etQuantity.getText() == null ? "" : etQuantity.getText().toString().trim().replace(',', '.');
        boolean valid = true;

        if (name.isEmpty()) {
            tilName.setError("Please enter an ingredient name");
            valid = false;
        } else if (!name.matches(".*[A-Za-z].*")) {
            tilName.setError("The name must contain letters");
            valid = false;
        }

        double qty = 0;
        if (qtyText.isEmpty()) {
            tilQuantity.setError("Please enter a quantity");
            valid = false;
        } else {
            try {
                qty = Double.parseDouble(qtyText);
                if (qty <= 0) {
                    tilQuantity.setError("Quantity must be greater than 0");
                    valid = false;
                } else if (qty > 100000) {
                    tilQuantity.setError("Quantity is too large");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                tilQuantity.setError("Enter a valid number");
                valid = false;
            }
        }

        if (!valid) {
            return;
        }

        String unit = (String) spUnit.getSelectedItem();
        String expiry = etExpiry.getText() == null ? "" : etExpiry.getText().toString().trim();

        if (editId == -1) {
            db.insertPantryItem(new PantryItem(0, name, qty, unit, expiry));          // CREATE
            Toast.makeText(this, name + " added to pantry", Toast.LENGTH_SHORT).show();
        } else {
            db.updatePantryItem(new PantryItem(editId, name, qty, unit, expiry));     // UPDATE
            Toast.makeText(this, name + " updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
