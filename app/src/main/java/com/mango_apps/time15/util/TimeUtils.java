package com.mango_apps.time15.util;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * This class provides utilities for formatting operations for days.
 */
public final class TimeUtils {

    public static String createID() {
        return createID(new GregorianCalendar());
    }

    public static String createID(GregorianCalendar cal) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String date = df.format(cal.getTime());
        return date;
    }

    public static String getMonthYearOfID(String id) {
        return id.substring(6) + "_" + id.substring(3,5);
    }

    public static String dateForwards(String id) {
        GregorianCalendar cal = toCalendar(id);
        cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
        return createID(cal);
    }

    public static String dateBackwards(String id) {
        GregorianCalendar cal = toCalendar(id);
        cal.add(GregorianCalendar.DAY_OF_YEAR, -1);
        return createID(cal);
    }

    public static String dayOfWeek(String id) {
        GregorianCalendar cal = toCalendar(id);
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.GERMANY);
    }

    private static GregorianCalendar toCalendar(String id) {
        int year = Integer.valueOf(id.substring(6));
        int month = Integer.valueOf(id.substring(3,5)) - 1;
        int day = Integer.valueOf(id.substring(0,2));
        GregorianCalendar cal = new GregorianCalendar(year, month, day);

        return  cal;
    }

    public static List<String> getListOfIdsOfMonth(String id) {
        ArrayList<String> result = new ArrayList<String>();
        GregorianCalendar cal = toCalendar(id);
        for (int i = 1; i < 32; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            result.add(createID(cal));
        }

        return result;
    }
}
