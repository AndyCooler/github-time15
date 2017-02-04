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

    public static final String DEFAULT_WORK = "Arbeit";

    public static final String DEFAULT_HOLIDAY = "Feiertag";

    public static final String DEFAULT_VACATION = "Urlaub";

    public static final String DEFAULT_SICKDAY = "Krank";

    public static final String DEFAULT_KIDSICKDAY = "Kind krank";

    public static final String DEFAULT_PARENTAL_LEAVE = "Elternzeit";


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
            if (day.name().equals(value)) {
                return day;
            }
            // new: allow restore by display string
            if (day.getDisplayString().equals(value)) {
                return day;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayString;
    } // new: now returns display string

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
