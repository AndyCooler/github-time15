package com.mythosapps.time15.storage;

import android.os.Environment;

import java.io.File;

/**
 * Created by andreas on 08.06.16.
 */
public class FileStorage {

    public static final String STORAGE_DIR = "Time15";

    protected boolean initialized = false;

    protected File storageDir;

    protected boolean init() {
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
}
