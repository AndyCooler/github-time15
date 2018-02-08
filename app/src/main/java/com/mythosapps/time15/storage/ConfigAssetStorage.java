package com.mythosapps.time15.storage;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.util.ConfigXmlParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads config from file within the app ("asset").
 */
public class ConfigAssetStorage implements ConfigStorageFacade {

    private static final String DEFAULT_ASSET_FILE = "Time15.conf";

    private final ConfigXmlParser parser;

    private boolean saveActionProcessed = false;

    private String saveActionXml = "";

    public ConfigAssetStorage(ConfigXmlParser parser) {
        this.parser = parser;
    }

    public List<KindOfDay> loadConfigXml(Activity activity) {

        String resourceFileName = DEFAULT_ASSET_FILE;
        List<KindOfDay> result = new ArrayList<>();

        try {
            InputStream stream = null;
            if (saveActionProcessed) {
                stream = new ByteArrayInputStream(saveActionXml.getBytes());
                Log.i(getClass().getName(), "Loading from cached XML.");
                result = parser.parse(stream);
            } else {
                Log.i(getClass().getName(), "Loading from AssetStorage.");
                AssetManager manager = activity.getAssets();
            stream = manager.open(resourceFileName);
            result = parser.parse(stream);
                // save/load again to ensure same order at initial load as when after a save
                saveExternalConfigXml(activity, result);
                stream = new ByteArrayInputStream(saveActionXml.getBytes());
                result = parser.parse(stream);
            }

        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
        }
        Log.i(getClass().getName(), "Loaded " + result.size() + " entries from AssetStorage.");
        return result;
    }

    @Override
    public boolean saveExternalConfigXml(Activity activity, List<KindOfDay> tasks) {

        saveActionProcessed = true;

        boolean result = false;
        try {
            StringBuilder xml = new StringBuilder(XML_PROLOG);

            for (KindOfDay task : tasks) {
                xml.append(task.toXmlConfig());
            }
            xml.append(XML_END);

            saveActionXml = xml.toString();
            Log.i(getClass().getName(), "Saved XML : \n" + xml);
            result = true;
        } catch (Throwable e) {
            Log.e(getClass().getName(), "Error saving XML ", e);
        }
        return result;
    }
}
