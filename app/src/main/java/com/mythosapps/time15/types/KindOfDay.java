package com.mythosapps.time15.types;

/**
 * Describes what kind of day is stored in a DaysData.
 */
public enum KindOfDay {

    WORKDAY("Arbeit"),

    HOLIDAY("Feiertag"),

    VACATION("Urlaub"),

    SICKDAY("Krank"),

    KIDSICKDAY("Kind krank"),

    PARENTAL_LEAVE("Elternzeit");

    private final String displayString;

    KindOfDay(String displayString) {
        this.displayString = displayString;
    }

    public static String toggle(String value) {
        KindOfDay[] values = KindOfDay.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].toString().equals(value)) {
                KindOfDay result = values[(i + 1) % values.length];
                return result.toString();
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

    public static boolean isBeginEndType(String day) {
        return KindOfDay.WORKDAY.equals(KindOfDay.fromString(day));
    }

    public static boolean isBeginEndType(KindOfDay day) {
        return KindOfDay.WORKDAY.equals(day);
    }
}
