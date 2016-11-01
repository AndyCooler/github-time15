package com.mango_apps.time15.types;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Created by andreas on 11.03.16.
 */
public class BeginEndTask implements Task {

    private static final String SEP = "#";

    private KindOfDay day;

    private Integer begin;

    private Integer end;

    private Integer begin15;

    private Integer end15;

    private Integer pause;

    private Time15 total;

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

    public KindOfDay getKindOfDay() {
        return day;
    }

    public void setKindOfDay(KindOfDay day) {
        this.day = day;
    }

    @Override
    public boolean isComplete() {
        return day != null && ((end != null && begin != null && begin15 != null && end15 != null)
                || (end == null && begin == null && begin15 == null && end15 == null && total != null));
    }

    @Override
    public Time15 getTotal() {
        if (!KindOfDay.isDueDay(day)) {
            return Time15.fromMinutes(DaysDataNew.DUE_TOTAL_MINUTES);
        }

        int difference = 0;
        int difference15 = 0;
        if (end != null && begin != null && begin15 != null && end15 != null) {
            difference = end - begin;
            difference15 = end15 - begin15;
            if (difference15 < 0) {
                difference--;
                difference15 = 60 + difference15;
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
        total = new Time15(difference, difference15); // TODO this is prep for merge of NumberTask into BeginEndTask
        return total;
    }

    public void setTotal(Time15 total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return SEP + day.toString() + SEP + valueOf(begin) + SEP + valueOf(begin15) + SEP +
                valueOf(end) + SEP + valueOf(end15) + SEP +
                valueOf(pause);
    }

    @Override
    public Task copy() {
        BeginEndTask copy = new BeginEndTask();
        copy.setKindOfDay(day);
        copy.setBegin(begin);
        copy.setEnd(end);
        copy.setBegin15(begin15);
        copy.setEnd15(end15);
        copy.setPause(pause);
        return copy;
    }

    private String valueOf(Integer value) {
        return (value == null) ? "-" : String.valueOf(value);
    }

    public static BeginEndTask fromString(String s) {

        BeginEndTask beginEndTask = new BeginEndTask();

        try {
            StringTokenizer tokenizer = new StringTokenizer(s, SEP);

            beginEndTask.setKindOfDay(KindOfDay.fromString(tokenizer.nextToken()));
            beginEndTask.setBegin(nextIntToken(tokenizer));
            beginEndTask.setBegin15(nextIntToken(tokenizer));
            beginEndTask.setEnd(nextIntToken(tokenizer));
            beginEndTask.setEnd15(nextIntToken(tokenizer));
            beginEndTask.setPause(nextIntToken(tokenizer));
        } catch (Throwable t) {
            // error while reading task from String, might result in Task.isComplete == false
        }
        return beginEndTask;
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
