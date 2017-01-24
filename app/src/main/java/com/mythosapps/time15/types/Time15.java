package com.mythosapps.time15.types;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Data structure for time hours in hours and minutes.
 */
public class Time15 {

    private static final DecimalFormat FORMAT = new DecimalFormat("#,00");

    private int totalMinutes;

    public Time15(int hours, int minutes) {
        // hours mit Vorzeichen
        // minutes ohne Vorzeichen
        totalMinutes = hours > 0 ? hours * 60 + minutes : hours * 60 - minutes;
    }

    public Time15(int totalMinutes) {
        // totalMinutes mit Vorzeichen
        this.totalMinutes = totalMinutes;
    }


    public int getHours() {
        return totalMinutes / 60;
    }

    public String getHoursDisplayString() {
        return formatWithTwoDigits(getHours());
    }

    public int getMinutes() {
        return Math.abs(totalMinutes % 60);
    }

    public String getMinutesDisplayString() {
        return formatWithTwoDigits(getMinutes());
    }

    public String toDisplayString() {
        return formatWithTwoDigits(getHours()) + ":" + formatWithTwoDigits(getMinutes());
    }

    public String toDisplayStringWithSign() {
        String s = "";
        if (totalMinutes > 0) {
            s += "+";
        } else if (totalMinutes < 0) {
            s += "-";
        }
        s += formatWithTwoDigits(getHours()) + ":" + formatWithTwoDigits(getMinutes());
        return s;
    }

    public static Time15 fromDisplayString(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        StringTokenizer t = new StringTokenizer(s, ":");

        if (t.countTokens() != 2) {
            throw new IllegalArgumentException("from DisplayString: param must be hh:mm");
        }
        Time15 result = null;
        try {
            int hours = Integer.valueOf(t.nextToken());
            int minutes = Integer.valueOf(t.nextToken());
            if (minutes < 0 || minutes > 59) {
                throw new IllegalArgumentException("fromDisplayString: param must be hh:mm with 0<=mm<60");
            }
            result = new Time15(hours, minutes);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("fromDisplayString: param must be hh:mm", e);
        }
        return result;
    }

    public static Time15 fromDecimalFormat(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        Time15 result = null;
        try {
            double dec = Double.parseDouble(s) * (double) 60;
            int totalMinutes = (int) Math.round(dec);
            result = Time15.fromMinutes(totalMinutes);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("fromDecimalFormat: param must be x.yy", e);
        }
        return result;
    }


    public String toDecimalFormat() {
        double d = (double) totalMinutes / (double) 60;
        return String.format(Locale.US, "%.2f", d);
    }

    public int toMinutes() {
        return totalMinutes;
    }

    public static Time15 fromMinutes(int totalMinutes) {
        return new Time15(totalMinutes);
    }

    private String formatWithTwoDigits(int difference) {
        String result = String.valueOf(Math.abs(difference));
        return result.length() < 2 ? "0" + result : result;
    }

    public void plus(Time15 toAdd) {
        totalMinutes += toAdd.toMinutes();
    }

    public void minus(int minutes) {
        totalMinutes -= minutes;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Time15) {
            Time15 t = (Time15) o;
            return totalMinutes == t.totalMinutes;
        }
        return false;
    }
}
