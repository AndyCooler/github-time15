package com.mango_apps.time15.storage;

import android.app.Activity;
import android.util.Log;

import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.util.DaysDataUtils;
import com.mango_apps.time15.util.TimeUtils;

import java.util.HashMap;
import java.util.List;

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

    @Override
    public int loadBalance(Activity activity, String id) {
        int balance = 0;

        List<String> idList = TimeUtils.getListOfIdsOfMonth(id);
        for (String currentId : idList) {
            DaysData data = loadDaysData(activity, currentId);
            balance += DaysDataUtils.calculateBalance(data);
        }
        return balance;
    }
}
