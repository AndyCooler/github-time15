package com.mythosapps.time15.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Utility class to send an email.
 */

public final class EmailUtils {

    public static void sendEmail(Activity activity, String filename, File storageDir, String subject) {

        File file = new File(storageDir, filename);
        Uri path = Uri.fromFile(file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"andreas.kohler.76@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);

        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);

        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Time15 data " + subject);
        activity.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}