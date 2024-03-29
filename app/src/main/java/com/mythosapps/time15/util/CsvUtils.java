package com.mythosapps.time15.util;

import com.mythosapps.time15.storage.CsvFileLineWrongException;
import com.mythosapps.time15.types.BeginEndTask;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.types.Time15;

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

        if (data.getNumberOfTasks() == 0) {
            return null;
        }
        StringBuilder s = new StringBuilder(data.getId() + ",");
        for (int i = 0; i < data.getNumberOfTasks(); i++) {
            BeginEndTask taskB = data.getTask(i);
            s.append(taskB.getKindOfDay()).append(",");
                if (taskB.getBegin() == null || taskB.getBegin15() == null) {
                    s.append(",");
                } else {
                    s.append(new Time15(taskB.getBegin(), taskB.getBegin15()).toDisplayString()).append(",");
                }
                if (taskB.getEnd() == null || taskB.getEnd15() == null) {
                    s.append(",");
                } else {
                    s.append(new Time15(taskB.getEnd(), taskB.getEnd15()).toDisplayString()).append(",");
                }
            if (taskB.getPause() == null) {
                s.append(",");
            } else {
                s.append(Time15.fromMinutes(taskB.getPause()).toDisplayString()).append(",");
            }
            s.append(taskB.getTotal().toDecimalFormat()).append(",");
            String note = taskB.getNote() == null ? "" : taskB.getNote();
            if (i == 0 && data.getHomeOffice()) {
                note += "[HO]"; // home office
            }
            s.append(note).append(",");
        }
        return s.toString();
    }


    public static DaysDataNew fromCsvLine(String csvString) throws CsvFileLineWrongException {
        String id = "unknown";
        String errMsg = "no error";
        DaysDataNew data = new DaysDataNew(id);
        try {
            errMsg = "Datum wird in der ersten Spalte erwartet!";
            String[] line = csvString.split(",", -1);

            if (line.length > 0) {
                id = line[0];
            }
            data.setId(id);

            errMsg = "Spalten B bis G sollten die Werte für den ersten Task enthalten!";
            if (line.length < CSV_LINE_LENGTH_A) {
                errMsg = "Spalten B bis G müssen vorhanden sein!";
            }
            BeginEndTask task0 = toBeginEndTask(id, line[1], line[2], line[3], line[4], line[5], line[6]);
            if (task0.getNote() != null && task0.getNote().endsWith("[HO]")) { // home office
                data.setHomeOffice(true);
                task0.setNote(task0.getNote().substring(0, task0.getNote().length() - 4));
            }
            data.addTask(task0);

            if (line.length >= CSV_LINE_LENGTH_B) {
                errMsg = "Spalten H bis M sollten die Werte für den zweiten Task enthalten, falls vorhanden!";
                BeginEndTask task1 = toBeginEndTask(id, line[7], line[8], line[9], line[10], line[11], line[12]);
                data.addTask(task1);
            }
        } catch (Throwable t) {
            // error while reading task from String, might result in Task.isComplete == false
        }

        // ignore rest of csvString
        return data;
    }

    private static BeginEndTask toBeginEndTask(String id, String kindOfTask, String begin, String end, String breakString, String total, String note) throws CsvFileLineWrongException {

        BeginEndTask task = new BeginEndTask();
        try {
            // can:  simplify
            String displayString = safeGetNextToken(kindOfTask, id, "Task");
            task.setKindOfDay(KindOfDay.fromString(displayString));

            String s = safeGetNextTokenOptional(begin, id, "Begin");
            Time15 beginTime = toTime15(s);
            if (beginTime != null) {
                task.setBegin(beginTime.getHours());
                task.setBegin15(beginTime.getMinutes());
            }

            s = safeGetNextTokenOptional(end, id, "End");
            Time15 endTime = toTime15(s);
            if (endTime != null) {
                task.setEnd(endTime.getHours());
                task.setEnd15(endTime.getMinutes());
            }

            s = safeGetNextTokenOptional(breakString, id, "Pause");
            Time15 pauseTime = toTime15(s);
            if (pauseTime != null) {
                task.setPause(pauseTime.toMinutes());
            }

            s = safeGetNextTokenOptional(total, id, "Total");
            Time15 totalTime = toTime15FromDecimal(s);
            if (totalTime != null) {
                task.setTotal(totalTime);
            }

            s = safeGetNextTokenOptional(note, id, "Note");
            task.setNote(s);

            if (task.getKindOfDay() == null) {
                task.setKindOfDay(KindOfDay.convert(displayString, task.getBegin(), task.getEnd()));
            }
        } catch (Throwable t) {
            // error while reading task from String, might result in Task.isComplete == false
        }

        return task;
    }

    private static Time15 toTime15(String s) {
        Time15 time15 = null;
        try {
            time15 = Time15.fromDisplayString(s);
        } catch (Throwable t) {
            // ignore
        }
        return time15;
    }

    private static Time15 toTime15FromDecimal(String s) {
        Time15 time15 = null;
        try {
            time15 = Time15.fromDecimalFormat(s);
        } catch (Throwable t) {
            // ignore
        }
        return time15;
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
