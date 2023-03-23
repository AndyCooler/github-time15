package com.mythosapps.time15.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public final class BuchungUtil {

    static final NumberFormat form = NumberFormat.getCurrencyInstance(Locale.getDefault());
    static final String currency = "EUR";

    public static String getNettoForDisplay(double rate, int billableMinutes) {
        form.setCurrency(Currency.getInstance(currency));
        return form.format(rate * billableMinutes / 60);
    }

    public static String getTaxForDisplay(double rate, int billableMinutes) {
        form.setCurrency(Currency.getInstance(currency));
        return form.format(0.19 * rate * billableMinutes / 60);
    }

    public static String getBruttoForDisplay(double rate, int billableMinutes) {
        form.setCurrency(Currency.getInstance(currency));
        return form.format(1.19 * rate * billableMinutes / 60);
    }

}
