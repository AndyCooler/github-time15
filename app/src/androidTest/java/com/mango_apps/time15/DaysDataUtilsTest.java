package com.mango_apps.time15;

import com.mango_apps.time15.storage.KindOfDay;
import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.types.Time15;
import com.mango_apps.time15.util.DaysDataUtils;

import junit.framework.TestCase;

/**
 * Created by andreas on 12.01.16.
 */
public class DaysDataUtilsTest extends TestCase {

    public void testDifferenceWithPause() {
        DaysData data = new DaysData("ID");
        data.setDay(KindOfDay.WORKDAY);
        data.setBegin(10);
        data.setBegin15(15);
        data.setEnd(16);
        data.setEnd15(45);
        data.setPause(60);

        Time15 total = DaysDataUtils.calculateTotal(data);
        assertEquals(5, total.getHours());
        assertEquals(30, total.getMinutes());
    }

    public void testDifferenceWithoutPause() {
        DaysData data = new DaysData("ID");
        data.setDay(KindOfDay.WORKDAY);
        data.setBegin(10);
        data.setBegin15(15);
        data.setEnd(16);
        data.setEnd15(45);
        data.setPause(null);

        Time15 total = DaysDataUtils.calculateTotal(data);
        assertEquals(6, total.getHours());
        assertEquals(30, total.getMinutes());
    }

    public void testDifferenceWithout15s() {
        DaysData data = new DaysData("ID");
        data.setDay(KindOfDay.WORKDAY);
        data.setBegin(10);
        data.setBegin15(null);
        data.setEnd(16);
        data.setEnd15(null);
        data.setPause(null);

        Time15 total = DaysDataUtils.calculateTotal(data);
        assertEquals(6, total.getHours());
        assertEquals(0, total.getMinutes());
    }

    public void testDifferenceIncomplete() {
        DaysData data = new DaysData("ID");
        data.setDay(KindOfDay.WORKDAY);
        data.setBegin(10);
        data.setBegin15(null);
        data.setEnd(null);
        data.setEnd15(null);
        data.setPause(null);

        Time15 total = DaysDataUtils.calculateTotal(data);
        assertEquals(0, total.getHours());
        assertEquals(0, total.getMinutes());
    }

    public void testDifferenceIncomplete15() {
        DaysData data = new DaysData("ID");
        data.setDay(KindOfDay.WORKDAY_SOME_VACATION);
        data.setBegin(10);
        data.setBegin15(null);
        data.setEnd(16);
        data.setEnd15(0);
        data.setPause(null);

        Time15 total = DaysDataUtils.calculateTotal(data);
        assertEquals(6, total.getHours());
        assertEquals(0, total.getMinutes());
    }
}
