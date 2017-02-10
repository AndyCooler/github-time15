package com.mythosapps.time15.storage;

import android.util.Log;

import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.util.ConfigXmlParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andreas on 09.02.17.
 */

public class ConfigFileStorage extends FileStorage {

    private ConfigXmlParser parser = new ConfigXmlParser();

    //TODO maybe default to xml from app, load from external if present, then merge with all existing kindOfDays

    public List<KindOfDay> loadConfigXml(String filename) {

        if (!initialized && !init()) {
            return null;
        }

        File file = new File(storageDir, filename);
        if (!file.exists()) {
            Log.w(getClass().getName(), "loadConfigXml : file not found " + filename);
            return null;
        }

        List<KindOfDay> result = new ArrayList<>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            result = parser.parse(fis);
        } catch (IOException e) {
            Log.e(getClass().getName(), "Error loading config from file " + filename, e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return result;
    }
}