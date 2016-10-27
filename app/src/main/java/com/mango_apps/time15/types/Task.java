package com.mango_apps.time15.types;

/**
 * Created by andreas on 11.03.16.
 */
public interface Task {

    Time15 getTotal();

    String toString();

    Task copy();

    void setKindOfDay(KindOfDay day);

    KindOfDay getKindOfDay();

    boolean valid();
}
