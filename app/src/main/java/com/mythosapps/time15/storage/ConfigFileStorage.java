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
 *
 * Default to xml asset config from app. Try and load from external xml config file,
 * then merge with asset config from app by first adding tasks from external config file, second
 * adding tasks from asset config.
 */

public class ConfigFileStorage extends FileStorage implements ConfigStorageFacade {

    public static final String DEFAULT_CONFIG_FILE = "Time15.conf";

    private ConfigXmlParser parser = new ConfigXmlParser();

    private ConfigAssetStorage assetStorage;
    private Activity activity;

    public ConfigFileStorage() {
        assetStorage = new ConfigAssetStorage(parser);
    }

    /**
     * Load config from external XML file {@link #DEFAULT_CONFIG_FILE} if present. Loaded
     * tasks are activated as a side effect. In addition, returns a list of tasks from app storage
     * (asset storage) that can be activated by the caller. This way, the loaded tasks are always
     * first and override tasks from asset storage.
     *
     * @param activity
     * @return config loaded from external XML blended with asset config, or in
     * case of error, only asset config
     */
    public List<KindOfDay> loadConfigXml(Activity activity) {

        this.activity = activity;

        List<KindOfDay> defaultAssetConfig = assetStorage.loadConfigXml(activity);
        List<KindOfDay> loadedConfig = new ArrayList<KindOfDay>();

        String filename = DEFAULT_CONFIG_FILE;

        if (!initialized && !init(activity)) {
            fatal("loadConfigXml", "Error loading file " + filename);
            return defaultAssetConfig;
        }

        File file = new File(storageDir, filename);
        if (!file.exists()) {
            Log.w(getClass().getName(), "loadConfigXml : file not found " + filename);
            return defaultAssetConfig;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            loadedConfig = parser.parse(fis);
            if (loadedConfig != null) {
                // loaded config is first, thus overrides asset
                KindOfDay.addTaskTypes(loadedConfig);
            }

        } catch (Throwable e) {
            fatal("loadConfigXml", "Error loading config from file " + filename + " " + e.getMessage());
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
        Log.i(getClass().getName(), "Loaded " + loadedConfig.size() + " entries from ConfigFileStorage.");

        return defaultAssetConfig;
    }

    @Override
    public boolean saveExternalConfigXml(Activity activity, List<KindOfDay> tasks) {
        String filename = DEFAULT_CONFIG_FILE;

        this.activity = activity;

        if (!initialized && !init(activity)) {
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
            Toast.makeText(activity.getApplicationContext(), method + " : " + msg, Toast.LENGTH_SHORT).show();
        }
    }
}