package com.mango_apps.time15;

import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.types.Time15;

import junit.framework.TestCase;

//import org.junit.Test;

/**
 * Created by andreas on 04.01.16.
 */


public class DaysDataTest extends TestCase {

    public void testToString() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.WORKDAY);
        task.setBegin(10);
        task.setBegin15(15);
        task.setEnd(16);
        task.setEnd15(45);
        task.setPause(60);
        task.setTotal(task.getTotal());

        String s = data.toString();
        assertEquals("ID#WORKDAY#10#15#16#45#60#5.50", s);
    }

    public void testFromString() {
        String s = "ID#WORKDAY#10#15#16#45#60#5.50";
        DaysDataNew copy = DaysDataNew.fromString(s);
        assertEquals("ID", copy.getId());
        BeginEndTask copyTask = (BeginEndTask) copy.getTask(0);
        assertEquals(KindOfDay.WORKDAY, copyTask.getKindOfDay());
        assertEquals(new Integer(10), copyTask.getBegin());
        assertEquals(new Integer(15), copyTask.getBegin15());
        assertEquals(new Integer(16), copyTask.getEnd());
        assertEquals(new Integer(45), copyTask.getEnd15());
        assertEquals(new Integer(60), copyTask.getPause());
        assertEquals(330, copyTask.getTotal().toMinutes());

        assertTrue(copyTask.isComplete());
        // Task 1: -150 min == -2h 30 min
        assertEquals(new Time15(-2, 30).toMinutes(), copy.getBalance());
    }

    public void testToStringNoTotal() {
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
        assertEquals("ID#WORKDAY#10#15#16#45#60#-", s);
    }

    public void testFromStringNoTotal() {
        String s = "ID#WORKDAY#10#15#16#45#60#-";
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
        assertEquals(new Time15(-2, 30).toMinutes(), copy.getBalance());
    }

    public void testFromStringLegacyFormat() {
        String s = "ID#WORKDAY#10#15#16#45#60";
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
        assertEquals(new Time15(-2, 30).toMinutes(), copy.getBalance());
    }

    public void testToStringPauseNull() {
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
        assertEquals("ID#KIDSICKDAY#10#15#16#45#-#-", s);
    }

    public void testFromStringPauseNull() {

        String s = "ID#KIDSICKDAY#10#15#16#45#-#-";

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

    public void testFromStringPauseNullLegacyFormat() {

        String s = "ID#KIDSICKDAY#10#15#16#45#-";

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

    public void testToStringCombinationWorkdayVacationPauseNull() {
        DaysDataNew data = new DaysDataNew("ID");
        BeginEndTask task = new BeginEndTask();
        data.addTask(task);
        task.setKindOfDay(KindOfDay.WORKDAY);
        task.setBegin(10);
        task.setBegin15(15);
        task.setEnd(16);
        task.setEnd15(45);
        task.setPause(null);

        BeginEndTask otherTask = new BeginEndTask();
        otherTask.setKindOfDay(KindOfDay.VACATION);
        otherTask.setTotal(new Time15(4, 0));
        data.addTask(otherTask);

        String s = data.toString();
        //assertEquals("ID#WORKDAY#10#15#16#45#-#VACATION#4", s);
        assertEquals("ID#WORKDAY#10#15#16#45#-#-#VACATION#-#-#-#-#-#4.00", s);
    }

    public void testFromStringCombinationWorkdayVacationPauseNull() {
        String s = "ID#WORKDAY#10#15#16#45#-#VACATION#-#-#-#-#-#4.00";
        DaysDataNew copy = DaysDataNew.fromString(s);
        assertEquals("ID", copy.getId());
        BeginEndTask copyTask = (BeginEndTask) copy.getTask(0);
        assertEquals(new Integer(10), copyTask.getBegin());
        assertEquals(new Integer(15), copyTask.getBegin15());
        assertEquals(new Integer(16), copyTask.getEnd());
        assertEquals(new Integer(45), copyTask.getEnd15());
        assertEquals(null, copyTask.getPause());
        assertEquals(KindOfDay.WORKDAY, copyTask.getKindOfDay());

        BeginEndTask copyTask1 = (BeginEndTask) copy.getTask(1);
        assertEquals(KindOfDay.VACATION, copyTask1.getKindOfDay());
        assertEquals(4, copyTask1.getTotal().getHours());
        assertEquals(0, copyTask1.getTotal().getMinutes());

        assertTrue(copyTask.isComplete());
        assertTrue(copyTask1.isComplete());
        // Task 1: -90 min, Task 2: +240 min == +150 min == +2h 30 min
        assertEquals(new Time15(2, 30).toMinutes(), copy.getBalance());
    }

    public void testFromStringCombinationWorkdayVacationPauseNullLegacyFormat() {
        String s = "ID#WORKDAY#10#15#16#45#-#VACATION#4";
        DaysDataNew copy = DaysDataNew.fromString(s);
        assertEquals("ID", copy.getId());
        BeginEndTask copyTask = (BeginEndTask) copy.getTask(0);
        assertEquals(new Integer(10), copyTask.getBegin());
        assertEquals(new Integer(15), copyTask.getBegin15());
        assertEquals(new Integer(16), copyTask.getEnd());
        assertEquals(new Integer(45), copyTask.getEnd15());
        assertEquals(null, copyTask.getPause());
        assertEquals(KindOfDay.WORKDAY, copyTask.getKindOfDay());

        BeginEndTask copyTask1 = (BeginEndTask) copy.getTask(1);
        assertEquals(KindOfDay.VACATION, copyTask1.getKindOfDay());
        assertEquals(4, copyTask1.getTotal().getHours());
        assertEquals(0, copyTask1.getTotal().getMinutes());

        assertTrue(copyTask.isComplete());
        assertTrue(copyTask1.isComplete());
        // Task 1: -90 min, Task 2: +240 min == +150 min == +2h 30 min
        assertEquals(new Time15(2, 30).toMinutes(), copy.getBalance());
    }

    public void testToStringIncomplete() {
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
        assertEquals("ID#WORKDAY#10#15#-#45#60#-", s);
    }

    public void testFromStringIncomplete() {
        String s = "ID#WORKDAY#10#15#-#45#60#-";
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
        assertEquals(0, copy.getBalance());
    }

    public void testFromStringIncompleteLegcyFormat() {
        String s = "ID#WORKDAY#10#15#-#45#60";
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
        assertEquals(0, copy.getBalance());
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
