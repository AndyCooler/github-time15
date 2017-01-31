package com.mythosapps.time15.types;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by andreas on 12.03.16.
 */
public class DaysDataNew {

    public static final int DUE_HOURS_PER_DAY = 8;

    public static final int DUE_TOTAL_MINUTES = DUE_HOURS_PER_DAY * 60;

    private String id;

    private final ArrayList<BeginEndTask> tasks;

    private static final String SEP = "#";

    public DaysDataNew(String id) {
        this.id = id;
        this.tasks = new ArrayList<BeginEndTask>();
    }

    /**
     * Copy constructor.
     *
     * @param data original data
     */
    public DaysDataNew(DaysDataNew data) {
        this(data.getId());
        if (data.getNumberOfTasks() > 0) {
            for (int i = 0; i < data.getNumberOfTasks(); i++) {
                tasks.add(data.getTask(i).copy());
            }
        }
    }

    /**
     * Copy method.
     *
     * @param data original data
     */
    public static DaysDataNew copy(DaysDataNew data) {
        if (data == null) {
            return null;
        }
        return new DaysDataNew(data);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addTask(BeginEndTask task) {
        tasks.add(task);
    }

    public void deleteTask(BeginEndTask task) {
        tasks.remove(task);
    }

    public BeginEndTask getTask(int taskNo) {
        return taskNo < tasks.size() ? tasks.get(taskNo) : null;
    }

    public int getBalance() {

        Time15 actual = getTotal();
        int actualTotalMinutes = actual.toMinutes();
        if (actualTotalMinutes == 0) {
            return 0;
        }
        int balance = actualTotalMinutes - DUE_TOTAL_MINUTES;

        return balance;
    }


    public Time15 getTotal() {
        Time15 total = Time15.fromMinutes(0);
        for (BeginEndTask task : tasks) {
            total.plus(task.getTotal());
        }
        return total;
    }

    @Override
    public String toString() {
        String s = id;
        for (BeginEndTask task : tasks) {
            s += task.toString();
        }
        return s;
    }

    public static DaysDataNew fromString(String s) {

        // das ist fuer 1 bis 2 Tasks pro DaysDataNew
        StringTokenizer tokenizer = new StringTokenizer(s, SEP);
        String id = tokenizer.nextToken();
        DaysDataNew data = new DaysDataNew(id);
        if (!tokenizer.hasMoreTokens()) {
            return data;
        }
        String kindOfDay = tokenizer.nextToken();
        String begin = tokenizer.nextToken();
        String begin15 = tokenizer.nextToken();
        String end = tokenizer.nextToken();
        String end15 = tokenizer.nextToken();
        String pause = tokenizer.nextToken();
        String kindOfDay2 = null;
        BeginEndTask beginEndTask = null;
        String total = null;
        if (tokenizer.hasMoreElements()) {
            total = tokenizer.nextToken();
            if (KindOfDay.fromString(total) == null && total != null) {
                // BeginEndTask mit total am Ende (neu)
                beginEndTask = BeginEndTask.fromString(kindOfDay + SEP + begin +
                        SEP + begin15 + SEP + end +
                        SEP + end15 + SEP + pause +
                        SEP + (total.equals("-") ? null : total));
            }
        }
        if (total == null || KindOfDay.fromString(total) != null) {
            // BeginEndTask ohne total am Ende (alt)
            beginEndTask = BeginEndTask.fromString(kindOfDay + SEP + begin +
                    SEP + begin15 + SEP + end +
                    SEP + end15 + SEP + pause);

        }
        data.addTask(beginEndTask);

        if (KindOfDay.fromString(total) != null) {
            kindOfDay2 = total; // Token ist schon Teil von zweitem Task
        }
        if (kindOfDay2 == null && tokenizer.hasMoreElements()) {
            kindOfDay2 = tokenizer.nextToken();
        }

        if (tokenizer.hasMoreElements()) {
            String begin2 = tokenizer.nextToken();
            BeginEndTask task1 = null;
            if (tokenizer.hasMoreElements()) {
                // akzeptiere neues Format: #VACATION#-#-#-#-#-#4.0
                task1 = BeginEndTask.fromString(kindOfDay2 +
                        SEP + begin2 + SEP + tokenizer.nextToken() +
                        SEP + tokenizer.nextToken() + SEP + tokenizer.nextToken() +
                        SEP + tokenizer.nextToken() +
                        (tokenizer.hasMoreElements() ? (SEP + tokenizer.nextToken()) : ""));
            } else {
                // akzeptiere altes Format (von obsoletem NumberTask): #VACATION#4
                final String NONE = "-";
                task1 = BeginEndTask.fromString(kindOfDay2 +
                        SEP + NONE + SEP + NONE +
                        SEP + NONE + SEP + NONE +
                        SEP + NONE + SEP + begin2);
            }
            data.addTask(task1);
        }

        return data;
    }

    public int getNumberOfTasks() {
        return tasks.size();
    }

    public boolean isInInitialState() {
        boolean initial = getNumberOfTasks() == 0 || getNumberOfTasks() == 1 && isInInitialState(getTask(0));
        return initial;
    }

    private boolean isInInitialState(BeginEndTask task) {
        boolean initial = KindOfDay.WORKDAY.equals(task.getKindOfDay()) && task.isOnlyTotalComplete();
        return initial;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof DaysDataNew) {
            DaysDataNew d = (DaysDataNew) o;
            if (d.id == null || !d.id.equals(id) || d.getNumberOfTasks() != getNumberOfTasks()) {
                return false;
            } else {
                for (int i = 0; i < getNumberOfTasks(); i++) {
                    if (!getTask(i).equals(d.getTask(i))) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }
}
