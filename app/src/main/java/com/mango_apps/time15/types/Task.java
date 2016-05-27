package com.mango_apps.time15.types;

/**
 * Created by andreas on 11.03.16.
 */
public interface Task {

    Time15 getTotal();

    int getBalance();

    void setKindOfDay(KindOfDay day);

    KindOfDay getKindOfDay();

    String toString();

    Task copy();
}
