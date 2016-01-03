package com.mango_apps.time15.storage;

/**
 * Describes what kind of day is stored in a DaysData.
 */
public enum KindOfDay {

    WORKDAY,

    HOLIDAY,

    VACATIONDAY,

    SICKDAY,

    CHILDCAREDAY;

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
}
