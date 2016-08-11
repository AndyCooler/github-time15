package com.mango_apps.time15.storage;

import android.app.Activity;
import android.util.Log;

import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.util.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public Set<String> loadTaskNames(Activity activity, String id) {

        Set<String> taskNames = new HashSet<>();

        List<String> idList = TimeUtils.getListOfIdsOfMonth(id);
        for (String currentId : idList) {
            DaysDataNew data = loadDaysDataNew(activity, currentId);
            if (data != null) {
                for (int i = 0; i < data.getNumberOfTasks(); i++) {
                    taskNames.add(data.getTask(i).getKindOfDay().getDisplayString());
                }
            }
        }
        return taskNames;
    }
}
