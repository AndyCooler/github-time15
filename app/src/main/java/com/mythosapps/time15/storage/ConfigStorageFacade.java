package com.mythosapps.time15.storage;

import android.app.Activity;

import com.mythosapps.time15.types.KindOfDay;

import java.util.List;

/**
 * Created by andreas on 10.02.17.
 */

public interface ConfigStorageFacade {

    List<KindOfDay> loadConfigXml(Activity activity);
}
