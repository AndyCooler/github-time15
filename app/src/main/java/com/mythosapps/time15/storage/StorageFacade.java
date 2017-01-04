package com.mythosapps.time15.storage;

import android.app.Activity;

import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;

import java.util.Set;

/**
 * Storage facade to load and save a day's data.
 */
public interface StorageFacade {

    /**
     * Save a day's data.
     *
     * @param activity parent activity
     * @param data     the data to save
     * @return
     */
    boolean saveDaysDataNew(Activity activity, DaysDataNew data);

    /**
     * Load a day's data.
     *
     * @param activity parent activity
     * @param id       id of data to load
     * @return the day's data
     */
    DaysDataNew loadDaysDataNew(Activity activity, String id);

    /**
     * Load all day's data of the id's month and calculate the balance.
     *
     * @param activity parent activity
     * @param id       id in a certain month
     * @return balance of month
     */
    int loadBalance(Activity activity, String id);

    /**
     * Load the name of all tasks of a month identified by id.
     */
    Set<String> loadTaskNames(Activity activity, String id);

    /**
     * Load the name of all tasks of a month identified by id.
     */
    int loadTaskSum(Activity activity, String id, KindOfDay task);
}
