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

    public static void sendEmail(Activity activity, String filename, File storageDir, String subject) {

        // https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Time15 backup data " + subject);
        activity.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
