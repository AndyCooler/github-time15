package com.mango_apps.time15.types;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Created by andreas on 11.03.16.
 */
public class TimeTask implements Task {

    private static final String SEP = "#";

    private Integer begin;

    private Integer end;

    private Integer begin15;

    private Integer end15;

    private Integer pause;

    private KindOfDay day;

    public Integer getBegin() {
        return begin;
    }

    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Integer getBegin15() {
        return begin15;
    }

    public void setBegin15(Integer begin15) {
        this.begin15 = begin15;
    }

    public Integer getEnd15() {
        return end15;
    }

    public void setEnd15(Integer end15) {
        this.end15 = end15;
    }

    public Integer getPause() {
        return pause;
    }

    public void setPause(Integer pause) {
        this.pause = pause;
    }

    @Override
    public KindOfDay getKindOfDay() {
        return day;
    }

    @Override
    public void setKindOfDay(KindOfDay day) {
        this.day = day;
    }

    @Override
    public Time15 getTotal() {
        return null;
    }

    @Override
    public int getBalance() {
        return 0;
    }

    @Override
    public String toString() {
        return SEP + day.toString() + SEP + valueOf(begin) + SEP + valueOf(begin15) + SEP +
                valueOf(end) + SEP + valueOf(end15) + SEP +
                valueOf(pause);
    }

    private String valueOf(Integer value) {
        return (value == null) ? "-" : String.valueOf(value);
    }

    public static TimeTask fromString(String s) {

        StringTokenizer tokenizer = new StringTokenizer(s, SEP);
        TimeTask timeTask = new TimeTask();
        timeTask.setKindOfDay(KindOfDay.fromString(tokenizer.nextToken()));
        timeTask.setBegin(nextIntToken(tokenizer));
        timeTask.setBegin15(nextIntToken(tokenizer));
        timeTask.setEnd(nextIntToken(tokenizer));
        timeTask.setEnd15(nextIntToken(tokenizer));
        timeTask.setPause(nextIntToken(tokenizer));

        return timeTask;
    }

    private static Integer nextIntToken(StringTokenizer tokenizer) {
        String token = null;
        try {
            token = tokenizer.nextToken();
        } catch (NoSuchElementException e) {
            return null; // noop migration: allow self-repair of data next time it's saved
        }
        if (token == null || "null".equals(token) || "-".equals(token)) {
            return null;
        }
        return Integer.valueOf(token);
    }
}
