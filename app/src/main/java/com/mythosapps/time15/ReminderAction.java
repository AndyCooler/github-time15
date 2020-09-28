package com.mythosapps.time15;

import android.app.Activity;

import com.mythosapps.time15.storage.StorageFacade;
import com.mythosapps.time15.storage.StorageFactory;
import com.mythosapps.time15.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ReminderAction {

    private final Activity activity;
    private StorageFacade storage;

    public ReminderAction(Activity activity) {
        this.activity = activity;
    }

    public List<String> remindOfLastWeeksEntries() {
        if (storage == null) {
            storage = StorageFactory.getDataStorage();
        }

        List<String> lastSevenDays = TimeUtils.getListOfLastSevenDays();
        List<String> missingEntries = new ArrayList<>();
        for (String lastId : lastSevenDays) {
            if (storage.loadDaysDataNew(activity, lastId) == null) {
                missingEntries.add(lastId);
            }
        }
        return missingEntries;
    }
}
