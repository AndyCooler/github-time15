package com.mango_apps.time15.storage;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.Task;
import com.mango_apps.time15.types.Time15;
import com.mango_apps.time15.util.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Spec by example:
 * Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
 * 08.06.2016,WORKDAY,10:00,14:00,00:45,"1,25",,VACATION,,,,"4,0","endlich Urlaub"
 * 09.06.2016,WORKDAY,10:00,14:00,00:45,"4,75","Übergabe",SICK,,,,"6,0"
 */
public class ExternalCsvFileStorage extends FileStorage implements StorageFacade {

    private static final String CSV_VERSION_CURRENT = "1_0";

    private Activity activity;

    @Override
    public boolean saveDaysDataNew(Activity activity, DaysDataNew data) {

        if (!initialized && !init()) {
            return false;
        }
        this.activity = activity;
        String csvHeadline = getHeadline(CSV_VERSION_CURRENT);
        if (csvHeadline == null) {
            return false;
        }
        List<String> csvMonth = loadWholeMonth(getFilename(data.getId()));
        List<String> csvMonthNew = new ArrayList<String>();

        String newCsvLine = toCsvLine(data, CSV_VERSION_CURRENT);
        for (String csvLine : csvMonth) {
            if (csvLine.startsWith(data.getId())) {
                csvMonthNew.add(newCsvLine);
            } else {
                csvMonthNew.add(csvLine);
            }
        }

        return saveWholeMonth(getFilename(data.getId()), csvMonth);
    }

    private boolean saveWholeMonth(String filename, List<String> csvMonth) {
        File file = new File(storageDir, filename);

        boolean result = false;
        try {
            FileOutputStream fos = new FileOutputStream(file, true);

            PrintWriter pw = new PrintWriter(fos);
            for (String csvLine : csvMonth) {
                pw.println(csvLine);
            }
            pw.flush();
            pw.close();
            fos.close();

            Log.i(getClass().getName(), "Saved file " + filename);
            result = true;
        } catch (IOException e) {
            fatal("saveWholeMonth", "Error saving file " + filename);
            Log.e(getClass().getName(), "Error saving file " + filename + " as " + file.getAbsolutePath(), e);
        }
        return result;
    }

    private String toCsvLine(DaysDataNew data, String version) {

        if (CSV_VERSION_CURRENT.equals(version)) {
            String s = data.getId() + ",";
            for (int i = 0; i < data.getNumberOfTasks(); i++) {
                Task task = data.getTask(i);
                s += task.getKindOfDay();
                if (task instanceof BeginEndTask) {
                    BeginEndTask taskB = (BeginEndTask) task;
                    s += new Time15(taskB.getBegin(), taskB.getBegin15()).toDisplayString() + ",";
                    s += new Time15(taskB.getEnd(), taskB.getEnd15()).toDisplayString() + ",";
                    if (taskB.getPause() == null) {
                        s += ",";
                    } else {
                        s += Time15.fromMinutes(taskB.getPause()).toDisplayString() + ",";
                    }
                } else {
                    s += ",,,";
                }
                s += "\"" + task.getTotal().toDecimalFormat() + "\",";
                s += ","; // reserved for future note
            }
            return s;
        }
        fatal("getHeadline", "Version " + version + " unsupported!");
        return null;
    }

    public List<String> loadWholeMonth(String filename) {

        return null; // TODO loadWholeMonth
    }

    private String getHeadline(String version) {
        if (CSV_VERSION_CURRENT.equals(version)) {
            return "Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note";
        }
        fatal("getHeadline", "Version " + version + " unsupported!");
        return null;
    }

    private void fatal(String method, String msg) {

        if (activity != null) {
            Toast.makeText(activity, method + " : " + msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public DaysDataNew loadDaysDataNew(Activity activity, String id) {
        return null;
    }

    @Override
    public int loadBalance(Activity activity, String id) {
        return 0;
    }

    private String getFilename(String id) {
        return TimeUtils.getMonthYearOfID(id) + "__Time15__" + CSV_VERSION_CURRENT + ".csv";
    }
}
