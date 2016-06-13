package com.mango_apps.time15.storage;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.util.TimeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class uses external files to store the start, end and pause values for each day.
 * All days of a month are saved in the same file.
 */
public class ExternalFileStorage extends FileStorage implements StorageFacade {

    @Override
    public boolean saveDaysDataNew(Activity activity, DaysDataNew data) {
        if (!initialized && !init()) {
            return false;
        }

        String s = data.toString();

        File file = new File(storageDir, getFilename(data.getId()));

        boolean result = false;
        try {
            FileOutputStream fos = new FileOutputStream(file, true);

            PrintWriter pw = new PrintWriter(fos);
            pw.println(s);
            pw.flush();
            pw.close();
            fos.close();

            Log.i(getClass().getName(), "Saved data with id " + data.getId());
            result = true;
        } catch (IOException e) {
            Log.e(getClass().getName(), "Error saving data with id " + data.getId() + " to file " + file.getAbsolutePath(), e);
        }
        return result;
    }

    @Override
    public DaysDataNew loadDaysDataNew(Activity activity, String id) {
        if (!initialized && !init()) {
            return null;
        }

        File file = new File(storageDir, getFilename(id));
        if (!file.exists()) {
            return null;
        }

        DaysDataNew data = null;
        try {
            FileInputStream fis = new FileInputStream(file);

            InputStreamReader isr;
            isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

            String test = null;
            String candidate = null;
            while (true) {
                test = br.readLine();

                if (test == null) break;
                if (test.startsWith(id)) {
                    candidate = test;
                }
            }
            isr.close();
            fis.close();
            br.close();
            if (candidate == null) {
                Log.i(getClass().getName(), "No data with id " + id);
            } else {
                data = DaysDataNew.fromString(candidate);
            }
        } catch (IOException e) {
            Log.e(getClass().getName(), "Error loading data with id " + id, e);
        }

        return data;
    }

    @Override
    public int loadBalance(Activity activity, String id) {

        if (!initialized && !init()) {
            return 0;
        }

        File file = new File(storageDir, getFilename(id));
        if (!file.exists()) {
            return 0;
        }

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
    public boolean saveDaysDataMonth(Activity activity, String id, List<DaysDataNew> dataList) {
        for (DaysDataNew data : dataList) {
            if (!saveDaysDataNew(activity, data)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<DaysDataNew> loadDaysDataMonth(Activity activity, String id) {

        List<DaysDataNew> dataList = new ArrayList<DaysDataNew>();
        for (String idCurrent : TimeUtils.getListOfIdsOfMonth(id)) {
            DaysDataNew data = loadDaysDataNew(activity, idCurrent);
            dataList.add(data);
        }
        return dataList;
    }

    private String getFilename(String id) {
        return TimeUtils.getMonthYearOfID(id) + ".dat";
    }
}
