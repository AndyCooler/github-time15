package com.mango_apps.time15.storage;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.util.DaysDataUtils;
import com.mango_apps.time15.util.TimeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

/**
 * This class uses external files to store the start, end and pause values for each day.
 * All days of a month are saved in the same file.
 */
public class ExternalFileStorage implements StorageFacade {

    public static final String STORAGE_DIR = "Time15";

    private boolean initialized = false;

    private File storageDir;

    @Override
    public boolean saveDaysData(Activity activity, DaysData data) {
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
    public DaysData loadDaysData(Activity activity, String id) {
        if (!initialized && !init()) {
            return null;
        }

        File file = new File(storageDir, getFilename(id));
        if (!file.exists()) {
            return null;
        }

        DaysData data = null;
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
                data = DaysData.fromString(candidate);
            }
        } catch (IOException e) {
            Log.e(getClass().getName(), "Error loading data with id " + id, e);
        }

        return data;
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
            DaysData data = loadDaysData(activity, currentId);
            balance += DaysDataUtils.calculateBalance(data);
        }
        return balance;
    }

    private boolean init() {
        if (isExternalStorageWritable()) {
            if (isStorageDirPresent()) {
                storageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS) + File.separator + STORAGE_DIR);
                return true;
            } else {
                storageDir = createStorageDir();
                if (storageDir == null) {
                    Log.e(getClass().getName(), "Storage NOT initialized!");
                } else {
                    Log.i(getClass().getName(), "Storage initialized: " + storageDir);
                    initialized = true;
                    return true;
                }
            }
        }
        return false;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        Log.e(getClass().getName(), "Storage not writable! State: " + state);
        return false;
    }

    private boolean isStorageDirPresent() {

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS) + File.separator + STORAGE_DIR);
        if (file.exists()) {
            return true;
        }
        Log.w(getClass().getName(), "Storage dir not present! Checked " + file.getAbsolutePath());
        return false;
    }

    public File createStorageDir() {

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS) + File.separator + STORAGE_DIR);
        if (file.mkdirs()) {
            Log.i(getClass().getName(), "Storage dir created: " + file.getAbsolutePath());
        } else {
            Log.e(getClass().getName(), "Storage dir not created!");
        }
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    private String getFilename(String id) {
        return TimeUtils.getMonthYearOfID(id) + ".dat";
    }
}
