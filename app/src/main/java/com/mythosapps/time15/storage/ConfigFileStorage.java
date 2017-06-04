package com.mythosapps.time15.storage;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.util.ConfigXmlParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andreas on 09.02.17.
 */

public class ConfigFileStorage extends FileStorage implements ConfigStorageFacade {

    private static final String DEFAULT_CONFIG_FILE = "Time15.conf";

    private ConfigXmlParser parser = new ConfigXmlParser();

    private ConfigAssetStorage assetStorage;
    private Activity activity;

    //TODO maybe default to xml from app, load from external if present, then merge with all existing kindOfDays

    public ConfigFileStorage() {
        assetStorage = new ConfigAssetStorage(parser);
    }

    public List<KindOfDay> loadConfigXml(Activity activity) {

        this.activity = activity;

        List<KindOfDay> result = new ArrayList<>();

        KindOfDay.addTaskTypes(assetStorage.loadConfigXml(activity));

        String filename = DEFAULT_CONFIG_FILE;

        if (!initialized && !init()) {
            return result;
        }

        File file = new File(storageDir, filename);
        if (!file.exists()) {
            Log.w(getClass().getName(), "loadConfigXml : file not found " + filename);
            return result;
        }

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

        // TODO add all tasks found in data storage to KindOfDay.list
        // TODO when we add a new task from data store, default due minutes per task cannot be determined
        // TODO => better to not store default due minutes per task in KindOfDay (better make configurable once for all tasks)
        // TODO this way, read only name and determine isBeginEndType from begin/end values
        // TODO and let user define a color in their config file or (later) in a separate GUI

        return result;
    }

    @Override
    public boolean saveExternalConfigXml(Activity activity, List<KindOfDay> tasks) {
        String filename = DEFAULT_CONFIG_FILE;

        this.activity = activity;

        if (!initialized && !init()) {
            return false;
        }

        File file = new File(storageDir, filename);
        boolean result = false;
        try {
            FileOutputStream fos = new FileOutputStream(file, false);

            PrintWriter pw = new PrintWriter(fos);
            pw.println(XML_PROLOG);
            for (KindOfDay task : tasks) {
                pw.println(task.toXmlConfig());
            }
            pw.println(XML_END);
            pw.flush();
            pw.close();
            fos.close();

            Log.i(getClass().getName(), "Saved file " + filename);
            result = true;
        } catch (IOException e) {
            fatal("saveExternalConfigXml", "Error saving file " + filename);
            Log.e(getClass().getName(), "Error saving file " + filename + " as " + file.getAbsolutePath(), e);
        }
        return result;
    }


    private void fatal(String method, String msg) {
        Log.e(getClass().getName(), method + " : " + msg);

        if (activity != null) {
            Toast.makeText(activity, method + " : " + msg, Toast.LENGTH_SHORT).show();
        }
    }
}