package com.mango_apps.time15.types;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimerTask;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void getTask(int taskNo) {
        tasks.get(taskNo);
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
        TimeTask timeTask = TimeTask.fromString(tokenizer.nextToken() +
                SEP + tokenizer.nextToken() + SEP + tokenizer.nextToken() +
                SEP + tokenizer.nextToken() + SEP + tokenizer.nextToken() +
                SEP + tokenizer.nextToken());
        data.addTask(timeTask);
        if (tokenizer.hasMoreElements()) {
            NumberTask numberTask = NumberTask.fromString(tokenizer.nextToken() +
                    SEP + tokenizer.nextToken());
            data.addTask(numberTask);
        }
        return data;
    }
}
