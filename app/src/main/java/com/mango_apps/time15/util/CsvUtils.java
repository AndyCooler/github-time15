package com.mango_apps.time15.util;

import android.util.Log;

import com.mango_apps.time15.storage.CsvFileLineWrongException;
import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.types.NumberTask;
import com.mango_apps.time15.types.Task;
import com.mango_apps.time15.types.Time15;

/**
 * Utility class to work with csv files or rather, csv input streams.
 */
public final class CsvUtils {

    public static final String[] CSV_TASK_COLUMNS = {"Task", "Begin", "End", "Break", "Total", "Note"};

    private static final String CSV_ID_COLUMN = "Date";


    public static int CSV_LINE_LENGTH_A;
    public static int CSV_LINE_LENGTH_B;

    static {
        CSV_LINE_LENGTH_A = 2 + CSV_TASK_COLUMNS.length;
        CSV_LINE_LENGTH_B = 2 + 2 * CSV_TASK_COLUMNS.length;
    }

    public static String toCsvLine(DaysDataNew data) {

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


    public static DaysDataNew fromCsvLine(String csvString) throws CsvFileLineWrongException {
        DaysDataNew data = null;

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
        return data;
    }

    private static BeginEndTask toBeginEndTask(String id, String kindOfTask, String begin, String end, String breakString, String total, String note) throws CsvFileLineWrongException {

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

    private static NumberTask toNumberTask(String id, String kindOfTask, String begin, String end, String breakString, String total, String note) throws CsvFileLineWrongException {
        NumberTask task = new NumberTask();

        String s = safeGetNextToken(kindOfTask, id, "Task");
        task.setKindOfDay(KindOfDay.valueOf(s));

        s = safeGetNextTokenOptional(total, id, "Total");
        task.setTotal(Time15.fromDecimalFormat(s));

        return task;
    }

    private static String safeGetNextTokenOptional(String s, String id, String expected) throws CsvFileLineWrongException {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return s.trim();
    }

    private static String safeGetNextToken(String s, String id, String expected) throws CsvFileLineWrongException {
        if (s == null || s.isEmpty()) {
            String msg = "load csv " + id + " missing: " + expected;
            Log.e(CsvUtils.class.getName(), msg);
            throw new CsvFileLineWrongException(id, msg);
        }
        return s.trim();
    }

    public static String getHeadline() {
        return CSV_ID_COLUMN + ","
                + "Task,Begin,End,Break,Total,Note" + ","
                + "Task,Begin,End,Break,Total,Note";
    }
}
