package com.mythosapps.time15.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public final class BuchungUtil {

    static final NumberFormat form = NumberFormat.getCurrencyInstance(Locale.getDefault());
    static final String currency = "EUR";

    public static String getNettoForDisplay(double rate, int billableMinutes) {
        form.setCurrency(Currency.getInstance(currency));
        return form.format(round(rate * billableMinutes / 60, 2));
    }

    public static String getTaxForDisplay(double rate, int billableMinutes) {
        form.setCurrency(Currency.getInstance(currency));
        return form.format(round(19 * rate * billableMinutes / 60 / 100, 2));
    }

    public static String getBruttoForDisplay(double rate, int billableMinutes) {
        form.setCurrency(Currency.getInstance(currency));
        return form.format(round(119 * rate * billableMinutes / 60 / 100, 2));
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
