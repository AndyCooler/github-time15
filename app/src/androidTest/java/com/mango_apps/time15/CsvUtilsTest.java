package com.mango_apps.time15;

import com.mango_apps.time15.storage.CsvFileLineWrongException;
import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.types.NumberTask;
import com.mango_apps.time15.types.Time15;
import com.mango_apps.time15.util.CsvUtils;

import junit.framework.TestCase;

/**
 * Created by andreas on 12.10.16.
 */

public class CsvUtilsTest extends TestCase {

    public void testToCsvLineWorkday() {
        //Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
        DaysDataNew data = new DaysDataNew("05.05.2016");
        BeginEndTask task0 = new BeginEndTask();
        task0.setKindOfDay(KindOfDay.WORKDAY);
        task0.setBegin(10);
        task0.setBegin15(15);
        task0.setEnd(17);
        task0.setEnd15(00);
        task0.setPause(60);
        data.addTask(task0);
        String s = CsvUtils.toCsvLine(data);
        assertEquals("05.05.2016,WORKDAY,10:15,17:00,01:00,5.75,,", s);
    }

    public void testToCsvLineVacation() {
        //Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
        DaysDataNew data = new DaysDataNew("05.05.2016");
        BeginEndTask task0 = new BeginEndTask();
        task0.setKindOfDay(KindOfDay.VACATION);
        data.addTask(task0);
        String s = CsvUtils.toCsvLine(data);
        assertEquals("05.05.2016,VACATION,,,,8.00,,", s);
    }

    public void testToCsvLineWorkday2() {
        //Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
        DaysDataNew data = new DaysDataNew("05.05.2016");
        BeginEndTask task0 = new BeginEndTask();
        task0.setKindOfDay(KindOfDay.WORKDAY);
        task0.setBegin(10);
        task0.setBegin15(15);
        task0.setEnd(16);
        task0.setEnd15(45);
        task0.setPause(60);
        data.addTask(task0);
        NumberTask task1 = new NumberTask();
        task1.setKindOfDay(KindOfDay.VACATION);
        task1.setTotal(Time15.fromMinutes(4 * 60));
        data.addTask(task1);

        String s = CsvUtils.toCsvLine(data);
        assertEquals("05.05.2016,WORKDAY,10:15,16:45,01:00,5.50,,VACATION,,,,4.00,,", s);
    }

    public void testFromCsvLineWorkday() throws CsvFileLineWrongException {
        //Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
        String s = "05.05.2016,WORKDAY,10:15,17:00,01:00,5.75,,";

        DaysDataNew data = CsvUtils.fromCsvLine(s);

        assertEquals("05.05.2016", data.getId());
        BeginEndTask task0 = (BeginEndTask) data.getTask(0);
        assertEquals(KindOfDay.WORKDAY, task0.getKindOfDay());
        assertEquals(10, task0.getBegin().intValue());
        assertEquals(15, task0.getBegin15().intValue());
        assertEquals(17, task0.getEnd().intValue());
        assertEquals(00, task0.getEnd15().intValue());
        assertEquals(60, task0.getPause().intValue());
    }

    public void testFromCsvLineWorkdayNoPause() throws CsvFileLineWrongException {
        //Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
        String s = "05.05.2016,WORKDAY,10:15,16:45,,5.50,,";

        DaysDataNew data = CsvUtils.fromCsvLine(s);

        assertEquals("05.05.2016", data.getId());
        BeginEndTask task0 = (BeginEndTask) data.getTask(0);
        assertEquals(KindOfDay.WORKDAY, task0.getKindOfDay());
        assertEquals(10, task0.getBegin().intValue());
        assertEquals(15, task0.getBegin15().intValue());
        assertEquals(16, task0.getEnd().intValue());
        assertEquals(45, task0.getEnd15().intValue());
        assertNull(task0.getPause());
    }

    public void testFromCsvLineWorkday2() throws CsvFileLineWrongException {
        //Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
        String s = "05.05.2016,WORKDAY,10:15,16:45,01:00,5.50,note,VACATION,,,,4.00,,";

        DaysDataNew data = CsvUtils.fromCsvLine(s);

        assertEquals("05.05.2016", data.getId());
        BeginEndTask task0 = (BeginEndTask) data.getTask(0);
        assertEquals(KindOfDay.WORKDAY, task0.getKindOfDay());
        assertEquals(10, task0.getBegin().intValue());
        assertEquals(15, task0.getBegin15().intValue());
        assertEquals(16, task0.getEnd().intValue());
        assertEquals(45, task0.getEnd15().intValue());
        assertEquals(60, task0.getPause().intValue());

        NumberTask task1 = (NumberTask) data.getTask(1);
        assertEquals(KindOfDay.VACATION, task1.getKindOfDay());
        assertEquals(4 * 60, task1.getTotal().toMinutes());
    }

    public void testFromCsvLineVacation() throws CsvFileLineWrongException {

        String s = "05.05.2016,VACATION,,,,0.00,,";

        DaysDataNew data = CsvUtils.fromCsvLine(s);

        assertEquals("05.05.2016", data.getId());
        BeginEndTask task0 = (BeginEndTask) data.getTask(0);
        assertEquals(KindOfDay.VACATION, task0.getKindOfDay());
        assertNull(task0.getBegin());
        assertNull(task0.getBegin15());
        assertNull(task0.getEnd());
        assertNull(task0.getEnd15());
        assertNull(task0.getPause());
    }
}