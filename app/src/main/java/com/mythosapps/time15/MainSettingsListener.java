package com.mythosapps.time15;

import android.content.SharedPreferences;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.TimeUnit;

public class MainSettingsListener implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final MainSettingsFragment fragment;
    private WorkManager workManager;

    public MainSettingsListener(MainSettingsFragment fragment) {
        this.fragment = fragment;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if ("setting_reminder_notifications_active".equals(key)) {
            if (workManager == null) {
                workManager = WorkManager.getInstance(fragment.getContext());
            }
            if (sharedPreferences.getBoolean("setting_reminder_notifications_active", true)) {

                PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(ReminderWorker.class, 24, TimeUnit.HOURS)
                        .addTag(ReminderWorker.REMINDER_ACTION_WORK_NAME)
                        .build();

                workManager.enqueueUniquePeriodicWork(ReminderWorker.REMINDER_ACTION_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, periodicWork);
                Snackbar.make(fragment.getActivity().findViewById(R.id.settingsFrame), "Notifications on",
                        Snackbar.LENGTH_LONG).show();
            } else {
                workManager.cancelAllWorkByTag(ReminderWorker.REMINDER_ACTION_WORK_NAME);
                Snackbar.make(fragment.getActivity().findViewById(R.id.settingsFrame), "Notifications off",
                        Snackbar.LENGTH_LONG).show();
            }

        }
    }
}
