package com.mango_apps.time15;

import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.types.Time15;

import junit.framework.TestCase;

/**
 * Created by andreas on 12.01.16.
 */
public class DaysDataTotalAndBalanceTest extends TestCase {

    public void testDifferenceWithPause() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.WORKDAY);
        task.setBegin(10);
        task.setBegin15(15);
        task.setEnd(16);
        task.setEnd15(45);
        task.setPause(60);

        Time15 total = data.getTotal();
        assertEquals(5, total.getHours());
        assertEquals(30, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(-150, balance);
    }

    public void testDifferenceWithoutPause() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.WORKDAY);
        task.setBegin(10);
        task.setBegin15(15);
        task.setEnd(16);
        task.setEnd15(45);
        task.setPause(null);

        Time15 total = data.getTotal();
        assertEquals(6, total.getHours());
        assertEquals(30, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(-90, balance);
    }

    public void testDifferenceWithout15s() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.WORKDAY);
        task.setBegin(10);
        task.setBegin15(null);
        task.setEnd(16);
        task.setEnd15(null);
        task.setPause(null);

        Time15 total = data.getTotal();
        assertEquals(6, total.getHours());
        assertEquals(0, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(-120, balance); // 6h - 8h = -2h  d.h. -120
    }

    public void testDifferenceIncomplete() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.WORKDAY);
        task.setBegin(10);
        task.setBegin15(null);
        task.setEnd(null);
        task.setEnd15(null);
        task.setPause(null);

        Time15 total = data.getTotal();
        assertEquals(0, total.getHours());
        assertEquals(0, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(-480, balance); // soll: 480, ist: 0 d.h. balance = -480
    }

    public void testDifferenceIncomplete15() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.WORKDAY_SOME_VACATION);
        task.setBegin(10);
        task.setBegin15(null);
        task.setEnd(16);
        task.setEnd15(0);
        task.setPause(null);

        Time15 total = data.getTotal();
        assertEquals(6, total.getHours());
        assertEquals(0, total.getMinutes());

        int balance = data.getBalance(); // TODO wird nur temporaer unterstuetzt!
        // TODO wenn WORKDAY_SOME_VACATION wegfaellt, muss hier ein 2. Task (NumberTask +4h) dazu!
        assertEquals(120, balance); // 6h + 4h other hours = 10h d.h. +2h = 120 min
    }

    public void testHoliday() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.HOLIDAY);

        Time15 total = data.getTotal();
        assertEquals(0, total.getHours());
        assertEquals(0, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(0, balance);
    }

}
