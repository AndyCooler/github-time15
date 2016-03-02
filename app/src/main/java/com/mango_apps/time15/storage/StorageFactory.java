package com.mango_apps.time15.storage;

import android.os.Build;

import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.util.DaysDataUtils;
import com.mango_apps.time15.util.TimeUtils;

import java.util.List;

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
        List<String> ids = TimeUtils.getListOfIdsOfMonth(id);
        for (String currentId : ids) {
            if (!TimeUtils.isWeekend(currentId)) {
                DaysData data1 = TestDataFactory.newRandom(currentId);
                storage.saveDaysData(null, data1);
            }
        }
    }
}
