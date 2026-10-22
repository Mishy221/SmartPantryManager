package com.smartpantry.app;

import android.content.Context;
import android.content.SharedPreferences;

/** Small helper around SharedPreferences for the Settings screen. */
public final class Prefs {
    private static final String FILE = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_SHOW_ALMOST = "show_almost";

    private Prefs() { }

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean expiryAlertsEnabled(Context c) {
        return sp(c).getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public static void setExpiryAlerts(Context c, boolean on) {
        sp(c).edit().putBoolean(KEY_EXPIRY_ALERTS, on).apply();
    }

    public static boolean showAlmostThere(Context c) {
        return sp(c).getBoolean(KEY_SHOW_ALMOST, false);
    }

    public static void setShowAlmostThere(Context c, boolean on) {
        sp(c).edit().putBoolean(KEY_SHOW_ALMOST, on).apply();
    }
}
