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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spec by example:
 * Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
 * 08.06.2016,WORKDAY,10:00,14:00,00:45,1.25,,VACATION,,,,4.0,"endlich Urlaub"
 * 09.06.2016,WORKDAY,10:00,14:00,00:45,4.75,"Übergabe",SICK,,,,6.0
 */
public class ExternalCsvFileStorage extends FileStorage implements StorageFacade {

    public static final String CSV_VERSION_CURRENT = "1_0";

    private static final String CSV_ID_COLUMN = "Date";

    private static final String[] CSV_TASK_COLUMNS = {"Task", "Begin", "End", "Break", "Total", "Note"};
    private static int CSV_LINE_LENGTH_A;
    private static int CSV_LINE_LENGTH_B;
    private Activity activity;

    private String currentMonthYear;

    private HashMap<String, DaysDataNew> currentMonthsData = null;

    ExternalFileStorage redundantFileStorage = new ExternalFileStorage();

    public ExternalCsvFileStorage() {
        CSV_LINE_LENGTH_A = 2 + CSV_TASK_COLUMNS.length;
        CSV_LINE_LENGTH_B = 2 + 2 * CSV_TASK_COLUMNS.length;
    }

    @Override
    public boolean saveDaysDataNew(Activity activity, DaysDataNew data) {

        if (!initialized && !init()) {
            Log.e(getClass().getName(), "Save failed for " + data.getId() + ": not initialized.");
            return false;
        }
        this.activity = activity;
        String csvHeadline = getHeadline(CSV_VERSION_CURRENT);
        if (csvHeadline == null) {
            Log.e(getClass().getName(), "Save failed for " + data.getId() + ": no csv headline.");
            return false;
        }
        String newMonthYear = TimeUtils.getMonthYearOfID(data.getId());

        if (!newMonthYear.equals(currentMonthYear)) {
            Log.e(getClass().getName(), "Save failed for " + data.getId() + ": not in current month " + currentMonthYear);
            return false;
        }
        // update cache
        currentMonthsData.put(data.getId(), data);

        // save to legacy storage
        boolean success = redundantFileStorage.saveDaysDataNew(activity, data);

        // save to csv storage
        List<String> csvMonth = new ArrayList<String>();
        csvMonth.add(csvHeadline);
        for (String idCurrent : TimeUtils.getListOfIdsOfMonth(data.getId())) {
            DaysDataNew dataCurrent = currentMonthsData.get(idCurrent);
            if (dataCurrent != null) {
                csvMonth.add(toCsvLine(dataCurrent, CSV_VERSION_CURRENT));
            }
        }
        boolean successCsvSave = saveWholeMonth(getFilename(data.getId()), csvMonth);
        Log.i(getClass().getName(), "Save for " + data.getId() + ": success legacy: " + success + ", success csv: " + successCsvSave);
        return success && successCsvSave;
    }

