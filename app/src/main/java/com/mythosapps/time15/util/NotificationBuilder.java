package com.mythosapps.time15.util;

import static androidx.core.app.NotificationCompat.CATEGORY_REMINDER;
import static com.mythosapps.time15.MainActivity.EXTRA_MESSAGE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.mythosapps.time15.MainActivity;
import com.mythosapps.time15.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class NotificationBuilder {

    public static final int TIME15_NOTIFICATION_ID = 151515;

    static int icon = R.drawable.ic_stat_time15_notification;

    static NotificationCompat.Builder builder;

    static NotificationChannel channel;

    public static Notification buildForId(String id, Context context) {

        Calendar cal = GregorianCalendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);

        System.out.println("time15.notification for " + id);
        CharSequence contentText = id + " noch eintragen";

        if (builder == null) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(EXTRA_MESSAGE, id);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 123, intent, PendingIntent.FLAG_IMMUTABLE);

            builder = new NotificationCompat.Builder(context, Context.NOTIFICATION_SERVICE)
                    .setSmallIcon(icon)
                    .setContentTitle("Zeiten Eintragen")
                    .setContentText(contentText)
                    .setOnlyAlertOnce(true)
                    //.setShowWhen(false)
                    //.setExtras()
                    .setOngoing(false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(CATEGORY_REMINDER);
        } else {
            builder.setContentText(contentText);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // ab Android Oreo mit Channel:

            if (channel == null) {
                channel = new NotificationChannel("time15_channel", "time15_channel", NotificationManager.IMPORTANCE_HIGH);
            }
            NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.createNotificationChannel(channel);
            builder.setChannelId("time15_channel");
        }
        return builder.build();
    }
}
