package com.mango_apps.time15;

import android.test.suitebuilder.annotation.SmallTest;

import com.mango_apps.time15.util.TimeUtils;

import junit.framework.TestCase;

/**
 * Created by andreas on 05.01.16.
 */
@SmallTest
public class TimeUtilsTest extends TestCase {

    public void testCreateID() {
        assertNotNull(TimeUtils.createID());
        assertTrue(TimeUtils.createID().length() == 10);
    }

    public void testGetMonthYearOfID() {
        assertEquals("2016_01", TimeUtils.getMonthYearOfID("05.01.2016"));
    }
}
