package com.mango_apps.time15.types;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Created by andreas on 11.03.16.
 */
public class NumberTask implements Task {

    private static final String SEP = "#";

    private KindOfDay day;

    Time15 total;

    public void setTotal(Time15 total) {
        this.total = total;
    }

    @Override
    public Time15 getTotal() {
        return total;
    }

    @Override
    public int getBalance() {
        return total.toMinutes();
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
    public String toString() {
        return SEP + day.toString() + SEP + valueOf(total);
    }

    @Override
    public Task copy() {
        NumberTask copy = new NumberTask();
        copy.setKindOfDay(day);
        copy.setTotal(Time15.fromMinutes(total.toMinutes()));
        return copy;
    }

    private String valueOf(Time15 value) {
        return (value == null) ? "-" : String.valueOf(value.getHours());
    }

    public static NumberTask fromString(String s) {

        StringTokenizer tokenizer = new StringTokenizer(s, SEP);
        NumberTask numberTask = new NumberTask();
        String token = tokenizer.nextToken();
        numberTask.setKindOfDay(KindOfDay.fromString(token));
        Integer totalInteger = nextIntToken(tokenizer);
        if (totalInteger == null) {
            totalInteger = 0;
        }
        numberTask.setTotal(Time15.fromMinutes(totalInteger * 60));

        return numberTask;
    }

    private static Integer nextIntToken(StringTokenizer tokenizer) {
        String token = null;
        try {
            token = tokenizer.nextToken();
        } catch (NoSuchElementException e) {
            return null; // noop migration: allow self-repair of data next time it's saved
        }
        return nextIntToken0(token);
    }

    private static Integer nextIntToken0(String token) {
        if (token == null || "null".equals(token) || "-".equals(token)) {
            return null;
        }
        return Integer.valueOf(token);
    }
}
