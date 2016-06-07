package com.mango_apps.time15.storage;

import android.app.Activity;
import android.util.Log;

import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.util.TimeUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by andreas on 07.01.16.
 */
public class NoopStorage implements StorageFacade {

    HashMap<String, String> cache = new HashMap<String, String>();

    @Override
    public boolean saveDaysDataNew(Activity activity, DaysDataNew data) {
        Log.i(getClass().getName(), "Saved data: " + data.toString());
        cache.put(data.getId(), data.toString());
        return true;
    }

    @Override
    public DaysDataNew loadDaysDataNew(Activity activity, String id) {
        if (cache.get(id) == null) {
            Log.i(getClass().getName(), "No data with id " + id);
        } else {
            Log.i(getClass().getName(), "Loaded data " + cache.get(id));
        }
        return cache.get(id) == null ? null : DaysDataNew.fromString(cache.get(id));
    }


    @Override
    public int loadBalance(Activity activity, String id) {
        int balance = 0;

        List<String> idList = TimeUtils.getListOfIdsOfMonth(id);
        for (String currentId : idList) {
            DaysDataNew data = loadDaysDataNew(activity, currentId);
            if (data != null) {
                balance += data.getBalance();
            }
        }
        return balance;
    }
}
