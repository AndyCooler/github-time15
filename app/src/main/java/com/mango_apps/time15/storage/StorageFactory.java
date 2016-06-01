package com.mango_apps.time15.storage;

import android.os.Build;
import android.util.Log;

import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;
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
                DaysDataNew data1 = null;
                BeginEndTask task0 = null;
                switch (i++) {
                    case 1:
                        data1 = new DaysDataNew(currentId);
                        task0 = new BeginEndTask();
                        task0.setKindOfDay(KindOfDay.WORKDAY);
                        task0.setBegin(9);
                        task0.setBegin15(15);
                        task0.setEnd(19);
                        task0.setEnd15(30);
                        task0.setPause(30);
                        data1.addTask(task0);
                        break;
                    case 2:
                        data1 = new DaysDataNew(currentId);
                        task0 = new BeginEndTask();
                        task0.setKindOfDay(KindOfDay.WORKDAY);
                        task0.setBegin(9);
                        task0.setBegin15(15);
                        task0.setEnd(17);
                        task0.setEnd15(45);
                        task0.setPause(45);
                        data1.addTask(task0);
                        break;
                    case 3:
                        data1 = new DaysDataNew(currentId);
                        task0 = new BeginEndTask();
                        task0.setKindOfDay(KindOfDay.WORKDAY);
                        task0.setBegin(10);
                        task0.setBegin15(45);
                        task0.setEnd(17);
                        task0.setEnd15(30);
                        task0.setPause(30);
                        data1.addTask(task0);
                        break;
                    case 4:
                        data1 = new DaysDataNew(currentId);
                        task0 = new BeginEndTask();
                        task0.setKindOfDay(KindOfDay.KIDSICKDAY);
                        data1.addTask(task0);
                        break;
                    case 5:
                        data1 = new DaysDataNew(currentId);
                        task0 = new BeginEndTask();
                        task0.setKindOfDay(KindOfDay.HOLIDAY);
                        data1.addTask(task0);
                        break;
                    default:
                        data1 = TestDataFactory.newRandom(currentId);
                }
                Log.i("Test data total: ", currentId + " : " + data1.getTotal().toDisplayString());
                Log.i("Test data balance: ", currentId + " : " + data1.getBalance());
                storage.saveDaysDataNew(null, data1);
            }
        }
    }
}
