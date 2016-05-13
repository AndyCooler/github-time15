package com.mango_apps.time15.types;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Persistent entity class to store the data of one day.
 */
public class DaysData {

    private String id;

    private Integer begin;

    private Integer end;

    private Integer begin15;

    private Integer end15;

    private Integer pause;

    private KindOfDay day;

    /**
     * Number of hours spent in other activity.
     */
    private Integer otherHours;

    private static final String SEP = "#";

    public DaysData(String id) {
        this.id = id;
        day = KindOfDay.WORKDAY;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Integer getOtherHours() {
        return otherHours;
    }

    public void setOtherHours(Integer otherHours) {
        this.otherHours = otherHours;
    }

    public KindOfDay getDay() {
        return day;
    }

    public void setDay(KindOfDay day) {
        this.day = day;
        otherHours = KindOfDay.WORKDAY_SOME_VACATION.equals(day) ? 4 : null;
    }

    @Override
    public String toString() {
        String s = id + SEP + day.toString() + SEP + valueOf(begin) + SEP + valueOf(begin15) + SEP +
                valueOf(end) + SEP + valueOf(end15) + SEP +
                valueOf(pause) + SEP + valueOf(otherHours);
        return s;
    }

    private String valueOf(Integer value) {
        return (value == null) ? "-" : String.valueOf(value);
    }

    public static DaysData fromString(String s) {

        StringTokenizer tokenizer = new StringTokenizer(s, SEP);
        DaysData data = new DaysData(tokenizer.nextToken());
        data.setDay(KindOfDay.fromString(tokenizer.nextToken()));
        data.setBegin(nextIntToken(tokenizer));
        data.setBegin15(nextIntToken(tokenizer));
        data.setEnd(nextIntToken(tokenizer));
        data.setEnd15(nextIntToken(tokenizer));
        data.setPause(nextIntToken(tokenizer));
        data.setOtherHours(nextIntToken(tokenizer));

        return data;
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

    public boolean isEqualToNewData(DaysDataNew dataNew) {
        boolean result = false;
        if (!(dataNew.getTask(0) instanceof BeginEndTask)) {
            return false;
        }

        BeginEndTask task0 = (BeginEndTask) dataNew.getTask(0);

        result |= begin.equals(task0.getBegin());
        result |= begin15.equals(task0.getBegin15());
        result |= end.equals(task0.getEnd());
        result |= end15.equals(task0.getEnd15());

        if (otherHours != null && otherHours > 0) {
            if (day.equals(KindOfDay.WORKDAY_SOME_VACATION)) {
                if (dataNew.getTask(1) == null || !(dataNew.getTask(1) instanceof NumberTask)) {
                    return false;
                }
                NumberTask task1 = (NumberTask) dataNew.getTask(1);
                result |= KindOfDay.VACATION.equals(task1.getKindOfDay());
                result |= otherHours * 60 == task1.getTotal().toMinutes();
            } else {
                return false;
            }
        } else {
            result |= day.equals(task0.getKindOfDay());
            if (dataNew.getTask(1) != null) {
                return false;
            }
        }

        return result;
    }
}
