package com.mythosapps.time15.storage;

import android.app.Activity;
import android.util.Log;

import com.mythosapps.time15.types.BalanceType;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.types.Time15;
import com.mythosapps.time15.util.TimeUtils;
import com.mythosapps.time15.util.CsvUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by andreas on 07.01.16.
 */
public class NoopStorage implements StorageFacade {

    HashMap<String, String> cache = new HashMap<String, String>();

    @Override
    public boolean saveDaysDataNew(Activity activity, DaysDataNew data) {
        Log.i(getClass().getName(), "Saved data: " + CsvUtils.toCsvLine(data));
        cache.put(data.getId(), data.getNumberOfTasks() == 0 ? null : CsvUtils.toCsvLine(data));
        return true;
    }

    @Override
    public DaysDataNew loadDaysDataNew(Activity activity, String id) {
        if (cache.get(id) == null) {
            Log.i(getClass().getName(), "No data with id " + id);
        } else {
            Log.i(getClass().getName(), "Loaded data " + cache.get(id));
        }
        try {
            return cache.get(id) == null ? null : CsvUtils.fromCsvLine(cache.get(id));
        } catch (CsvFileLineWrongException e) {
            e.printStackTrace(); // for testing only
            throw new RuntimeException(e);
        }
    }


    @Override
    public int loadBalance(Activity activity, String id, BalanceType balanceType) {
        int balance = 0;

        List<String> idList = TimeUtils.getListOfIdsOfMonth(id);
        int sumWork = 0;
        int numDays = 0;
        for (String currentId : idList) {
            if (balanceType == BalanceType.BALANCE) {
                DaysDataNew data = loadDaysDataNew(activity, currentId);
                if (data != null) {
                    balance += data.getBalance();
                }
            } else if (balanceType == BalanceType.AVERAGE_WORK) {
                DaysDataNew data = loadDaysDataNew(activity, currentId);


                if (data != null) {
                    Time15 sumDay = data.getTotalFor(KindOfDay.WORKDAY);
                    if (sumDay.toMinutes() > 0) {
                        numDays++;
                    }
                    sumWork += sumDay.toMinutes();
                }
                balance = numDays > 0 ? sumWork / numDays : 0;
            }
        }
        return balance;
    }

    @Override
    public int loadTaskSum(Activity activity, String id, KindOfDay task) {
        int sum = 0;

        List<String> idList = TimeUtils.getListOfIdsOfMonth(id);
        for (String currentId : idList) {
            DaysDataNew data = loadDaysDataNew(activity, currentId);
            if (data != null) {
                for (int i = 0; i < data.getNumberOfTasks(); i++) {
                    sum += task.equals(data.getTask(i).getKindOfDay()) ? data.getTask(i).getTotal().toMinutes() : 0;
                }
            }
        }
        return sum;
    }
}
