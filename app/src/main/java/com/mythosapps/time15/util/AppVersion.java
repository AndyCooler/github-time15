package com.mythosapps.time15.util;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.UUID;

/**
 * Created by andreas on 16.01.17.
 */
public class AppVersion {


    public static String getVersionName(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        String version = "unknown";
        try {
            PackageInfo pi = pm.getPackageInfo("com.mythosapps.time15", PackageManager.GET_GIDS);
            if (pi != null) {
                version = pi.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return version;
        }
        return version;
    }

    public static String getVersionCode(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        Integer version = null;
        try {
            PackageInfo pi = pm.getPackageInfo("com.mythosapps.time15", PackageManager.GET_GIDS);
            if (pi != null) {
                version = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return "unknown";
        }
        return String.valueOf(version);
    }

    public static String generateUniqueId() {
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }

}
