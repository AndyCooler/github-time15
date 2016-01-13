package com.mango_apps.time15.types;

/**
 * Data structure for time difference in hours and minutes.
 */
public class TimeDifference {

    private int difference;
    private int difference15;

    public TimeDifference(int difference, int difference15) {
        this.difference=difference;
        this.difference15 = difference15;
    }

    public int getDifference() {
        return difference;
    }

    public void setDifference(int difference) {
        this.difference = difference;
    }

    public int getDifference15() {
        return difference15;
    }

    public void setDifference15(int difference15) {
        this.difference15 = difference15;
    }

    public String toDisplayString() {
        return formatWithTwoDigits(difference) + ":" + formatWithTwoDigits(difference15);
    }

    public int toMinutes() {
        return difference * 60 + difference15;
    }

    public static TimeDifference fromMinutes(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return new TimeDifference(hours, minutes);
    }

    private String formatWithTwoDigits(int difference) {
        String result = String.valueOf(difference);
        return result.length() < 2 ? "0" + result : result;
    }
}
