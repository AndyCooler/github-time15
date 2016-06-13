package com.mango_apps.time15.storage;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.types.NumberTask;
import com.mango_apps.time15.types.Task;
import com.mango_apps.time15.types.Time15;
import com.mango_apps.time15.util.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Spec by example:
 * Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
 * 08.06.2016,WORKDAY,10:00,14:00,00:45,1.25,,VACATION,,,,4.0,"endlich Urlaub"
 * 09.06.2016,WORKDAY,10:00,14:00,00:45,4.75,"Übergabe",SICK,,,,6.0
 */
public class ExternalCsvFileStorage extends FileStorage implements StorageFacade {

    public static final String CSV_VERSION_CURRENT = "1_0";

    private Activity activity;

    private String anyIdICurrentMonth;

    private HashMap<String, DaysDataNew> monthsData = null;

    ExternalFileStorage redundantFileStorage = new ExternalFileStorage();

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
        //if (!TimeUtils.isSameMonth(data.getId(), anyIdICurrentMonth) || monthsData == null) {
        //    return false;
        //}
        // update cache
        monthsData.put(data.getId(), data);
        List<String> csvMonth = new ArrayList<String>();
        csvMonth.add(csvHeadline);

        for (String idCurrent : TimeUtils.getListOfIdsOfMonth(data.getId())) {
            DaysDataNew dataCurrent = monthsData.get(idCurrent);
            if (dataCurrent != null) {
                csvMonth.add(toCsvLine(dataCurrent, CSV_VERSION_CURRENT));
            }
        }

        return saveWholeMonth(getFilename(data.getId()), csvMonth);
    }

    private boolean saveWholeMonth(String filename, List<String> csvMonth) {
        File file = new File(storageDir, filename);

        boolean result = false;
        try {
            FileOutputStream fos = new FileOutputStream(file, false);

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

    public String toCsvLine(DaysDataNew data, String version) {

        if (CSV_VERSION_CURRENT.equals(version)) {
            String s = data.getId() + ",";
            for (int i = 0; i < data.getNumberOfTasks(); i++) {
                Task task = data.getTask(i);
                s += task.getKindOfDay() + ",";
                if (task instanceof BeginEndTask) {
                    BeginEndTask taskB = (BeginEndTask) task;
                    if (taskB.getBegin() == null) {
                        s += ",";
                    } else {
                        s += new Time15(taskB.getBegin(), taskB.getBegin15()).toDisplayString() + ",";
                    }
                    if (taskB.getEnd() == null) {
                        s += ",";
                    } else {
                        s += new Time15(taskB.getEnd(), taskB.getEnd15()).toDisplayString() + ",";
                    }
                    if (taskB.getPause() == null) {
                        s += ",";
                    } else {
                        s += Time15.fromMinutes(taskB.getPause()).toDisplayString() + ",";
                    }
                } else {
                    s += ",,,";
                }
                s += task.getTotal().toDecimalFormat() + ",";
                s += ","; // reserved for future note
            }
            return s;
        }
        fatal("getHeadline", "Version " + version + " unsupported!");
        return null;
    }

    private DaysDataNew toDaysData(String csvString, String version) {
        DaysDataNew data = null;
        if (CSV_VERSION_CURRENT.equals(version)) {

            StringTokenizer t = new StringTokenizer(",");

            // parse ID
            if (t.hasMoreTokens()) {
                String id = t.nextToken();
                if ("Date".equals(id)) {
                    return null; // headline starts with Date
                }
                data = new DaysDataNew(id);
            } else {
                fatal("toDaysData", "must start with ID: " + csvString);
                return null;
            }
            int taskNumber = 0;
            while (t.hasMoreTokens()) {
                if (taskNumber == 0) {
                    BeginEndTask task0 = new BeginEndTask();
                    // parse kindOfDay
                    String token = t.nextToken();
                    KindOfDay kindOfDay = KindOfDay.fromString(token);
                    if (kindOfDay == null) {
                        fatal("toDaysData", "unknown kind of task : '" + token + "'");
                    }
                    task0.setKindOfDay(kindOfDay);
                    // parse beginTime
                    Time15 beginTime = Time15.fromDisplayString(t.nextToken());
                    if (beginTime != null) {
                        task0.setBegin(beginTime.getHours());
                        task0.setBegin15(beginTime.getMinutes());
                    }
                    // parse endTime
                    Time15 endTime = Time15.fromDisplayString(t.nextToken());
                    if (endTime != null) {
                        task0.setEnd(endTime.getHours());
                        task0.setEnd15(endTime.getMinutes());
                    }
                    // parse pause
                    Time15 pauseTime = Time15.fromDisplayString(t.nextToken());
                    if (pauseTime != null) {
                        task0.setPause(pauseTime.toMinutes());
                    }
                    t.nextToken(); // total (ignored)
                    t.nextToken(); // note (ignored)
                    data.addTask(task0);
                    taskNumber = 1;
                } else if (taskNumber == 1) {
                    NumberTask task1 = new NumberTask();
                    // parse kindOfDay
                    String token = t.nextToken();
                    KindOfDay kindOfDay = KindOfDay.fromString(token);
                    if (kindOfDay == null) {
                        fatal("toDaysData", "unknown kind of task : '" + token + "'");
                    }
                    task1.setKindOfDay(kindOfDay);
                    t.nextToken(); // begin (ignored)
                    t.nextToken(); // end (ignored)
                    t.nextToken(); // pause (ignored)

                    // parse total
                    token = t.nextToken();
                    task1.setTotal(Time15.fromDecimalFormat(token));
                    data.addTask(task1);
                    // ignore rest of csvString
                }
            }
        }
        return data;
    }

    public List<String> loadWholeMonth(String filename) {

// TODO load
        List<String> list = new ArrayList<String>();
        //list.add();
        return list;
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

        if (TimeUtils.isSameMonth(id, anyIdICurrentMonth) && monthsData != null) {
            return monthsData.get(id);
        }

        List<String> csvMonth = loadWholeMonth(getFilename(id));
        monthsData = new HashMap<String, DaysDataNew>();
        //for (String csvCurrent : csvMonth) {
        //    DaysDataNew data = toDaysData(csvCurrent, CSV_VERSION_CURRENT);
        //   if (data != null) {
        //      monthsData.put(data.getId(), data);
        //  }
        // }
        for (String idCurrent : TimeUtils.getListOfIdsOfMonth(id)) {
            DaysDataNew data = redundantFileStorage.loadDaysDataNew(activity, idCurrent);
            if (data != null) {
                monthsData.put(data.getId(), data);
            }
        }

        return redundantFileStorage.loadDaysDataNew(activity, id);
    }

    @Override
    public int loadBalance(Activity activity, String id) {

        return redundantFileStorage.loadBalance(activity, id);
    }

    @Override
    public boolean saveDaysDataMonth(Activity activity, String id, List<DaysDataNew> dataList) {
        return false;
    }

    @Override
    public List<DaysDataNew> loadDaysDataMonth(Activity activity, String id) {
        return null;
    }


    private String getFilename(String id) {
        return TimeUtils.getMonthYearOfID(id) + "__Time15__" + CSV_VERSION_CURRENT + ".csv";
    }
}
