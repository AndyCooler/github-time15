package com.mango_apps.time15.util;

import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.types.TimeDifference;

/**
 * This class provides utilities for tasks on day's data.
 */
public final class DaysDataUtils {

    public static TimeDifference calculateTotal(DaysData data) {
        return calculateTotal(data.getBegin(), data.getBegin15(), data.getEnd(), data.getEnd15(), data.getPause());
    }

    public static TimeDifference calculateTotal(Integer begin, Integer begin15, Integer end, Integer end15, Integer pause) {
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
        return new TimeDifference(difference, difference15);
    }


}
