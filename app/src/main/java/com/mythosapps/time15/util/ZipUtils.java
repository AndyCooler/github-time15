package com.mythosapps.time15.util;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.mythosapps.time15.storage.ConfigFileStorage;
import com.mythosapps.time15.storage.FileStorage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private static final int BUFFER = 8192;

    public static final FilenameFilter EXPORT_FILE_FILTER = (dir, name) -> name.endsWith(".csv") || name.equals(ConfigFileStorage.DEFAULT_CONFIG_FILE);

    public static void createZipFile(File storageDir, String zipArchiveFilename, String[] allFiles) throws Exception {
        File f = new File(storageDir, zipArchiveFilename);
        try (FileOutputStream dest = new FileOutputStream(f);
             ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {

            byte data[] = new byte[BUFFER];
            int i = 0;
            for (String filename : allFiles) {
                i++;
                try (FileInputStream fi = new FileInputStream(new File(storageDir, filename));
                     BufferedInputStream origin = new BufferedInputStream(fi, BUFFER)) {
                    ZipEntry entry = new ZipEntry(filename.substring(filename.lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    Log.i("time15", "Added: " + filename + " : " + i + " size: " + entry.getSize() + "  compressedSize: " + entry.getCompressedSize());
                }
            }
            Log.i("time15", "zip file: " + f.getName() + " : " + f.length());

        }
    }

    public static String backupToZip(File storageDir, String backupMoment) throws Exception {

        String[] allFiles = storageDir.list(EXPORT_FILE_FILTER);
        String zipArchiveFilename = "Time15_Backup_" + backupMoment + ".zip";
        createZipFile(storageDir, zipArchiveFilename, allFiles);
        return zipArchiveFilename;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] backupToBytes(String backupMoment, Activity activity) throws Exception {

        File storageDir = FileStorage.getStorageDir(activity);
        String tempFileName = backupToZip(storageDir, "toBytes_" + backupMoment);

        File tempFile = new File(storageDir, tempFileName);
        byte[] result = Files.readAllBytes(tempFile.toPath());
        tempFile.delete();

        return result;
    }

    public static String restoreFromBytes(byte[] zipBytes, Activity activity) {
        File storageDir = FileStorage.getStorageDir(activity);

        int numRestored = 0;
        int numSkipped = 0;
        ZipEntry next = null;
        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            next = in.getNextEntry();
            while (next != null) {
                String filename = next.getName();
                File file = new File(storageDir, filename);
                String canonicalPath = file.getCanonicalPath();
                if (!canonicalPath.startsWith(storageDir.getCanonicalPath())) {
                    throw new IOException("security exception: Zip Path Traversal Vulnerability");
                }
                if (file.exists()) {
                    numSkipped++;
                } else {
                    byte data[] = new byte[BUFFER];
                    int count;
                    try (FileOutputStream fo = new FileOutputStream(file)) {
                        while ((count = in.read(data)) != -1) {
                            fo.write(data, 0, count);
                        }
                    }
                    in.closeEntry();
                    numRestored++;
                }
                next = in.getNextEntry();
            }
            //Toast.makeText(activity.getApplicationContext(), "Found " + numEntries + " # total, " + numExists + " #exists", Toast.LENGTH_SHORT).show();
            //Toast.makeText(activity.getApplicationContext(), "Found " + storageDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            return "Erfolg! Monate " + numRestored + " / " + numSkipped + " (restored / skipped)";
        } catch (IOException e) {
            e.printStackTrace();
            return " Error: " + e.getMessage();
        }
    }
}
