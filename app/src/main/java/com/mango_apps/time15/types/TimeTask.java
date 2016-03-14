package com.mango_apps.time15.types;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Created by andreas on 11.03.16.
 */
public class TimeTask implements Task {

    public static final int DUE_HOURS_PER_DAY = 8;

    public static final int DUE_TOTAL_MINUTES = DUE_HOURS_PER_DAY * 60;

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
        int difference = 0;
        int difference15 = 0;
        if (end != null && begin != null) {
            difference = end - begin;
            if (begin15 != null && end15 != null) {
                difference15 = end15 - begin15;
                if (difference15 < 0) {
                    difference--;
                    difference15 = 60 + difference15;
                }
            }
            if (pause != null) {
                int pauseTemp = pause;
                while (pauseTemp > 60) {
                    difference--;
                    pauseTemp -= 60;
                }
                difference15 -= pauseTemp;
                if (difference15 < 0) {
                    difference--;
                    difference15 = 60 + difference15;
                }
            }
        }
        return new Time15(difference, difference15);
    }

    @Override
    public int getBalance() {
        if (!KindOfDay.isDueDay(day)) {
            return 0;
        }

        int actualTotalMinutes = 0;

        Time15 actual = getTotal();
        actualTotalMinutes += actual.toMinutes();
        if (KindOfDay.WORKDAY_SOME_VACATION.equals(day)) {
            //TODO remove after migration actualTotalMinutes += 4 * 60;
        }
        return actualTotalMinutes - DUE_TOTAL_MINUTES;
    }

    @Override
    public String toString() {
        // TODO remove after migration:
        if (day.equals(KindOfDay.WORKDAY_SOME_VACATION)) {
            throw new IllegalStateException("WORKDAY_SOME_VACATION migration problem!");
        }
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
        // TODO remove after migration:
        if (timeTask.getKindOfDay().equals(KindOfDay.WORKDAY_SOME_VACATION)) {
            timeTask.setKindOfDay(KindOfDay.WORKDAY);
        }
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
