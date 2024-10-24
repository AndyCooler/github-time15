package com.mythosapps.time15.types;

import android.app.Activity;
import android.util.Log;

import com.mythosapps.time15.storage.ConfigStorageFacade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Describes what kind of day is stored in a DaysData.
 */
public class KindOfDay {

    // TODO static method to return "initial"/default task used in modelToView initially
    // --> This way, a check for initial state of UI is easy

    // Idea is: Due hours per day is one central value (in future configurable) for all tasks.
    public static final int DEFAULT_DUE_TIME_PER_DAY_IN_MINUTES = 8 * 60;

    public static final int DEFAULT_DUE_TIME_PER_DAY_IN_HOURS = 8;

    // Constants for testing
    public static final String DEFAULT_WORK = "Arbeit";

    public static final String DEFAULT_HOLIDAY = "Feiertag";

    public static final String DEFAULT_VACATION = "Urlaub";

    public static final String DEFAULT_SICKDAY = "Krank";

    public static final String DEFAULT_KIDSICKDAY = "Kind krank";

    public static final String DEFAULT_PARENTAL_LEAVE = "Elternzeit";

    public static final List<KindOfDay> list = new ArrayList<>();

    public static final Set<String> listNames = new HashSet<>();

    // Constants for testing
    public static final KindOfDay WORKDAY = new KindOfDay(DEFAULT_WORK, ColorsUI.DARK_GREEN_SAVE_SUCCESS, true);

    public static final KindOfDay HOLIDAY = new KindOfDay(DEFAULT_HOLIDAY, ColorsUI.DARK_GREEN, false);

    public static final KindOfDay VACATION = new KindOfDay(DEFAULT_VACATION, ColorsUI.DARK_GREEN, false);

    public static final KindOfDay SICKDAY = new KindOfDay(DEFAULT_SICKDAY, ColorsUI.DARK_BLUE_DEFAULT, false);

    public static final KindOfDay KIDSICKDAY = new KindOfDay(DEFAULT_KIDSICKDAY, ColorsUI.DARK_BLUE_DEFAULT, false);

    public static final KindOfDay PARENTAL_LEAVE = new KindOfDay(DEFAULT_PARENTAL_LEAVE, ColorsUI.DARK_GREEN, false);

    public static final KindOfDay TEST_NEW = new KindOfDay("TestNew", ColorsUI.DARK_GREEN, false);


    public static void initializeFromConfig(ConfigStorageFacade configStorage, Activity activity) {
        addTaskTypes(configStorage.loadConfigXml(activity));
    }

    public static boolean saveToExternalConfig(ConfigStorageFacade configStorage, Activity activity) {
        return configStorage.saveExternalConfigXml(activity, list);
    }

    public static void initializeForTests() {
        list.clear();
        listNames.clear();
        // TODO initialize from asset and assign CONSTANTS WORKDAY = assets(1), VACATION = assets(2) ..
        list.add(WORKDAY);
        list.add(VACATION);
        list.add(HOLIDAY);
        list.add(KIDSICKDAY);
        list.add(SICKDAY);
        list.add(PARENTAL_LEAVE);
    }

    public static void addTaskTypes(List<KindOfDay> types) {
        for (KindOfDay task : types) {
            addTaskType(task);
        }
    }

    public static void addTaskType(KindOfDay task) {
        if (!listNames.contains(task.getDisplayString())) {
            list.add(task);
            listNames.add(task.getDisplayString());
            Log.i(KindOfDay.class.getName(), "Added task " + task.getDisplayString() + ".");
        } else {
            //Log.i(KindOfDay.class.getName(), "Skipped task " + task.getDisplayString() + ".");
        }
    }

    public static void replaceTaskType(KindOfDay task) {
        if (listNames.contains(task.getDisplayString())) {
            // cant use #remove cause remove uses equals which respects the color of the task
            boolean result0 = removeByName(task.getDisplayString());
            boolean result1 = listNames.remove(task.getDisplayString());
            Log.i(KindOfDay.class.getName(), "Replacing task " + task.getDisplayString() + ": " + result0 + "," + result1);
            if (result0 && result1) {
                addTaskType(task);
            }
        } else {
            Log.i(KindOfDay.class.getName(), "Replace task " + task.getDisplayString() + ": task not found.");
        }
    }

    private static boolean removeByName(String displayString) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDisplayString().equals(displayString)) {
                index = i;
            }
        }
        if (index >= 0) {
            list.remove(index);
            return true;
        }
        return false;
    }

    private String displayString;

    private int color;

    private boolean beginEndType;

    public KindOfDay(String displayString, int color, boolean beginEndType) {
        this.displayString = displayString;
        this.color = color;
        this.beginEndType = beginEndType;
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

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof KindOfDay) {
            KindOfDay other = (KindOfDay) o;
            return other.beginEndType == beginEndType &&
                    other.color == color &&
                    ((null == other.displayString && null == displayString) ||
                            (null != other.displayString && other.displayString.equals(displayString)) ||
                            (null != displayString && displayString.equals(other.displayString)));
            //Objects.equals(other.displayString, displayString); // requires higher API level
        }
        return false;
    }

    @Override
    public int hashCode() {
        int x = beginEndType ? 3 : 5;
        int y = displayString == null ? 7 : displayString.hashCode();
        return 11 * color * x * y;
    }

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
        return DEFAULT_DUE_TIME_PER_DAY_IN_MINUTES;
    }

    public static boolean isBeginEndType(String kindOfDay) {
        KindOfDay day = fromString(kindOfDay);
        return day == null ? true : day.isBeginEndType();
    }

    public static KindOfDay convert(String displayString, Integer begin, Integer end) {
        Log.i(KindOfDay.class.getName(), "Converting task " + displayString + ".");
        KindOfDay newType = new KindOfDay(displayString, ColorsUI.DARK_GREEN_SAVE_SUCCESS, begin != null && end != null);
        addTaskType(newType);
        return newType;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setBeginEndType(boolean beginEndType) {
        this.beginEndType = beginEndType;
    }

    public String toXmlConfig() {
        return "    <task>\n" +
                "        <displayString>" + getDisplayString() + "</displayString>\n" +
                "        <color>" + getColor() + "</color>\n" +
                "        <beginEndType>" + isBeginEndType() + "</beginEndType>\n" +
                "    </task>\n";
    }

    public int index() {
        return list.indexOf(this);
    }

    public static List<String> dataList() {
        ArrayList<String> result = new ArrayList<>();
        for (KindOfDay task : list) {
            result.add(task.getDisplayString());
        }
        return result;
    }
}
