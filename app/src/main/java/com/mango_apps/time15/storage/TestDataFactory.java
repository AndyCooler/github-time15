package com.mango_apps.time15.storage;

import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.types.NumberTask;
import com.mango_apps.time15.types.Time15;

import java.util.Random;

/**
 * Created by andreas on 02.03.16.
 */
public class TestDataFactory {

    public static final Random RANDOM = new Random();

    public static DaysDataNew newRandom(String id) {
        DaysDataNew data1 = new DaysDataNew(id);
        BeginEndTask task0 = new BeginEndTask();
        switch (RANDOM.nextInt(4)) {
            case 0:
                task0.setKindOfDay(KindOfDay.WORKDAY);
                task0.setBegin(8);
                task0.setBegin15(0);
                task0.setEnd(16);
                task0.setEnd15(0);
                data1.addTask(task0);
                break;
            case 1:
                task0.setKindOfDay(KindOfDay.WORKDAY);
                task0.setBegin(11);
                task0.setBegin15(0);
                task0.setEnd(16);
                task0.setEnd15(30);
                data1.addTask(task0);
                NumberTask task1 = new NumberTask();
                task1.setKindOfDay(KindOfDay.VACATION);
                task1.setTotal(Time15.fromMinutes(4 * 60));
                data1.addTask(task1);
                break;
            case 2:
                task0.setKindOfDay(KindOfDay.VACATION);
                data1.addTask(task0);
                break;
            case 3:
                task0.setKindOfDay(KindOfDay.HOLIDAY);
                data1.addTask(task0);
                break;
            case 4:
                task0.setKindOfDay(KindOfDay.SICKDAY);
                data1.addTask(task0);
                break;

        }

        return data1;
    }
}
