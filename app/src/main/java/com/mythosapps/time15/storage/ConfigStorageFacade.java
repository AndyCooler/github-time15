package com.mythosapps.time15.storage;

import android.app.Activity;

import com.mythosapps.time15.types.KindOfDay;

import java.util.List;

/**
 * Created by andreas on 10.02.17.
 */

public interface ConfigStorageFacade {

    String XML_PROLOG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<config>\n";
    String XML_END = "</config>\n";

    List<KindOfDay> loadConfigXml(Activity activity);

    boolean saveExternalConfigXml(Activity activity, List<KindOfDay> tasks);
}
