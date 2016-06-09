package com.mango_apps.time15.types;

import java.text.DecimalFormat;

/**
 * Data structure for time hours in hours and minutes.
 */
public class Time15 {

    private static final DecimalFormat FORMAT = new DecimalFormat("##,##");

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

    public int getMinutes() {
        return Math.abs(totalMinutes % 60);
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

    public String toDecimalFormat() {

        double hours = totalMinutes / 60;
        return FORMAT.format(hours);
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
}
