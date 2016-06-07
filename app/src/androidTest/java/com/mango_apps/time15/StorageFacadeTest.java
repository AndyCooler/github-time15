package com.mango_apps.time15;

import com.mango_apps.time15.storage.NoopStorage;
import com.mango_apps.time15.storage.StorageFacade;
import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;

import junit.framework.TestCase;

/**
 * Created by andreas on 13.05.16.
 */
public class StorageFacadeTest extends TestCase {

    public void testNoopStorageSaveLoad() {
        StorageFacade storage = new NoopStorage();

        DaysDataNew data = new DaysDataNew("05.05.2016");
        BeginEndTask task0 = new BeginEndTask();
        task0.setKindOfDay(KindOfDay.WORKDAY);
        task0.setBegin(10);
        task0.setBegin15(15);
        task0.setEnd(16);
        task0.setEnd15(45);
        task0.setPause(60);
        data.addTask(task0);
        storage.saveDaysDataNew(null, data);

        DaysDataNew loaded = storage.loadDaysDataNew(null, data.getId());
        assertEquals(data.toString(), loaded.toString());
    }
}
