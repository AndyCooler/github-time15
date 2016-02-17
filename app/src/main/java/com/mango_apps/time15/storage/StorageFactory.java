package com.mango_apps.time15.storage;

/**
 * Created by andreas on 14.02.16.
 */
public class StorageFactory {

    private static StorageFacade INSTANCE = null;

    public static StorageFacade getStorage() {
        if (INSTANCE == null) {
            INSTANCE = createStorage();
        }
        return INSTANCE;
    }

    private static StorageFacade createStorage() {
        return new ExternalFileStorage();
        //return new NoopStorage();
    }
}
