package com.mango_apps.time15.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class uses SharedPreferences to store the start, end and pause values for each day.
 * All days of a month are saved in the same file.
 */
public class PrefStorage implements StorageFacade {

    @Override
    public boolean saveDaysDataNew(Activity activity, DaysDataNew data) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                getFilename(data.getId()), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(data.getId(), data.toString());
        editor.commit();

        return true;
    }

    @Override
    public DaysDataNew loadDaysDataNew(Activity activity, String id) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                getFilename(id), Context.MODE_PRIVATE);

        String s = sharedPref.getString(id, null);
        if (s == null) {
            return null;
        }
        DaysDataNew data = DaysDataNew.fromString(s);

        return data;
    }

    @Override
    public int loadBalance(Activity activity, String id) {
        return 0;
    }

    private String getFilename(String id) {
        return "com.mango_apps.time15.PREF_" + TimeUtils.getMonthYearOfID(id) + ".dat";
    }
}
