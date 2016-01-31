package com.mango_apps.time15.storage;

/**
 * Describes what kind of day is stored in a DaysData.
 */
public enum KindOfDay {

    WORKDAY("Arbeit"),

    HOLIDAY("Feiertag"),

    VACATION("Urlaub"),

    SICKDAY("Krank"),

    KIDSICKDAY("Kind krank"),

    WORKDAY_SOME_VACATION("Arbeit / Urlaub");

    private final String displayString;

    private KindOfDay(String displayString) {
        this.displayString = displayString;
    }

    public static String toggle(String value) {
        KindOfDay[] values = KindOfDay.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].toString().equals(value)) {
                return values[(i + 1) % values.length].toString();
            }
        }
        return null;
    }

    public static KindOfDay fromString(String value) {
        for (KindOfDay day : KindOfDay.values()) {
            if (day.toString().equals(value)) {
                return day;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name();
    }

    public String getDisplayString() {
        return displayString;
    }

    public static boolean isDueDay(KindOfDay day) {
        return KindOfDay.WORKDAY.equals(day) || KindOfDay.WORKDAY_SOME_VACATION.equals(day);
    }
}
