package com.mythosapps.time15;

import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.Time15;

import junit.framework.TestCase;

import java.util.Locale;

public class YearOverviewActivityTest extends TestCase {

    public void testRoundsCorrectly() {

        // case:
        // es gibt 2 Einträge im Monat, bei 1 Nachkommastelle werden sie abgerundet
        // insgesamt ergibt sich ein Wert, der bei bei 1 Nachkommastelle aufgerundet wird
        // dadurch stimmt die Summe nicht mit den einzelnen Einträgen überein, die Summe
        // ist aber korrekt.

        Time15 totalYear = Time15.fromMinutes(0);

        Time15 timeA = Time15.fromMinutes(9 * 60);
        totalYear.plus(timeA);
        double numDaysA = (double) timeA.toMinutes() / (double) DaysDataNew.DUE_TOTAL_MINUTES;
        String numDaysStringA = String.format(Locale.US, "%.2f", numDaysA);
        String numDaysStringA1 = String.format(Locale.US, "%.1f", numDaysA);
        System.out.println("days  timeA  : " + numDaysStringA);
        System.out.println("days  timeA  : " + numDaysStringA1);

        Time15 timeB = Time15.fromMinutes(128 * 60 + 15);
        totalYear.plus(timeB);
        double numDaysB = (double) timeB.toMinutes() / (double) DaysDataNew.DUE_TOTAL_MINUTES;
        String numDaysStringB = String.format(Locale.US, "%.2f", numDaysB);
        String numDaysStringB1 = String.format(Locale.US, "%.1f", numDaysB);
        System.out.println("days  timeB  : " + numDaysStringB);
        System.out.println("days  timeB  : " + numDaysStringB1);

        System.out.println("--------------------------");
        String hoursPerYear = totalYear.toDecimalFormat();
        while (hoursPerYear.length() < 7) {
            hoursPerYear = " " + hoursPerYear;
        }
        System.out.println("hours per year: " + hoursPerYear);
        double numDays = (double) totalYear.toMinutes() / (double) DaysDataNew.DUE_TOTAL_MINUTES;
        String numDaysString = String.format(Locale.US, "%.2f", numDays);
        String numDaysString1 = String.format(Locale.US, "%.1f", numDays);

        System.out.println("in days       : " + numDaysString);
        System.out.println("in days       : " + numDaysString1);

        assertEquals("17.2", numDaysString1);

    }
}