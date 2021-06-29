package com.mythosapps.time15.storage;

import android.app.Activity;

import com.mythosapps.time15.types.BalanceType;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;

/**
 * Storage facade to load and save a day's data.
 */
public interface StorageFacade {

    // TODO targeting api level 30, should use scoped storage:
    // TODO shoud upload to my website as backup
    // Documents/Time15 is already an app-specific/app-private directory on internal storage
    // for which no permissions are necessary in android manifest.
    // But: Let the User approve to use this directory, by presenting them ACTION_DIRECTORY_TREE
    // system file picker, initialized with the Documents/Time15 directory.
    // (Basically let the User choose whatever directory they want to use as a device backup dir,
    // but warn them that moving existing files from Documents/Time15 directory to a different
    // directory is not supported (why offer it?). I.e. if they choose a different directory,
    // all existing data will be invisible and they have to move it on their own. If they are
    // new to the app, then no worries anyways.)
    // Finally, persist the URI (the directory) the user has granted access to using the
    // ACTION_DIRECTORY_TREE system file picker in order for the choice to survive device restarts.
    // Of course, put all this under Settings UI.

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
     * @param balanceType
     * @return balance of month
     */
    int loadBalance(Activity activity, String id, BalanceType balanceType);

    /**
     * Load the sum for a task for over the month identified by id.
     */
    int loadTaskSum(Activity activity, String id, KindOfDay task);
}
