package com.mango_apps.time15.storage;

import java.util.HashSet;
import java.util.Set;
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

    public KindOfDay getDay() {
        return day;
    }

    public void setDay(KindOfDay day) {
        this.day = day;
    }

    @Override
    public String toString() {
        String s = String.valueOf(begin) + SEP + String.valueOf(begin15) + SEP +
                String.valueOf(end) + SEP + String.valueOf(end15) + SEP +
                String.valueOf(pause) + SEP + day.toString();
        return s;
    }

    public static DaysData fromString(String id, String s) {
        DaysData data = new DaysData(id);
        StringTokenizer tokenizer = new StringTokenizer(s, SEP);
        data.setBegin(nextIntToken(tokenizer));
        data.setBegin15(nextIntToken(tokenizer));
        data.setEnd(nextIntToken(tokenizer));
        data.setEnd15(nextIntToken(tokenizer));
        data.setPause(nextIntToken(tokenizer));
        data.setDay(KindOfDay.fromString(tokenizer.nextToken()));
        return data;
    }

    private static Integer nextIntToken(StringTokenizer tokenizer) {
        String token = tokenizer.nextToken();
        if (token == null || "null".equals(token)) {
            return null;
        }
        return Integer.valueOf(token);
    }
}
