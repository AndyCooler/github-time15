package com.mythosapps.time15.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes what kind of day is stored in a DaysData.
 */
public class KindOfDay {

    // Constants for testing
    public static final String DEFAULT_WORK = "Arbeit";

    public static final String DEFAULT_HOLIDAY = "Feiertag";

    public static final String DEFAULT_VACATION = "Urlaub";

    public static final String DEFAULT_SICKDAY = "Krank";

    public static final String DEFAULT_KIDSICKDAY = "Kind krank";

    public static final String DEFAULT_PARENTAL_LEAVE = "Elternzeit";

    public static final List<KindOfDay> list = new ArrayList<>();

    // Constants for testing
    public static final KindOfDay WORKDAY = new KindOfDay(DEFAULT_WORK, ColorsUI.DARK_BLUE_DEFAULT, 8 * 60, true);

    public static final KindOfDay HOLIDAY = new KindOfDay(DEFAULT_HOLIDAY, ColorsUI.DARK_GREEN_SAVE_SUCCESS, 8 * 60, false);

    public static final KindOfDay VACATION = new KindOfDay(DEFAULT_VACATION, ColorsUI.DARK_GREEN_SAVE_SUCCESS, 8 * 60, false);

    public static final KindOfDay SICKDAY = new KindOfDay(DEFAULT_SICKDAY, ColorsUI.DARK_GREY_SAVE_ERROR, 8 * 60, false);

    public static final KindOfDay KIDSICKDAY = new KindOfDay(DEFAULT_KIDSICKDAY, ColorsUI.DARK_GREY_SAVE_ERROR, 8 * 60, false);

    public static final KindOfDay PARENTAL_LEAVE = new KindOfDay(DEFAULT_PARENTAL_LEAVE, ColorsUI.DARK_GREEN_SAVE_SUCCESS, 8 * 60, false);

    static {
        // loaded from resource file
//        list.add(WORKDAY);
//        list.add(HOLIDAY);
//        list.add(VACATION);
//        list.add(SICKDAY);
//        list.add(KIDSICKDAY);
//        list.add(PARENTAL_LEAVE);
    }

    private String displayString;

    private int color;

    private int dueMinutes;

    private boolean beginEndType;

    private Time15 defaultDue;

    public KindOfDay(String displayString, int color, int dueMinutes, boolean beginEndType) {
        this.displayString = displayString;
        this.color = color;
        this.dueMinutes = dueMinutes;
        this.beginEndType = beginEndType;
        this.defaultDue = Time15.fromMinutes(dueMinutes);
    }

    public static KindOfDay toggle(String value) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDisplayString().equals(value)) {
                return list.get((i + 1) % list.size());
            }
        }
        return null;
    }

    public static KindOfDay fromString(String value) {
        for (KindOfDay day : list) {
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

    public boolean isBeginEndType() {
        return beginEndType;
    }

    public int getColor() {
        return color;
    }

    public int getDueMinutes() {
        return dueMinutes;
    }

    public static boolean isBeginEndType(String kindOfDay) {
        KindOfDay day = fromString(kindOfDay);
        return day.isBeginEndType();
    }

    public Time15 getDefaultDue() {
        return defaultDue;
    }
}
