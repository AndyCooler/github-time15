package com.mango_apps.time15.storage;

import android.app.Activity;

/**
 * Storage facade to load and save a day's data.
 */
public interface StorageFacade {

    /**
     * Save a day's data.
     * @param activity parent activity
     * @param data the data to save
     * @return
     */
    boolean saveDaysData(Activity activity, DaysData data);

    /**
     * Load a day's data.
     * @param activity parent activity
     * @param id id of data to load
     * @return the day's data
     */
    DaysData loadDaysData(Activity activity, String id);
}
