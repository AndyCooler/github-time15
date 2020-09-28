package com.mythosapps.time15;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.mythosapps.time15.util.NotificationBuilder;
import com.mythosapps.time15.util.TimeUtils;

import java.util.List;

public class CheckDaysAlarmReceiver extends BroadcastReceiver {

    private SharedPreferences sharedPreferences;
    private ReminderAction reminderAction;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        if (sharedPreferences.getBoolean("setting_reminder_notifications_active", true)) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (notificationManager.areNotificationsEnabled()) { // TODO relates to setting just checked, how to only check one condition?

                if (reminderAction == null) {
                    reminderAction = new ReminderAction(null);
                }

                List<String> missingEntries = reminderAction.remindOfLastWeeksEntries();
                if (!missingEntries.isEmpty()) {
                    Notification notification = NotificationBuilder.buildForId(TimeUtils.createID(), context);
                    notificationManager.notify(NotificationBuilder.TIME15_NOTIFICATION_ID, notification);
                }
            }
        }
    }
}
