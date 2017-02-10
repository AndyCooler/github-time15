package com.mythosapps.time15.storage;

import android.app.Activity;
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

public class ConfigFileStorage extends FileStorage implements ConfigStorageFacade {

    private static final String DEFAULT_CONFIG_FILE = "Time15.conf";

    private ConfigXmlParser parser = new ConfigXmlParser();

    private ConfigAssetStorage assetStorage;

    //TODO maybe default to xml from app, load from external if present, then merge with all existing kindOfDays

    public ConfigFileStorage() {
        assetStorage = new ConfigAssetStorage(parser);
    }

    public List<KindOfDay> loadConfigXml(Activity activity) {

        String filename = DEFAULT_CONFIG_FILE;

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
        Log.i(getClass().getName(), "Loaded " + result.size() + " entries from ConfigFileStorage.");

        // merge items: add items from asset config only if the are not yet present
        List<String> configStorageNames = new ArrayList<>();
        for (KindOfDay task : result) {
            configStorageNames.add(task.getDisplayString());
        }
        List<KindOfDay> assets = assetStorage.loadConfigXml(activity);
        for (KindOfDay task : assets) {
            if (!configStorageNames.contains(task.getDisplayString())) {
                result.add(task);
                Log.i(getClass().getName(), "Added task " + task.getDisplayString() + " from AssetStorage.");
            } else {
                Log.i(getClass().getName(), "Skipped task " + task.getDisplayString() + " from AssetStorage.");
            }
        }

        // TODO add all tasks found in data storage to KindOfDay.list
        // TODO when we add a new task from data store, default due minutes per task cannot be determined
        // TODO => better to not store default due minutes per task in KindOfDay (better make configurable once for all tasks)
        // TODO this way, read only name and determine isBeginEndType from begin/end values
        // TODO and let user define a color in their config file or (later) in a separate GUI

        return result;
    }


}