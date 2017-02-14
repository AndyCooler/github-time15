package com.mythosapps.time15.storage;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.util.ConfigXmlParser;

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

    public ConfigAssetStorage(ConfigXmlParser parser) {
        this.parser = parser;
    }

    public List<KindOfDay> loadConfigXml(Activity activity) {

        String resourceFileName = DEFAULT_ASSET_FILE;

        AssetManager manager = activity.getAssets();
        InputStream stream;
        List<KindOfDay> result = new ArrayList<>();

        try {
            stream = manager.open(resourceFileName);
            result = parser.parse(stream);
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
        } finally {
            //TODO ? manager.close();
        }
        Log.i(getClass().getName(), "Loaded " + result.size() + " entries from AssetStorage.");
        return result;
    }
}
