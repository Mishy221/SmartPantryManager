package com.smartpantry.app;

/** Model class: one ingredient the user currently has at home. */
public class PantryItem {
    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiry; // "yyyy-MM-dd" or empty string when no expiry date was set

    public PantryItem(long id, String name, double quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry == null ? "" : expiry;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getExpiry() { return expiry; }
}
