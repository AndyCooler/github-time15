package com.mythosapps.time15;

import android.test.suitebuilder.annotation.SmallTest;

import com.mythosapps.time15.types.Time15;

import junit.framework.TestCase;

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

    public void testToDecimalPositive() {
        checkToDecimal(1, 30, "1.50");
        checkToDecimal(1, 15, "1.25");
        checkToDecimal(10, 45, "10.75");
        checkToDecimal(0, 0, "0.00");
    }

    public void testToDecimalNegative() {
        checkToDecimal(-1, 30, "-1.50");
        checkToDecimal(-1, 15, "-1.25");
        checkToDecimal(-10, 45, "-10.75");
        checkToDecimal(-4, 0, "-4.00");
    }

    private void checkToDecimal(int hours, int minutes, String decimalFormat) {
        Time15 t = new Time15(hours, minutes);
        String s = t.toDecimalFormat();
        assertEquals(decimalFormat, s);
    }

    public void testFromDecimalPositive() {
        checkFromDecimal(1, 30, "1.50");
        checkFromDecimal(1, 15, "1.25");
        checkFromDecimal(10, 45, "10.75");
        checkFromDecimal(0, 0, "0.00");
    }

    public void testFromDecimalNegative() {
        checkFromDecimal(-1, 30, "-1.50");
        checkFromDecimal(-1, 15, "-1.25");
        checkFromDecimal(-10, 45, "-10.75");
        checkFromDecimal(-4, 0, "-4.00");
    }

    private void checkFromDecimal(int hours, int minutes, String decimalFormat) {
        Time15 t = Time15.fromDecimalFormat(decimalFormat);
        assertEquals(hours, t.getHours());
        assertEquals(minutes, t.getMinutes());
    }
}
