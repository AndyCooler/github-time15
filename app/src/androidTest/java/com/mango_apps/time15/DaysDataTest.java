package com.mango_apps.time15;

import android.test.suitebuilder.annotation.SmallTest;

import com.mango_apps.time15.storage.DaysData;
import com.mango_apps.time15.storage.KindOfDay;

import junit.framework.TestCase;

//import org.junit.Test;

/**
 * Created by andreas on 04.01.16.
 */

@SmallTest
public class DaysDataTest extends TestCase {

    public void testToFromString() {
        DaysData data = new DaysData("ID");
        data.setDay(KindOfDay.CHILDCAREDAY);
        data.setBegin(10);
        data.setBegin15(15);
        data.setEnd(16);
        data.setEnd15(45);
        data.setPause(60);

        String s = data.toString();
        assertEquals("10#15#16#45#60#CHILDCAREDAY", s);

        DaysData copy = DaysData.fromString("ID", s);
        assertEquals("ID", copy.getId());
        assertEquals(new Integer(10), copy.getBegin());
        assertEquals(new Integer(15), copy.getBegin15());
        assertEquals(new Integer(16), copy.getEnd());
        assertEquals(new Integer(45), copy.getEnd15());
        assertEquals(new Integer(60), copy.getPause());
        assertEquals(KindOfDay.CHILDCAREDAY, copy.getDay());
    }
}
