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

        if (begin == null || end == null) {
            return Time15.fromMinutes(0);
        }

        Time15 beginTime = new Time15(begin, begin15 == null ? 0 : begin15);
        Time15 endTime = new Time15(end, end15 == null ? 0 : end15);

        if (beginTime.toMinutes() > endTime.toMinutes()) {
            throw new IllegalStateException("Begin time must be before end time!");
        }

        endTime.minus(beginTime.toMinutes());
        endTime.minus(pause == null ? 0 : pause);

        if (endTime.toMinutes() < 0) {
            throw new IllegalStateException("Negative time difference!");
        }

        return endTime;
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