    private boolean saveWholeMonth(String filename, List<String> csvMonth) {

        if (!initialized && !init()) {
            return false;
        }

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

    public DaysDataNew fromCsvLine(String csvString, String version) throws CsvFileLineWrongException {
        DaysDataNew data = null;
        if (CSV_VERSION_CURRENT.equals(version)) {

            String[] line = csvString.split(",", -1);
            String id = "unknown";
            if (line.length > 0) {
                id = line[0];
            }

            if (line.length == CSV_LINE_LENGTH_A) {
                data = new DaysDataNew(id);

                BeginEndTask task0 = toBeginEndTask(id, line[1], line[2], line[3], line[4], line[5], line[6]);
                data.addTask(task0);
            } else if (line.length == CSV_LINE_LENGTH_B) {
                data = new DaysDataNew(id);

                BeginEndTask task0 = toBeginEndTask(id, line[1], line[2], line[3], line[4], line[5], line[6]);
                data.addTask(task0);

                NumberTask task1 = toNumberTask(id, line[7], line[8], line[9], line[10], line[11], line[12]);
                data.addTask(task1);
            } else {
                String msg = "Number of columns: " + line.length + ", allowed: " + CSV_LINE_LENGTH_A + " or " + CSV_LINE_LENGTH_B;

                throw new CsvFileLineWrongException(id, msg);
            }

            // ignore rest of csvString
        }
        return data;
    }

    private NumberTask toNumberTask(String id, String kindOfTask, String begin, String end, String breakString, String total, String note) throws CsvFileLineWrongException {
        NumberTask task = new NumberTask();

        String s = safeGetNextToken(kindOfTask, id, "Task");
        task.setKindOfDay(KindOfDay.valueOf(s));

        s = safeGetNextTokenOptional(total, id, "Total");
        task.setTotal(Time15.fromDecimalFormat(s));

        return task;
    }

    private BeginEndTask toBeginEndTask(String id, String kindOfTask, String begin, String end, String breakString, String total, String note) throws CsvFileLineWrongException {

        BeginEndTask task = new BeginEndTask();

        String s = safeGetNextToken(kindOfTask, id, "Task");
        task.setKindOfDay(KindOfDay.valueOf(s));

        s = safeGetNextTokenOptional(begin, id, "Begin");
        Time15 beginTime = Time15.fromDisplayString(s);
        if (beginTime != null) {
            task.setBegin(beginTime.getHours());
            task.setBegin15(beginTime.getMinutes());
        }

        s = safeGetNextTokenOptional(end, id, "End"); // TODO if begin exists then require end
        Time15 endTime = Time15.fromDisplayString(s);
        if (endTime != null) {
            task.setEnd(endTime.getHours());
            task.setEnd15(endTime.getMinutes());
        }

        s = safeGetNextTokenOptional(breakString, id, "Pause");
        Time15 pauseTime = Time15.fromDisplayString(s);
        if (pauseTime != null) {
            task.setPause(pauseTime.toMinutes());
        }

        return task;
    }

    private String safeGetNextTokenOptional(String s, String id, String expected) throws CsvFileLineWrongException {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return s.trim();
    }

    private String safeGetNextToken(String s, String id, String expected) throws CsvFileLineWrongException {
        if (s == null || s.isEmpty()) {
            String msg = "load csv " + id + " missing: " + expected;
            Log.e(getClass().getName(), msg);
            throw new CsvFileLineWrongException(id, msg);
        }
        return s.trim();
    }

    public Map<String, Integer> loadWholeMonth(String id, String filename) {

        if (!initialized && !init()) {
            return null;
        }

        Map<String, Integer> map = new HashMap<String, Integer>();

        File file = new File(storageDir, filename);
        if (!file.exists()) {
            Log.w(getClass().getName(), "loadWholeMonth : file not found " + filename);
            return map;
        }

        List<String> idsOfMonth = TimeUtils.getListOfIdsOfMonth(id);

        try {
            FileInputStream fis = new FileInputStream(file);

            InputStreamReader isr;
            isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

            String test = null;
            String currentId = null;
            int lineNumber = 0;
            while (true) {
                lineNumber++;
                test = br.readLine();

                if (test == null) break;

                currentId = test.length() > 10 ? test.substring(0, 10) : null;
                if (idsOfMonth.contains(currentId)) {
                    map.put(test, lineNumber);
                } else {
                    Log.w(getClass().getName(), filename + " : Ignored line " + lineNumber + " : " + test);
                }
            }
            isr.close();
            fis.close();
            br.close();

        } catch (IOException e) {
            Log.e(getClass().getName(), "Error loading from csv file " + filename + " for id " + id, e);
        }
        return map;
    }

    private String getHeadline(String version) {
        if (CSV_VERSION_CURRENT.equals(version)) {
            return CSV_ID_COLUMN + ","
                    + "Task,Begin,End,Break,Total,Note" + ","
                    + "Task,Begin,End,Break,Total,Note";
        }
        fatal("getHeadline", "Version " + version + " unsupported!");
        return null;
    }

    private void fatal(String method, String msg) {
        Log.e(getClass().getName(), method + " : " + msg);

        if (activity != null) {
            Toast.makeText(activity, method + " : " + msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public DaysDataNew loadDaysDataNew(Activity activity, String id) {

        String newMonthYear = TimeUtils.getMonthYearOfID(id);

        if (newMonthYear.equals(currentMonthYear)) {
            Log.i(getClass().getName(), "Loaded data from cache for " + id);
            return currentMonthsData.get(id);
        }
        boolean success = false;
        HashMap<String, DaysDataNew> newMonthsDataRedundant = new HashMap<String, DaysDataNew>();
        HashMap<String, DaysDataNew> newMonthsData = new HashMap<String, DaysDataNew>();
        ArrayList<String> warnings = new ArrayList<String>();
        try {
            // load from CSV
            Map<String, Integer> csvMonth = loadWholeMonth(id, getFilename(id));
            for (String csvCurrent : csvMonth.keySet()) {
                DaysDataNew data = null;
                try {
                    data = fromCsvLine(csvCurrent, CSV_VERSION_CURRENT);
                } catch (CsvFileLineWrongException e) {
                    warnings.add("Line " + csvMonth.get(csvCurrent) + ": " + e.getMessage());
                }
                if (data != null) {
                    newMonthsData.put(data.getId(), data); // fill cache
                }
            }

            // load from legacy / redundant storage
            for (String idCurrent : TimeUtils.getListOfIdsOfMonth(id)) {
                DaysDataNew data = redundantFileStorage.loadDaysDataNew(activity, idCurrent);
                if (data != null) {
                    newMonthsDataRedundant.put(data.getId(), data);
                }
            }
            // (destructive!) compare csv to legacy / redundant storage
            String missingIds = "";
            for (String idCurrent : newMonthsDataRedundant.keySet()) {
                if (newMonthsData.containsKey(idCurrent)) {
                    newMonthsData.remove(idCurrent);
                } else {
                    missingIds += idCurrent + " ";
                }
            }
            if (!missingIds.isEmpty()) {
                warnings.add("Not loaded from csv: " + missingIds);
            }
            if (!newMonthsData.isEmpty()) {
                String additionalIds = "";
                for (String idCurrent : newMonthsData.keySet()) {
                    additionalIds += idCurrent + " ";
                }
                warnings.add("Load.Compare: csv has + ids: " + additionalIds);
            }

            if (!warnings.isEmpty()) {
                String warningsMsg = "";
                for (String msg : warnings) {
                    warningsMsg += msg + "\n";
                }
                Toast.makeText(activity, warningsMsg, Toast.LENGTH_LONG).show();
            }
            success = true;
        } finally {
            if (success) {
                Log.i(getClass().getName(), "Cache changed from " + currentMonthYear + " to " + newMonthYear + " while loading " + id);
                currentMonthYear = newMonthYear;
                currentMonthsData = newMonthsDataRedundant;
            } else {
                Log.e(getClass().getName(), "Cache error, can't load " + newMonthYear + " while loading " + id);
            }
        }
        return currentMonthsData == null ? null : currentMonthsData.get(id);
    }

    @Override
    public int loadBalance(Activity activity, String id) {

        String newMonthYear = TimeUtils.getMonthYearOfID(id);

        if (newMonthYear.equals(currentMonthYear)) {
            int balance = 0;
            for (DaysDataNew data : currentMonthsData.values()) {
                balance += data.getBalance();
            }
            Log.i(getClass().getName(), "Load balance from cache for " + id);
            return balance;
        }
        Log.e(getClass().getName(), "Cache miss while loading balance for " + id);
        return redundantFileStorage.loadBalance(activity, id);
    }

    private String getFilename(String id) {
        return TimeUtils.getMonthYearOfID(id) + "__Time15__" + CSV_VERSION_CURRENT + ".csv";
    }
}
