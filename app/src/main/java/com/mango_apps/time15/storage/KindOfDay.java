package com.mango_apps.time15.storage;

/**
 * Describes what kind of day is stored in a DaysData.
 */
public enum KindOfDay {

    WORKDAY,

    HOLIDAY,

    VACATION,

    SICKDAY,

    KIDSICKDAY;

    @Override
    public String toString() {
        return name();
    }

    public static KindOfDay fromString(String value) {
        for (KindOfDay day : KindOfDay.values()) {
            if (day.toString().equals(value)) {
                return day;
            }
        }
        return null;
    }

    public static String toggle(String value) {
        KindOfDay[] values = KindOfDay.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].toString().equals(value)) {
                return values[(i+1)%values.length].toString();
            }
        }
        return null;
    }
}
