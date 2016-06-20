package com.mango_apps.time15;

import com.mango_apps.time15.storage.CsvFileLineWrongException;
import com.mango_apps.time15.storage.ExternalCsvFileStorage;
import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.types.NumberTask;
import com.mango_apps.time15.types.Time15;

import junit.framework.TestCase;

/**
 * Created by andreas on 12.06.16.
 */
public class ExternalCsvFileStorageTest extends TestCase {

    private ExternalCsvFileStorage storage = new ExternalCsvFileStorage();

    public void testToCsvLine() {
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
        String s = storage.toCsvLine(data, ExternalCsvFileStorage.CSV_VERSION_CURRENT);
        assertEquals("05.05.2016,WORKDAY,10:15,16:45,01:00,5.50,,", s);
    }

    public void testToCsvLine2() {
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

        String s = storage.toCsvLine(data, ExternalCsvFileStorage.CSV_VERSION_CURRENT);
        assertEquals("05.05.2016,WORKDAY,10:15,16:45,01:00,5.50,,VACATION,,,,4.00,,", s);
    }

    public void testFromCsvLine() throws CsvFileLineWrongException {
        //Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
        String s = "05.05.2016,WORKDAY,10:15,16:45,01:00,5.50,,";

        DaysDataNew data = storage.fromCsvLine(s, ExternalCsvFileStorage.CSV_VERSION_CURRENT);

        assertEquals("05.05.2016", data.getId());
        BeginEndTask task0 = (BeginEndTask) data.getTask(0);
        assertEquals(KindOfDay.WORKDAY, task0.getKindOfDay());
        assertEquals(10, task0.getBegin().intValue());
        assertEquals(15, task0.getBegin15().intValue());
        assertEquals(16, task0.getEnd().intValue());
        assertEquals(45, task0.getEnd15().intValue());
        assertEquals(60, task0.getPause().intValue());
    }

    public void testFromCsvLine2() throws CsvFileLineWrongException {
        //Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
        String s = "05.05.2016,WORKDAY,10:15,16:45,01:00,5.50,note,VACATION,,,,4.00,,";

        DaysDataNew data = storage.fromCsvLine(s, ExternalCsvFileStorage.CSV_VERSION_CURRENT);

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
}
