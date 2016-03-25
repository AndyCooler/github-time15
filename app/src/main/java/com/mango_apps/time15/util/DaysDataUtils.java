package com.mango_apps.time15.util;

import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.types.Time15;

/**
 * This class provides utilities for tasks on day's data.
 */
public final class DaysDataUtils {

    public static final int DUE_HOURS_PER_DAY = 8;
    public static final int DUE_TOTAL_MINUTES = DUE_HOURS_PER_DAY * 60;

    public static Time15 calculateTotal(DaysData data) {
        return calculateTotal(data.getBegin(), data.getBegin15(), data.getEnd(), data.getEnd15(), data.getPause());
    }

    public static Time15 calculateTotal(Integer begin, Integer begin15, Integer end, Integer end15, Integer pause) {
        int difference = 0;
        int difference15 = 0;
        if (end != null && begin != null) {
            difference = end - begin;
            if (begin15 != null && end15 != null) {
                difference15 = end15 - begin15;
                if (difference15 < 0) {
                    difference--;
                    difference15 = 60 + difference15;
                }
            }
            if (pause != null) {
                int pauseTemp = pause;
                while (pauseTemp > 60) {
                    difference--;
                    pauseTemp -= 60;
                }
                difference15 -= pauseTemp;
                if (difference15 < 0) {
                    difference--;
                    difference15 = 60 + difference15;
                }
            }
        }
        return new Time15(difference, difference15);
    }


    /**
     * @param data
     * @return
     * @deprecated use DaysDataNew#getBalance()
     */
    @Deprecated
    public static int calculateBalance(DaysData data) {

        if (data == null || !KindOfDay.isDueDay(data.getDay())) {
            return 0;
        }

        int actualTotalMinutes = 0;

        Time15 actual = DaysDataUtils.calculateTotal(data);
        actualTotalMinutes += actual.toMinutes();
        if (KindOfDay.WORKDAY_SOME_VACATION.equals(data.getDay())) {
            actualTotalMinutes += data.getOtherHours() * 60;
        }
        return actualTotalMinutes - DUE_TOTAL_MINUTES;
    }
}
