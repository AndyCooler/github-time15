package com.mythosapps.time15.types;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by andreas on 12.03.16.
 */
public class DaysDataNew {

    public static final int DUE_HOURS_PER_DAY = 8;

    public static final int DUE_TOTAL_MINUTES = DUE_HOURS_PER_DAY * 60;

    private String id;

    private boolean homeOffice;

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
        setHomeOffice(data.getHomeOffice());
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

    public void setHomeOffice(boolean homeOffice) {
        this.homeOffice = homeOffice;
    }

    public boolean getHomeOffice() {
        return homeOffice;
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

    public Time15 getTotalFor(KindOfDay day) {
        Time15 total = Time15.fromMinutes(0);
        for (BeginEndTask task : tasks) {
            if (day.equals(task.getKindOfDay())) {
                total.plus(task.getTotal());
            }
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(id);
        for (BeginEndTask task : tasks) {
            s.append(task.toString());
        }
        return s.toString();
    }


    public int getNumberOfTasks() {
        return tasks.size();
    }

    public boolean isInInitialState() {
        boolean initial = getNumberOfTasks() == 0 || getNumberOfTasks() == 1 && isInInitialState(getTask(0));
        return initial;
    }

    private boolean isInInitialState(BeginEndTask task) {
        // TODO Default kind of day!
        boolean initial = KindOfDay.WORKDAY.getDisplayString().equals(task.getKindOfDay().getDisplayString()) && task.isOnlyTotalComplete();
        return initial;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof DaysDataNew) {
            DaysDataNew d = (DaysDataNew) o;
            if (d.id == null || !d.id.equals(id) || d.homeOffice != homeOffice || d.getNumberOfTasks() != getNumberOfTasks()) {
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

    public void collectTaskNames(Set<KindOfDay> setOfTaskNames) {

        for (BeginEndTask task : tasks) {
            setOfTaskNames.add(task.getKindOfDay());
        }
    }
}
