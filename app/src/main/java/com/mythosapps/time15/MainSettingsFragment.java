package com.mythosapps.time15;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class MainSettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences.OnSharedPreferenceChangeListener listener =
            new MainSettingsListener(this);

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.main_preferences, rootKey);
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        //Preference pref = getPreferenceScreen().findPreference("settings_cloud_backup_id");
        //pref.setEnabled(false);
        //sharedPreferences.edit().putBoolean("settings_cloud_backup_id_editable", false).apply();
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }
}
