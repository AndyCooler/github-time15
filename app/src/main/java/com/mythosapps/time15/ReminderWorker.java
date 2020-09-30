package com.mythosapps.time15;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mythosapps.time15.util.NotificationBuilder;

import java.util.List;

public class ReminderWorker extends Worker {

    public static final String REMINDER_ACTION_WORK_NAME = "time15.reminderAction";

    private SharedPreferences sharedPreferences;
    private ReminderAction reminderAction;

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }
        if (sharedPreferences.getBoolean("setting_reminder_notifications_active", true)) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            if (notificationManager.areNotificationsEnabled()) { // TODO relates to setting just checked, how to only check one condition?

                if (reminderAction == null) {
                    reminderAction = new ReminderAction(null);
                }

                List<String> missingEntries = reminderAction.remindOfLastWeeksEntries();
                if (!missingEntries.isEmpty()) {
                    notificationManager.cancel(NotificationBuilder.TIME15_NOTIFICATION_ID);

                    Notification notification = NotificationBuilder.buildForId(missingEntries.get(0), getApplicationContext());
                    notificationManager.notify(NotificationBuilder.TIME15_NOTIFICATION_ID, notification);
                }
            }
        }

        return Result.success();
    }
}
