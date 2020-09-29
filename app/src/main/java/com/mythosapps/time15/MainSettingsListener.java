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

               /* WorkContinuation continuation = workManager
                        .beginUniqueWork(ReminderWorker.REMINDER_ACTION_WORK_NAME,
                                ExistingWorkPolicy.KEEP,
                                OneTimeWorkRequest.from(ReminderWorker.class));*/
                PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(ReminderWorker.class, 20, TimeUnit.SECONDS).build();

                workManager.enqueueUniquePeriodicWork(ReminderWorker.REMINDER_ACTION_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, periodicWork);
                Snackbar.make(fragment.getActivity().findViewById(R.id.settingsFrame), "Notifications on",
                        Snackbar.LENGTH_LONG).show();
            } else {
                workManager.cancelUniqueWork(ReminderWorker.REMINDER_ACTION_WORK_NAME);
                Snackbar.make(fragment.getActivity().findViewById(R.id.settingsFrame), "Notifications off",
                        Snackbar.LENGTH_LONG).show();
            }

        }
    }
}
