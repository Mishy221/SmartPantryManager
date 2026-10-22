package com.smartpantry.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Helpers for working with the "yyyy-MM-dd" expiry date strings. */
public final class ExpiryUtils {
    public static final int NO_EXPIRY = Integer.MAX_VALUE;

    private ExpiryUtils() { }

    /** Days from today until the expiry date (negative = already expired). NO_EXPIRY if none. */
    public static int daysUntil(String expiry) {
        if (expiry == null || expiry.isEmpty()) {
            return NO_EXPIRY;
        }
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            fmt.setLenient(false);
            Date target = fmt.parse(expiry);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            long diff = target.getTime() - today.getTimeInMillis();
            return (int) Math.round(diff / 86400000.0);
        } catch (ParseException e) {
            return NO_EXPIRY;
        }
    }

    /** Returns a date string "yyyy-MM-dd" for today plus the given number of days. */
    public static String datePlusDays(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);
        return String.format(Locale.US, "%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }
}
