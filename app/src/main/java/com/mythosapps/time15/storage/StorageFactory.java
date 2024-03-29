package com.mythosapps.time15.storage;

import android.util.Log;

import com.mythosapps.time15.types.BeginEndTask;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.types.Time15;
import com.mythosapps.time15.util.TimeUtils;

import java.util.List;

/**
 * Created by andreas on 14.02.16.
 */
public class StorageFactory {

    private static StorageFacade INSTANCE_DATA_STORE = null;

    private static ConfigStorageFacade INSTANCE_CONFIG_STORE = null;


    public static StorageFacade getDataStorage() {
        if (INSTANCE_DATA_STORE == null) {
            INSTANCE_DATA_STORE = createStorage();
        }
        return INSTANCE_DATA_STORE;
    }

    private static StorageFacade createStorage() {
       /* TODO wieder ausbauen if (Build.FINGERPRINT.contains("generic")) {
            StorageFacade storage = new NoopStorage(); // running on emulator
            String id = TimeUtils.createID();
            createTestData(id, storage);
            return storage;
        }*/
        return new ExternalCsvFileStorage();
    }

    private static void createTestData(String id, StorageFacade storage) {
        List<String> ids = TimeUtils.getListOfIdsOfMonth(id);
        int i = 0;
        for (String currentId : ids) {
            // zum Test ob Hinweis angezeigt wird wenn eintrag fehlt
            if (!TimeUtils.isWeekend(currentId) && !TimeUtils.isMonday(currentId)) {
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
        // create day in next month that has a new type of task
        String idNext = TimeUtils.monthForwards(id);
        DaysDataNew data1 = new DaysDataNew(idNext);
        BeginEndTask task0 = new BeginEndTask();
        task0.setKindOfDay(KindOfDay.TEST_NEW);
        task0.setTotal(Time15.fromMinutes(480));
        data1.addTask(task0);
        storage.saveDaysDataNew(null, data1);
    }

    public static ConfigStorageFacade getConfigStorage() {
        if (INSTANCE_CONFIG_STORE == null) {
            INSTANCE_CONFIG_STORE = createConfigStorage();
        }
        return INSTANCE_CONFIG_STORE;
    }

    private static ConfigStorageFacade createConfigStorage() {
        /* TODO wieder ausbauen für lokale tests if (Build.FINGERPRINT.contains("generic")) {
            ConfigAssetStorage storage = new ConfigAssetStorage(new ConfigXmlParser()); // running on emulator
            return storage;
        }
         */
        return new ConfigFileStorage();
    }
}
