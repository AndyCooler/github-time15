package com.mango_apps.time15.storage;

import android.os.Build;
import android.util.Log;

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
        int i = 0;
        for (String currentId : ids) {
            if (!TimeUtils.isWeekend(currentId)) {
                DaysData data1 = null;
                switch (i++) {
                    case 1:
                        data1 = new DaysData(currentId);
                        data1.setDay(KindOfDay.WORKDAY);
                        data1.setBegin(9);
                        data1.setBegin15(15);
                        data1.setEnd(19);
                        data1.setEnd15(30);
                        data1.setPause(30);
                        break;
                    case 2:
                        data1 = new DaysData(currentId);
                        data1.setDay(KindOfDay.WORKDAY);
                        data1.setBegin(9);
                        data1.setBegin15(15);
                        data1.setEnd(17);
                        data1.setEnd15(45);
                        data1.setPause(45);
                        break;
                    case 3:
                        data1 = new DaysData(currentId);
                        data1.setDay(KindOfDay.WORKDAY);
                        data1.setBegin(10);
                        data1.setBegin15(45);
                        data1.setEnd(17);
                        data1.setEnd15(30);
                        data1.setPause(30);
                        break;
                    case 4:
                        data1 = new DaysData(currentId);
                        data1.setDay(KindOfDay.KIDSICKDAY);
                        break;
                    case 5:
                        data1 = new DaysData(currentId);
                        data1.setDay(KindOfDay.HOLIDAY);
                        break;
                    default:
                        data1 = TestDataFactory.newRandom(currentId);
                }
                Log.i("Test data total: ", currentId + " : " + DaysDataUtils.calculateTotal(data1).toDisplayString());
                Log.i("Test data balance: ", currentId + " : " + DaysDataUtils.calculateBalance(data1));
                storage.saveDaysData(null, data1);
            }
        }
    }
}
