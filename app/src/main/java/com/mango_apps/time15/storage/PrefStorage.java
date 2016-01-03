package com.mango_apps.time15.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.mango_apps.time15.R;
import com.mango_apps.time15.util.TimeUtils;

/**
 * This class stores the start, end and pause values for each day. All days of a month are saved in the same file.
 */
public class PrefStorage {


    public boolean saveDaysData(Activity activity, DaysData data) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                getFilename(data.getId()), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(data.getId(), data.toString());
        editor.commit();

        return true;
    }

    public DaysData loadDaysData(Activity activity, String id) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                getFilename(id), Context.MODE_PRIVATE);

        String s = sharedPref.getString(id, null);
        if (s == null) {
            return null;
        }
        DaysData data = DaysData.fromString(id, s);

        return data;
    }


    private String getFilename(String id) {
        return "com.mango_apps.time15.PREF_" + TimeUtils.getMonthYearOfID(id) + ".dat";
    }
}
