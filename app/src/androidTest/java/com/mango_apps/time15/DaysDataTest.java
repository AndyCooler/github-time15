package com.mango_apps.time15;

import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.types.KindOfDay;

import junit.framework.TestCase;

//import org.junit.Test;

/**
 * Created by andreas on 04.01.16.
 */


public class DaysDataTest extends TestCase {

    public void testToFromString() {
        DaysData data = new DaysData("ID");
        data.setDay(KindOfDay.KIDSICKDAY);
        data.setBegin(10);
        data.setBegin15(15);
        data.setEnd(16);
        data.setEnd15(45);
        data.setPause(60);

        String s = data.toString();
        assertEquals("ID#KIDSICKDAY#10#15#16#45#60#-", s);

        DaysData copy = DaysData.fromString(s);
        assertEquals("ID", copy.getId());
        assertEquals(new Integer(10), copy.getBegin());
        assertEquals(new Integer(15), copy.getBegin15());
        assertEquals(new Integer(16), copy.getEnd());
        assertEquals(new Integer(45), copy.getEnd15());
        assertEquals(new Integer(60), copy.getPause());
        assertEquals(KindOfDay.KIDSICKDAY, copy.getDay());
        assertEquals(null, copy.getOtherHours());
    }

    public void testToFromStringPauseNull() {
        DaysData data = new DaysData("ID");
        data.setDay(KindOfDay.KIDSICKDAY);
        data.setBegin(10);
        data.setBegin15(15);
        data.setEnd(16);
        data.setEnd15(45);
        data.setPause(null);

        String s = data.toString();
        assertEquals("ID#KIDSICKDAY#10#15#16#45#-#-", s);

        DaysData copy = DaysData.fromString(s);
        assertEquals("ID", copy.getId());
        assertEquals(new Integer(10), copy.getBegin());
        assertEquals(new Integer(15), copy.getBegin15());
        assertEquals(new Integer(16), copy.getEnd());
        assertEquals(new Integer(45), copy.getEnd15());
        assertEquals(null, copy.getPause());
        assertEquals(KindOfDay.KIDSICKDAY, copy.getDay());
        assertEquals(null, copy.getOtherHours());
    }

    public void testToFromStringCombinationWorkdayVacationPauseNull() {
        DaysData data = new DaysData("ID");
        data.setDay(KindOfDay.WORKDAY_SOME_VACATION);
        data.setBegin(10);
        data.setBegin15(15);
        data.setEnd(16);
        data.setEnd15(45);
        data.setOtherHours(4);

        String s = data.toString();
        assertEquals("ID#WORKDAY_SOME_VACATION#10#15#16#45#-#4", s);

        DaysData copy = DaysData.fromString(s);
        assertEquals("ID", copy.getId());
        assertEquals(KindOfDay.WORKDAY_SOME_VACATION, copy.getDay());
        assertEquals(new Integer(10), copy.getBegin());
        assertEquals(new Integer(15), copy.getBegin15());
        assertEquals(new Integer(16), copy.getEnd());
        assertEquals(new Integer(45), copy.getEnd15());
        assertEquals(null, copy.getPause());
        assertEquals(new Integer(4), copy.getOtherHours());
    }
}