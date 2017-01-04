package com.mythosapps.time15.types;

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
        // could be named isValid()
        return day != null && (isBeginEndTimeComplete() || isOnlyTotalComplete());
    }

    public boolean isBeginEndTimeComplete() {
        return end != null && begin != null && begin15 != null && end15 != null;
    }

    public boolean isOnlyTotalComplete() {
        // aka number task
        return end == null && begin == null && begin15 == null && end15 == null && total != null;
    }

    @Override
    public Time15 getTotal() {
        if (total == null) {
            calcTotal();
        }
        return total;
    }

    public void setTotal(Time15 total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return SEP + day.toString() + SEP + valueOf(begin) + SEP + valueOf(begin15) + SEP +
                valueOf(end) + SEP + valueOf(end15) + SEP +
                valueOf(pause) + SEP + (total == null ? "-" : total.toDecimalFormat());
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
        copy.setTotal(total == null ? null : Time15.fromMinutes(total.toMinutes()));
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
            String totalString = tokenizer.hasMoreElements() ? tokenizer.nextToken() : null;
            if (totalString == null) {
                beginEndTask.setTotal(null);
            } else {
                if (totalString.indexOf(".") > 0) {
                    beginEndTask.setTotal(Time15.fromDecimalFormat(totalString));
                } else {
                    // legacy format
                    beginEndTask.setTotal(Time15.fromMinutes(60 * Integer.valueOf(totalString)));
                }
            }
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

    public void calcTotal() {
        if (isBeginEndTimeComplete() && day != null) {
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
            total = new Time15(difference, difference15);
        } else if (isOnlyTotalComplete()) {
            // total is set, ok.
        } else {
            // task is incomplete, repair assuming 8 hours if not a due day
            if (!KindOfDay.isBeginEndType(day)) {
                total = Time15.fromMinutes(DaysDataNew.DUE_TOTAL_MINUTES);
            } else {
                total = Time15.fromMinutes(0);
            }
        }
    }
}
