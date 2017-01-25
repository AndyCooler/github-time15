package com.mythosapps.time15;

import com.mythosapps.time15.types.BeginEndTask;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.types.Time15;

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

    public void testDifferenceTwoTasks() {
        DaysDataNew data = new DaysDataNew("ID");

        BeginEndTask task0 = new BeginEndTask();
        task0.setKindOfDay(KindOfDay.WORKDAY);
        task0.setBegin(10);
        task0.setBegin15(0);
        task0.setEnd(17);
        task0.setEnd15(0);
        data.addTask(task0);

        BeginEndTask task1 = new BeginEndTask();
        task1.setKindOfDay(KindOfDay.VACATION);
        task1.setTotal(Time15.fromMinutes(4 * 60));
        data.addTask(task1);

        Time15 total0 = task0.getTotal();
        assertEquals(7, total0.getHours());
        assertEquals(0, total0.getMinutes());

        Time15 total1 = task1.getTotal();
        assertEquals(4, total1.getHours());
        assertEquals(0, total1.getMinutes());


        Time15 total = data.getTotal();
        assertEquals(11, total.getHours());
        assertEquals(0, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(180, balance);
    }

    public void testDifferenceTwoTasksBeginEnd() {
        DaysDataNew data = new DaysDataNew("ID");

        BeginEndTask task0 = new BeginEndTask();
        task0.setKindOfDay(KindOfDay.WORKDAY);
        task0.setBegin(10);
        task0.setBegin15(0);
        task0.setEnd(17);
        task0.setEnd15(0);
        data.addTask(task0);

        BeginEndTask task1 = new BeginEndTask();
        task1.setKindOfDay(KindOfDay.WORKDAY);
        task1.setBegin(10);
        task1.setBegin15(0);
        task1.setEnd(18);
        task1.setEnd15(30);
        data.addTask(task1);

        Time15 total0 = task0.getTotal();
        assertEquals(7, total0.getHours());
        assertEquals(0, total0.getMinutes());

        Time15 total1 = task1.getTotal();
        assertEquals(8, total1.getHours());
        assertEquals(30, total1.getMinutes());

        Time15 total = data.getTotal();
        assertEquals(15, total.getHours());
        assertEquals(30, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(450, balance); // +7:30h => +450min

        DaysDataNew copy = data.copy(data);
        total = copy.getTotal();
        assertEquals(15, total.getHours());
        assertEquals(30, total.getMinutes());

        balance = copy.getBalance();
        assertEquals(450, balance); // +7:30h => +450min

    }

    public void testDifferenceTwoDays() {
        DaysDataNew data1 = new DaysDataNew("ID");

        BeginEndTask task0 = new BeginEndTask();
        task0.setKindOfDay(KindOfDay.VACATION);
        data1.addTask(task0);

        DaysDataNew data2 = new DaysDataNew("ID2");
        BeginEndTask task02 = new BeginEndTask();
        task02.setKindOfDay(KindOfDay.WORKDAY);
        task02.setBegin(10);
        task02.setBegin15(0);
        task02.setEnd(16);
        task02.setEnd15(0);
        data2.addTask(task02);
        BeginEndTask task12 = new BeginEndTask();
        task12.setKindOfDay(KindOfDay.VACATION);
        task12.setTotal(Time15.fromMinutes(4 * 60));
        data2.addTask(task12);

        Time15 total0 = task0.getTotal();
        assertEquals(8, total0.getHours());
        assertEquals(0, total0.getMinutes());

        Time15 total02 = task02.getTotal();
        assertEquals(6, total02.getHours());
        assertEquals(0, total02.getMinutes());

        Time15 total12 = task12.getTotal();
        assertEquals(4, total12.getHours());
        assertEquals(0, total12.getMinutes());

        int balance1 = data1.getBalance();
        assertEquals(0, balance1); // VACATION => 0

        int balance2 = data2.getBalance();
        assertEquals(120, balance2); // 6 + 4 = 10 => +2h (120min)

    }

    public void testDeleteTask() {

        DaysDataNew data2 = new DaysDataNew("ID2");
        BeginEndTask task02 = new BeginEndTask();
        task02.setKindOfDay(KindOfDay.WORKDAY);
        task02.setBegin(10);
        task02.setBegin15(0);
        task02.setEnd(16);
        task02.setEnd15(0);
        data2.addTask(task02);
        BeginEndTask task12 = new BeginEndTask();
        task12.setKindOfDay(KindOfDay.VACATION);
        task12.setTotal(Time15.fromMinutes(4 * 60));
        data2.addTask(task12);

        data2.deleteTask(task02);
        assertEquals(1, data2.getNumberOfTasks());
        assertNotNull(data2.getTask(0));
        assertNull(data2.getTask(1));

        Time15 total2 = data2.getTotal();
        assertEquals(4, total2.getHours());
        assertEquals(0, total2.getMinutes());

        int balance2 = data2.getBalance();
        assertEquals(-240, balance2); // 4h => Balance -4h (-240min)
    }

    public void testTotalAndBalanceVacation() {
        DaysDataNew data = new DaysDataNew("ID");

        BeginEndTask task0 = new BeginEndTask();
        task0.setKindOfDay(KindOfDay.VACATION);
        data.addTask(task0);

        Time15 total0 = task0.getTotal();
        assertEquals(8, total0.getHours());
        assertEquals(0, total0.getMinutes());

        int balance = data.getBalance();
        assertEquals(0, balance);
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

        Time15 total = data.getTotal(); // incomplete selection has total 0
        assertEquals(0, total.getHours());
        assertEquals(0, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(0, balance); // incomplete selection has balance 0
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

        Time15 total = data.getTotal(); // incomplete selection has total 0
        assertEquals(0, total.getHours());
        assertEquals(0, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(0, balance); // incomplete selection has balance 0
    }

    public void testDifference() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task0 = new BeginEndTask();
        data.addTask(task0);
        task0.setKindOfDay(KindOfDay.WORKDAY);
        task0.setBegin(10);
        task0.setBegin15(15);
        task0.setEnd(16);
        task0.setEnd15(0);
        task0.setPause(30);
        BeginEndTask task1 = new BeginEndTask();
        task1.setKindOfDay(KindOfDay.VACATION);
        task1.setTotal(Time15.fromMinutes(4 * 60));
        data.addTask(task1);

        Time15 total = data.getTotal();
        assertEquals(9, total.getHours());
        assertEquals(15, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(75, balance); // 5.75h - 0.5h pause + 4h extra task = 9.25h d.h. +1.25h = +75 min
    }

    public void testNoTasks() {
        DaysDataNew data = new DaysDataNew("ID");

        Time15 total = data.getTotal();
        assertEquals(0, total.getHours());
        assertEquals(0, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(0, balance);
    }

    public void testHoliday() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.HOLIDAY);

        Time15 total = data.getTotal();
        assertEquals(8, total.getHours());
        assertEquals(0, total.getMinutes());

        int balance = data.getBalance();
        assertEquals(0, balance);
    }

}
