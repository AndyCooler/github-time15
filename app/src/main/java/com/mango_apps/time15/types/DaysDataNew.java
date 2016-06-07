package com.mango_apps.time15.types;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by andreas on 12.03.16.
 */
public class DaysDataNew {

    private String id;

    private final ArrayList<Task> tasks;

    private static final String SEP = "#";

    public DaysDataNew(String id) {
        this.id = id;
        this.tasks = new ArrayList<Task>();
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

    public void addTask(Task task) {
        tasks.add(task);
    }

    public Task getTask(int taskNo) {
        return taskNo < tasks.size() ? tasks.get(taskNo) : null;
    }

    public int getBalance() {
        int balance = 0;
        for (Task task : tasks) {
            balance += task.getBalance();
        }
        return balance;
    }

    public Time15 getTotal() {
        Time15 total = Time15.fromMinutes(0);
        for (Task task : tasks) {
            total.plus(task.getTotal());
        }
        return total;
    }

    @Override
    public String toString() {
        String s = id;
        for (Task task : tasks) {
            s += task.toString();
        }
        return s;
    }

    public static DaysDataNew fromString(String s) {

        // TODO das ist erstmal nur fuer 1 bis 2 Tasks pro DaysData
        StringTokenizer tokenizer = new StringTokenizer(s, SEP);
        DaysDataNew data = new DaysDataNew(tokenizer.nextToken());
        BeginEndTask beginEndTask = BeginEndTask.fromString(tokenizer.nextToken() +
                SEP + tokenizer.nextToken() + SEP + tokenizer.nextToken() +
                SEP + tokenizer.nextToken() + SEP + tokenizer.nextToken() +
                SEP + tokenizer.nextToken());
        data.addTask(beginEndTask);
        if (tokenizer.hasMoreElements()) {
            String zweiter = tokenizer.nextToken();
            if (!"-".equals(zweiter)) {
                NumberTask numberTask = NumberTask.fromString(zweiter + (tokenizer.hasMoreElements() ? (SEP + tokenizer.nextToken()) : ""));
                data.addTask(numberTask);
            }
        }

        // TODO Idee: erst KindOfDay einlesen, dann je nach KindOfDay einen BeginEndTask oder NumberTask einlesen
        return data;
    }

    public int getNumberOfTasks() {
        return tasks.size();
    }
}
