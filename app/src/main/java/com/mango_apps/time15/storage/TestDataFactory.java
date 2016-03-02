package com.mango_apps.time15.storage;

import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.types.KindOfDay;

import java.util.Random;

/**
 * Created by andreas on 02.03.16.
 */
public class TestDataFactory {

    public static final Random RANDOM = new Random();

    public static DaysData newRandom(String id) {
        DaysData data1 = new DaysData(id);
        switch (RANDOM.nextInt(3)) {
            case 0:
                data1.setDay(KindOfDay.WORKDAY);
                data1.setBegin(8);
                data1.setBegin15(0);
                data1.setEnd(16);
                data1.setEnd15(0);
                break;
            case 1:
                data1.setDay(KindOfDay.WORKDAY_SOME_VACATION);
                data1.setBegin(11);
                data1.setBegin15(0);
                data1.setEnd(16);
                data1.setEnd15(30);
                data1.setOtherHours(4);
                break;
            case 2:
                data1.setDay(KindOfDay.HOLIDAY);
                break;
            case 3:
                data1.setDay(KindOfDay.SICKDAY);
                break;
        }

        return data1;
    }
}
