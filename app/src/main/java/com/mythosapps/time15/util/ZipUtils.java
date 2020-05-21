package com.mythosapps.time15.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private static final int BUFFER = 8192;

    public static void createZipFile(File storageDir, String zipArchiveFilename, String[] allFiles) throws Exception {

        try (FileOutputStream dest = new FileOutputStream(new File(storageDir, zipArchiveFilename));
             ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {

            byte data[] = new byte[BUFFER];

            for (String filename : allFiles) {

                //Log.i("add:", filename);
                //Log.i("Compress", "Adding: " + filename);
                try (FileInputStream fi = new FileInputStream(new File(storageDir, filename));
                     BufferedInputStream origin = new BufferedInputStream(fi, BUFFER)) {
                    ZipEntry entry = new ZipEntry(filename.substring(filename.lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                }
            }
        }
    }
}
