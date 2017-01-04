package com.mythosapps.time15.storage;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.util.CsvUtils;
import com.mythosapps.time15.util.TimeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Spec by example:
 * Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
 * 08.06.2016,WORKDAY,10:00,14:00,00:45,1.25,,VACATION,,,,4.0,"endlich Urlaub"
 * 09.06.2016,WORKDAY,10:00,14:00,00:45,4.75,"Übergabe",SICK,,,,6.0
 */
public class ExternalCsvFileStorage extends FileStorage implements StorageFacade {

    private Activity activity;

    private String currentMonthYear;

    private HashMap<String, DaysDataNew> currentMonthsData = null;

    ExternalFileStorage redundantFileStorage = new ExternalFileStorage();

    public ExternalCsvFileStorage() {
    }

    @Override
    public boolean saveDaysDataNew(Activity activity, DaysDataNew data) {

        if (!initialized && !init()) {
            Log.e(getClass().getName(), "Save failed for " + data.getId() + ": not initialized.");
            return false;
        }
        this.activity = activity;
        String csvHeadline = CsvUtils.getHeadline();
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
                csvMonth.add(CsvUtils.toCsvLine(dataCurrent));
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


    private void fatal(String method, String msg) {
        Log.e(getClass().getName(), method + " : " + msg);

        if (activity != null) {
            Toast.makeText(activity, method + " : " + msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public DaysDataNew loadDaysDataNew(Activity activity, String id) {

        // get data for id from cache
        String newMonthYear = TimeUtils.getMonthYearOfID(id);

        if (newMonthYear.equals(currentMonthYear)) {
            Log.i(getClass().getName(), "Loaded data from cache for " + id);
            return currentMonthsData.get(id);
        }
        // load month from file into cache, then get data for id from new cache
        boolean success = false;
        HashMap<String, DaysDataNew> newMonthsDataRedundant = new HashMap<String, DaysDataNew>();
        HashMap<String, DaysDataNew> newMonthsData = new HashMap<String, DaysDataNew>();
        ArrayList<String> warnings = new ArrayList<String>();
        try {
            // load from CSV
            // TODO 1. refactor move code block to CsvUtils with InputStream parameter
            // TODO 2. rework user notification scheme when problems occur
            Map<String, Integer> csvMonth = loadWholeMonth(id, getFilename(id));
            for (String csvCurrent : csvMonth.keySet()) {
                DaysDataNew data = null;
                try {
                    data = CsvUtils.fromCsvLine(csvCurrent);
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
            //if (!missingIds.isEmpty()) {
            //    warnings.add("Not loaded from csv: " + missingIds);
            //}
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

        String monthYear = TimeUtils.getMonthYearOfID(id);

        if (monthYear.equals(currentMonthYear)) {
            int balance = 0;
            for (DaysDataNew data : currentMonthsData.values()) {
                balance += data.getBalance();
            }
            Log.i(getClass().getName(), "Load balance from cache for " + id);
            return balance;
        }
        Log.w(getClass().getName(), "Cache miss while loading balance for " + id);
        return redundantFileStorage.loadBalance(activity, id);
    }

    public static String getFilename(String id) {
        return TimeUtils.getMonthYearOfID(id) + "__Time15__" + "1_0" + ".csv";
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
