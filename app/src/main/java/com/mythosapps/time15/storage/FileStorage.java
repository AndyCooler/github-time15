package com.mythosapps.time15.storage;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.core.app.ActivityCompat;

import java.io.File;

/**
 * Created by andreas on 08.06.16.
 */
public class FileStorage {

    public static final String STORAGE_DIR = "Time15";

    protected boolean initialized = false;

    protected File storageDir;

    protected boolean init(Activity activity) {
        verifyStoragePermissions(activity);
        if (isExternalStorageWritable()) {
            if (isStorageDirPresent()) {
                storageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS) + File.separator + STORAGE_DIR);
                return true;
            } else {
                storageDir = createStorageDir();
                if (storageDir == null) {
                } else {
                    initialized = true;
                    return true;
                }
            }
        }
        return false;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    protected boolean isStorageDirPresent() {

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS) + File.separator + STORAGE_DIR);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public File createStorageDir() {

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS) + File.separator + STORAGE_DIR);
        if (file.mkdirs()) {
        } else {
        }
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
