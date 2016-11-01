package com.mango_apps.time15;

import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.types.NumberTask;
import com.mango_apps.time15.types.Time15;

import junit.framework.TestCase;

//import org.junit.Test;

/**
 * Created by andreas on 04.01.16.
 */


public class DaysDataTest extends TestCase {

    public void testToFromString() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.WORKDAY);
        task.setBegin(10);
        task.setBegin15(15);
        task.setEnd(16);
        task.setEnd15(45);
        task.setPause(60);

        String s = data.toString();
        assertEquals("ID#WORKDAY#10#15#16#45#60", s);

        DaysDataNew copy = DaysDataNew.fromString(s);
        assertEquals("ID", copy.getId());
        BeginEndTask copyTask = (BeginEndTask) copy.getTask(0);
        assertEquals(new Integer(10), copyTask.getBegin());
        assertEquals(new Integer(15), copyTask.getBegin15());
        assertEquals(new Integer(16), copyTask.getEnd());
        assertEquals(new Integer(45), copyTask.getEnd15());
        assertEquals(new Integer(60), copyTask.getPause());
        assertEquals(KindOfDay.WORKDAY, copyTask.getKindOfDay());

        assertTrue(copyTask.isComplete());
        // Task 1: -150 min == -2h 30 min
        assertEquals(new Time15(-2, 30).toMinutes(), data.getBalance());
    }

    public void testToFromStringPauseNull() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.KIDSICKDAY);
        task.setBegin(10);
        task.setBegin15(15);
        task.setEnd(16);
        task.setEnd15(45);
        task.setPause(null);

        String s = data.toString();
        assertEquals("ID#KIDSICKDAY#10#15#16#45#-", s);

        DaysDataNew copy = DaysDataNew.fromString(s);
        assertEquals("ID", copy.getId());
        BeginEndTask copyTask = (BeginEndTask) copy.getTask(0);
        assertEquals(new Integer(10), copyTask.getBegin());
        assertEquals(new Integer(15), copyTask.getBegin15());
        assertEquals(new Integer(16), copyTask.getEnd());
        assertEquals(new Integer(45), copyTask.getEnd15());
        assertEquals(null, copyTask.getPause());
        assertEquals(KindOfDay.KIDSICKDAY, copyTask.getKindOfDay());
        assertTrue(copyTask.isComplete());
    }

    public void testToFromStringCombinationWorkdayVacationPauseNull() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.WORKDAY);
        task.setBegin(10);
        task.setBegin15(15);
        task.setEnd(16);
        task.setEnd15(45);
        task.setPause(null);

        NumberTask otherTask = new NumberTask();
        otherTask.setKindOfDay(KindOfDay.VACATION);
        otherTask.setTotal(new Time15(4, 0));
        data.addTask(otherTask);

        String s = data.toString();
        assertEquals("ID#WORKDAY#10#15#16#45#-#VACATION#4", s);

        DaysDataNew copy = DaysDataNew.fromString(s);
        assertEquals("ID", copy.getId());
        BeginEndTask copyTask = (BeginEndTask) copy.getTask(0);
        assertEquals(new Integer(10), copyTask.getBegin());
        assertEquals(new Integer(15), copyTask.getBegin15());
        assertEquals(new Integer(16), copyTask.getEnd());
        assertEquals(new Integer(45), copyTask.getEnd15());
        assertEquals(null, copyTask.getPause());
        assertEquals(KindOfDay.WORKDAY, copyTask.getKindOfDay());

        NumberTask copyNumberTask = (NumberTask) copy.getTask(1);
        assertEquals(KindOfDay.VACATION, copyNumberTask.getKindOfDay());
        assertEquals(4, copyNumberTask.getTotal().getHours());
        assertEquals(0, copyNumberTask.getTotal().getMinutes());

        assertTrue(copyTask.isComplete());
        assertTrue(copyNumberTask.isComplete());
        // Task 1: -90 min, Task 2: +240 min == +150 min == +2h 30 min
        assertEquals(new Time15(2, 30).toMinutes(), copy.getBalance());
    }

    public void testToFromStringIncomplete() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.WORKDAY);
        task.setBegin(10);
        task.setBegin15(15);
        task.setEnd(null); // incomplete!
        task.setEnd15(45);
        task.setPause(60);

        String s = data.toString();
        assertEquals("ID#WORKDAY#10#15#-#45#60", s);

        DaysDataNew copy = DaysDataNew.fromString(s);
        assertEquals("ID", copy.getId());
        BeginEndTask copyTask = (BeginEndTask) copy.getTask(0);
        assertEquals(new Integer(10), copyTask.getBegin());
        assertEquals(new Integer(15), copyTask.getBegin15());
        assertEquals(null, copyTask.getEnd());
        assertEquals(new Integer(45), copyTask.getEnd15());
        assertEquals(new Integer(60), copyTask.getPause());
        assertEquals(KindOfDay.WORKDAY, copyTask.getKindOfDay());

        assertFalse(copyTask.isComplete());

        // Task 1: -150 min == -2h 30 min
        assertEquals(0, data.getBalance());
    }

    public void testFromStringWithIncompleteTask() {

        String s = "ID#WORKDAY#10#15#-####45#m60";

        DaysDataNew copy = DaysDataNew.fromString(s);
        assertEquals("ID", copy.getId());
        BeginEndTask copyTask = (BeginEndTask) copy.getTask(0);
        assertEquals(new Integer(10), copyTask.getBegin());
        assertEquals(new Integer(15), copyTask.getBegin15());
        assertEquals(null, copyTask.getEnd());
        assertEquals(new Integer(45), copyTask.getEnd15());
        assertEquals(null, copyTask.getPause());
        assertEquals(KindOfDay.WORKDAY, copyTask.getKindOfDay());

        assertFalse(copyTask.isComplete());

        // Task 1: -150 min == -2h 30 min
        assertEquals(0, copy.getBalance());
    }
}
