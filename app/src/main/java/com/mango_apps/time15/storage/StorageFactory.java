package com.mango_apps.time15.storage;

import android.os.Build;

import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.util.DaysDataUtils;
import com.mango_apps.time15.util.TimeUtils;

/**
 * Created by andreas on 14.02.16.
 */
public class StorageFactory {

    private static StorageFacade INSTANCE = null;

    public static StorageFacade getStorage() {
        if (INSTANCE == null) {
            INSTANCE = createStorage();
        }
        return INSTANCE;
    }

    private static StorageFacade createStorage() {
        if (Build.FINGERPRINT.contains("generic")) {
            StorageFacade storage = new NoopStorage(); // running on emulator
            String id = TimeUtils.createID();
            createTestData(id, storage);
            return storage;
        }
        return new ExternalFileStorage();
    }

    private static void createTestData(String id, StorageFacade storage) {
        DaysData data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(8);
        data1.setBegin15(0);
        data1.setEnd(16);
        data1.setEnd15(0);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(9);
        data1.setBegin15(0);
        data1.setEnd(17);
        data1.setEnd15(30);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY_SOME_VACATION);
        data1.setBegin(11);
        data1.setBegin15(0);
        data1.setEnd(16);
        data1.setEnd15(30);
        data1.setOtherHours(4);
        storage.saveDaysData(null, data1);


        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.HOLIDAY);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(9);
        data1.setBegin15(0);
        data1.setEnd(17);
        data1.setEnd15(30);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(9);
        data1.setBegin15(0);
        data1.setEnd(17);
        data1.setEnd15(30);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(9);
        data1.setBegin15(0);
        data1.setEnd(17);
        data1.setEnd15(30);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(9);
        data1.setBegin15(0);
        data1.setEnd(17);
        data1.setEnd15(30);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(9);
        data1.setBegin15(0);
        data1.setEnd(17);
        data1.setEnd15(30);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(9);
        data1.setBegin15(0);
        data1.setEnd(17);
        data1.setEnd15(30);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(9);
        data1.setBegin15(0);
        data1.setEnd(17);
        data1.setEnd15(30);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(9);
        data1.setBegin15(0);
        data1.setEnd(17);
        data1.setEnd15(30);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(9);
        data1.setBegin15(0);
        data1.setEnd(17);
        data1.setEnd15(30);
        storage.saveDaysData(null, data1);

        id = TimeUtils.dateBackwards(id);
        data1 = new DaysData(id);
        data1.setDay(KindOfDay.WORKDAY);
        data1.setBegin(9);
        data1.setBegin15(0);
        data1.setEnd(17);
        data1.setEnd15(30);
        storage.saveDaysData(null, data1);
    }
}
