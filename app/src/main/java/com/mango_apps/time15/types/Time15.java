package com.mango_apps.time15.types;

/**
 * Data structure for time hours in hours and minutes.
 */
public class Time15 {

    private int hours;
    private int minutes;

    public Time15(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String toDisplayString() {
        return formatWithTwoDigits(hours) + ":" + formatWithTwoDigits(minutes);
    }

    public int toMinutes() {
        return hours * 60 + minutes;
    }

    public static Time15 fromMinutes(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = Math.abs(totalMinutes % 60);
        return new Time15(hours, minutes);
    }

    private String formatWithTwoDigits(int difference) {
        String result = String.valueOf(difference);
        return result.length() < 2 ? "0" + result : result;
    }
}
