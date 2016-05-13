package com.mango_apps.time15;

import com.mango_apps.time15.storage.NoopStorage;
import com.mango_apps.time15.storage.StorageFacade;
import com.mango_apps.time15.storage.StorageFactory;
import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;

import junit.framework.TestCase;

/**
 * Created by andreas on 13.05.16.
 */
public class StorageFacadeTest extends TestCase {

    public void testNoopStorageSaveLoad() {
        StorageFacade storage = new NoopStorage();

        DaysData data = new DaysData("05.05.2016");
        data.setDay(KindOfDay.WORKDAY);
        data.setBegin(10);
        data.setBegin15(15);
        data.setEnd(16);
        data.setEnd15(45);
        data.setPause(60);
        storage.saveDaysData(null, data);

        DaysData loaded = storage.loadDaysData(null, data.getId());
        assertEquals(data.toString(), loaded.toString());
    }

    public void testNoopStorageMigrationSaveLoad1() {
        DaysData data = DaysData.fromString("ID#WORKDAY_SOME_VACATION#10#15#16#45#-#4");
        StorageFacade storage = new NoopStorage();
        storage.saveDaysData(null, data);

        DaysDataNew loaded = storage.loadDaysDataNew(null, data.getId());
        assertTrue(data.isEqualToNewData(loaded));
        assertEquals("ID#WORKDAY#10#15#16#45#-#VACATION#4", loaded.toString());
    }

    public void testNoopStorageMigrationSaveLoad2() {
        DaysData data = DaysData.fromString("ID#WORKDAY#10#15#16#45#60");
        StorageFacade storage = new NoopStorage();
        storage.saveDaysData(null, data);

        DaysDataNew loaded = storage.loadDaysDataNew(null, data.getId());
        boolean isEqual = data.isEqualToNewData(loaded);
        assertTrue(isEqual);
        assertEquals("ID#WORKDAY#10#15#16#45#60", loaded.toString());
    }

    public void testNoopStorageMigrationSaveLoad3() {
        DaysData data = DaysData.fromString("ID#KIDSICKDAY#10#15#16#45#-#-"); // pause, otherHours
        StorageFacade storage = new NoopStorage();
        storage.saveDaysData(null, data);

        DaysDataNew loaded = storage.loadDaysDataNew(null, data.getId());
        assertTrue(data.isEqualToNewData(loaded));
        assertEquals("ID#KIDSICKDAY#10#15#16#45#-", loaded.toString());
    }

}
