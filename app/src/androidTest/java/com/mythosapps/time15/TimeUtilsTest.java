package com.mythosapps.time15;

import com.mythosapps.time15.storage.StorageFactory;
import com.mythosapps.time15.util.TimeUtils;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by andreas on 05.01.16.
 */
public class TimeUtilsTest {

    @Test
    public void testCreateID() {
        Assert.assertNotNull(TimeUtils.createID());
        Assert.assertTrue(TimeUtils.createID().length() == 10);
    }

    @Test
    public void testGetMonthYearOfID() {
        Assert.assertEquals("2016_01", TimeUtils.getMonthYearOfID("05.01.2016"));
    }

    @Test
    public void testDateBackwards() {
        Assert.assertEquals("03.01.2016", TimeUtils.dateBackwards("04.01.2016"));
        Assert.assertEquals("04.01.2016", TimeUtils.dateBackwards("05.01.2016"));
        Assert.assertEquals("04.01.2016", TimeUtils.dateBackwards("05.01.2016"));
        Assert.assertEquals("05.01.2016", TimeUtils.dateBackwards("06.01.2016"));
    }

    @Test
    public void testDateForwards() {
        Assert.assertEquals("01.02.2016", TimeUtils.dateForwards("31.01.2016"));
        Assert.assertEquals("01.02.2016", TimeUtils.dateForwards("31.01.2016"));
    }

    @Test
    public void testListOfIdsOfMonth() {

        checkMonth("05.01.2016", 31);
        checkMonth("05.02.2016", 29);
        checkMonth("05.03.2016", 31);
        checkMonth("05.04.2016", 30);
        checkMonth("05.05.2016", 31);
        checkMonth("05.06.2016", 30);
        checkMonth("05.07.2016", 31);
        checkMonth("05.08.2016", 31);
        checkMonth("05.09.2016", 30);
        checkMonth("05.10.2016", 31);
        checkMonth("05.11.2016", 30);
        checkMonth("05.12.2016", 31);
    }

    @Test
    public void testCreateTestdata() {
        StorageFactory.getDataStorage();
    }

    private void checkMonth(String id, int daysInMonth) {
        List<String> list = TimeUtils.getListOfIdsOfMonth(id);
        String firstId = list.get(0);
        String lastId = list.get(list.size() - 1);
        Assert.assertEquals("01" + id.substring(2), firstId);
        Assert.assertEquals(daysInMonth + id.substring(2), lastId);
    }
}
