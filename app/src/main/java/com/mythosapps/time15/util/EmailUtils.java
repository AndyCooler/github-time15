package com.mythosapps.time15.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;

import java.io.File;

/**
 * Utility class to send an email.
 */

public final class EmailUtils {

    public static void sendEmail(Activity activity, String filename, File storageDir, String subject, String sendToAddress) {

        // https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File file = new File(storageDir, filename);
        Uri path = Uri.fromFile(file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {sendToAddress};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);

        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);

        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hallo,\n\n das ist eine automatisch generierte Email der Time15 Zeiterfassungs-App. Version: " + AppVersion.getVersionName(activity) + "\n"
                + "Im Anhang ist ein Backup mit allen Daten, erstellt am " + subject.substring(14).replaceFirst("_", " um ")
                + " Uhr.\n Es ist weiterhin eine normale Datei im ZIP Format. Als Endung wurde statt .zip nun .time15 gewählt, um das Wiederherstellen zu erleichtern.\n"
                + " Die Datei enthält alle erfassten Einträge, für jeden Monat eine .csv Datei."
                + "\n\nViele Grüße,\nAndreas");
        activity.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
