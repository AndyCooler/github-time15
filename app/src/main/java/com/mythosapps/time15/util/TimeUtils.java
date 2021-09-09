package com.mythosapps.time15.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class provides utilities for formatting operations for days.
 */
public final class TimeUtils {

    private static final Map<String, GregorianCalendar> cache = new HashMap<>();

    public static String createID() {
        return createID(new GregorianCalendar());
    }

    public static String createID(GregorianCalendar cal) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault());
        String id = df.format(cal.getTime());
        cache.put(id, cal);
        return id;
    }

    public static String getMonthYearOfID(String id) {
        return id.substring(6) + "_" + id.substring(3, 5);
    }

    public static String getMonthYearDisplayString(String id) {
        GregorianCalendar cal = toCalendar(id);
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.GERMANY) + " " + cal.get(Calendar.YEAR);
    }

    public static String getMonthYearDisplayStringShort(String id) {
        GregorianCalendar cal = toCalendar(id);
        return id.substring(3, 5) + "/" + id.substring(6);
    }

    public static String getYearMonthDisplayStringShort(String id) {
        GregorianCalendar cal = toCalendar(id);
        return id.substring(6) + "/" + id.substring(3, 5);
    }

    public static String dateForwards(String id) {
        GregorianCalendar cal = toCalendar(id);
        cache.remove(id);
        cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
        return createID(cal);
    }

    public static String dateBackwards(String id) {
        GregorianCalendar cal = toCalendar(id);
        cache.remove(id);
        cal.add(GregorianCalendar.DAY_OF_YEAR, -1);
        return createID(cal);
    }

    public static String monthForwards(String id) {
        GregorianCalendar cal = toCalendar(id);
        cache.remove(id);
        cal.add(GregorianCalendar.MONTH, 1);
        return createID(cal);
    }

    public static String monthBackwards(String id) {
        GregorianCalendar cal = toCalendar(id);
        cache.remove(id);
        cal.add(GregorianCalendar.MONTH, -1);
        return createID(cal);
    }

    public static String yearForwards(String id) {
        GregorianCalendar cal = toCalendar(id);
        cache.remove(id);
        cal.add(GregorianCalendar.YEAR, 1);
        return createID(cal);
    }

    public static String yearBackwards(String id) {
        GregorianCalendar cal = toCalendar(id);
        cache.remove(id);
        cal.add(GregorianCalendar.YEAR, -1);
        return createID(cal);
    }

    public static String dayOfWeek(String id) {
        GregorianCalendar cal = toCalendar(id);
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.GERMANY);
    }

    private static GregorianCalendar toCalendar(String id) {
        GregorianCalendar cal = cache.get(id);
        if (cache.get(id) == null) { // cache miss
            int year = Integer.valueOf(id.substring(6));
            int month = Integer.valueOf(id.substring(3, 5)) - 1;
            int day = Integer.valueOf(id.substring(0, 2));
            cal = new GregorianCalendar(year, month, day);
        }
        return cal;
    }

    public static List<String> getListOfIdsOfMonth(String id) {
        ArrayList<String> result = new ArrayList<String>();
        GregorianCalendar cal = toCalendar(id);
        int max = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        for (int i = 1; i <= max; i++) {
            cal = (GregorianCalendar) cal.clone();
            cal.set(Calendar.DAY_OF_MONTH, i);
            result.add(createID(cal));
        }

        return result;
    }

    public static boolean isSameMonth(String idA, String idB) {
        if (idA == null || idB == null) {
            return false;
        }
        Calendar calA = toCalendar(idA);
        Calendar calB = toCalendar(idB);
        return calA.get(Calendar.MONTH) == calB.get(Calendar.MONTH) && calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR);
    }

    public static boolean isMonday(String id) {
        if (id == null) {
            return false;
        }
        GregorianCalendar cal = toCalendar(id);
        return 2 == cal.get(Calendar.DAY_OF_WEEK);
    }

    public static int getWeekOfYear(String id) {
        Calendar cal = toCalendar(id);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public static String getMainTitleString(String id) {
        return TimeUtils.dayOfWeek(id) + " " + id.substring(0, 6);
    }

    public static boolean isWeekend(String id) {
        Calendar cal = toCalendar(id);
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:
                return true;
        }
        return false;
    }

    public static boolean isLastWorkDayOfMonth(String id) {
        GregorianCalendar cal = toCalendar(id);
        int max = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        cal = (GregorianCalendar) cal.clone();
        cal.set(Calendar.DAY_OF_MONTH, max);
        String maxId = createID(cal);
        while (isWeekend(maxId)) {
            maxId = dateBackwards(maxId);
        }
        return id.equals(maxId);
    }

    public static String getYearDisplayString(String id) {
        GregorianCalendar cal = toCalendar(id);
        return "" + cal.get(Calendar.YEAR);
    }

    public static String getCalendarWeekDisplayString(String id) {
        // siehe https://stackoverflow.com/questions/4608470/why-dec-31-2010-returns-1-as-week-of-year
        // siehe https://www.java-forum.org/thema/problem-mit-erster-kalenderwoche.143817/
        GregorianCalendar cal = toCalendar(id);
        cal.setMinimalDaysInFirstWeek(4); // Donnerstag immer in erster Kalenderwoche in DE
        return "KW " + cal.get(Calendar.WEEK_OF_YEAR);
    }

    public static boolean isOkayToEdit(String id) {
        return createID().equals(id);
    }

    public static List<String> getListOfLastSevenDays() {
        ArrayList<String> result = new ArrayList<String>();
        GregorianCalendar cal = toCalendar(createID());

        for (int i = 1; i <= 7; i++) {
            cal = (GregorianCalendar) cal.clone();
            cal.add(Calendar.HOUR, -24);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                result.add(createID(cal));
            }
        }

        return result;
    }

    public static String createMoment() {
        return new Timestamp(System.currentTimeMillis()).toString().substring(0, 19).replaceAll(":", "-").replaceFirst(" ", "_");
    }
}
