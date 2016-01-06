package com.mango_apps.time15.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This class provides utilities for formatting operations for days.
 */
public final class TimeUtils {

    public static String createID() {
        Calendar cal = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String date = df.format(cal.getTime());
        return date;
    }

    public static String getMonthYearOfID(String id) {
        return id.substring(6) + "_" + id.substring(3,5);
    }
}
