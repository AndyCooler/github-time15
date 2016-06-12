package com.mango_apps.time15;

import android.test.suitebuilder.annotation.SmallTest;

import com.mango_apps.time15.types.Time15;

import junit.framework.TestCase;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by andreas on 14.03.16.
 */
@SmallTest
public class Time15Test extends TestCase {

    public void testToMinutes() {
        Time15 t = new Time15(2, 30);
        assertEquals(150, t.toMinutes());
    }

    public void testToMinutesPos() {
        Time15 t = Time15.fromMinutes(150);
        assertEquals(2, t.getHours());
        assertEquals(30, t.getMinutes());
        assertEquals(150, t.toMinutes());
    }

    public void testToMinutesNeg() {
        Time15 t = Time15.fromMinutes(-150);
        assertEquals(-2, t.getHours());
        assertEquals(30, t.getMinutes());
        assertEquals(-150, t.toMinutes());
    }

    public void testToMinutesPosSmall() {
        Time15 t = Time15.fromMinutes(15);
        assertEquals(0, t.getHours());
        assertEquals(15, t.getMinutes());
        assertEquals(15, t.toMinutes());
    }

    public void testToMinutesNegSmall() {
        Time15 t = Time15.fromMinutes(-15);
        assertEquals(0, t.getHours());
        assertEquals(15, t.getMinutes());
        assertEquals(-15, t.toMinutes());
    }

    public void testPlusPos() {
        Time15 t = Time15.fromMinutes(150);
        Time15 add = new Time15(2, 45);
        t.plus(add);
        assertEquals(5, t.getHours());
        assertEquals(15, t.getMinutes());
        assertEquals(315, t.toMinutes());
    }

    public void testPlusNeg() {
        Time15 t = Time15.fromMinutes(-150);
        Time15 add = new Time15(-2, 45);
        t.plus(add);
        assertEquals(-5, t.getHours());
        assertEquals(15, t.getMinutes());
        assertEquals(-315, t.toMinutes());
    }

    public void testDecimalPositive() {
        Time15 t = new Time15(1, 30);
        String s = t.toDecimalFormat();
        assertEquals("1.50", s);
    }

    public void testDecimalNegative() {
        Time15 t = new Time15(-1, 30);
        String s = t.toDecimalFormat();
        assertEquals("-1.50", s);
    }
}
