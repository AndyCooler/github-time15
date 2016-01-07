package com.mango_apps.time15.storage;

import android.app.Activity;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by andreas on 07.01.16.
 */
public class NoopStorage implements StorageFacade {

    HashMap<String, DaysData> cache = new HashMap<String, DaysData>();

    @Override
    public boolean saveDaysData(Activity activity, DaysData data) {
        Log.i(getClass().getName(), "Saved data: " + data);
        cache.put(data.getId(), data);
        return true;
    }

    @Override
    public DaysData loadDaysData(Activity activity, String id) {
        if (cache.get(id) == null) {
            Log.i(getClass().getName(), "No data with id " + id);
        } else {
            Log.i(getClass().getName(), "Loaded data " + cache.get(id));
        }
        return cache.get(id);
    }
}
