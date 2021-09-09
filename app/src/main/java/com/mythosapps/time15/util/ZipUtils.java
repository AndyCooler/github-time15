package com.mythosapps.time15.util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.mythosapps.time15.storage.ConfigFileStorage;
import com.mythosapps.time15.storage.FileStorage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
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
    public static byte[] backupToBytes(String backupMoment) throws Exception {

        File storageDir = FileStorage.getStorageDir();
        String tempFileName = backupToZip(storageDir, "toBytes_" + backupMoment);

        File tempFile = new File(storageDir, tempFileName);
        byte[] result = Files.readAllBytes(tempFile.toPath());
        tempFile.delete();

        return result;
    }
}
