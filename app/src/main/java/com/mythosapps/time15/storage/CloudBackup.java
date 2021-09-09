package com.mythosapps.time15.storage;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.mythosapps.time15.R;
import com.mythosapps.time15.util.TimeUtils;
import com.mythosapps.time15.util.ZipUtils;

/**
 * Cloud backup is a class that offers a backup on a remote server, i.e. in the cloud.
 * It is different from StorageFacade in that it doesn't save every small input the user makes
 * in a store. It is exactly the same functionality as email backup. What means backup? Backup means
 * all data is exported to outside the app, so that the user knows all their data is backed up
 * and they don't need to worry about losing it.
 * This can take place on demand (as with email backup) or automatically based on an interval.
 * TODO I plan automatic because it doesnt' disrupt the user experience, in the same way that email
 * backup does. This can be configured in in the settings dialog.
 * TODO See StorageFacade: Use scoped-storage instead of file system storage. Together with cloud
 * backup, this makes for a good backup mechanism as data remains local (and within app scope),
 * but pushed to the cloud frequently.
 */
public class CloudBackup {

    private static final String REQUEST_TAG = "time15.request";

    private static final String BASE_URL = "https://mythosapps.com/time15app";

    private static final String OP_CLOUD_AVAILABLE = "/hello/world";

    private static final String OP_CLOUD_STORE = "/store";

    private RequestQueue requestQueue;

    private Activity activity;

    // null (never requested or request pending), true (available), false (unavailable)
    private static Boolean available;

    // null (never requested, request pending or failed to send request), true (response reported success), false (response reported failure)
    public static Boolean backupSuccess;

    public Boolean isAvailable() {
        return available;
    }

    public Boolean isBackupSuccess() {
        return backupSuccess;
    }

    public void disconnect() {
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUEST_TAG);
        }
        available = false;
    }

    /**
     * Check if Cloud is available at all.
     */
    // TODO change so that #requestAvailability first checks if cloud is available at all, and second checks when the last successful backup took place, and return whatever info is available,
    //  because all that info is shown in a Snackbar anyways. This way, the user can always check the state of their cloud storage.
    public void requestAvailability(Activity activity, View view) {
        this.activity = activity;

        String url = BASE_URL + OP_CLOUD_AVAILABLE;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (null != response) {
                            try {
                                if (view != null) {
                                    Snackbar.make(view, "Cloud Backup: " + response, Snackbar.LENGTH_LONG).show();
                                }
                                available = true;
                            } catch (Exception e) {
                                if (view != null) {
                                    Snackbar.make(view, "Cloud Backup: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                                available = false;
                            }
                        } else {
                            if (view != null) {
                                Snackbar.make(view, "Cloud Backup: none.", Snackbar.LENGTH_LONG).show();
                            }
                            available = false;
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (view != null) {
                    Snackbar.make(view, "Cloud Backup: VE: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                available = false;
            }
        });
        request.addMarker(REQUEST_TAG);
        getRequestQueue().add(request);
    }

    /**
     * Store zip file in Cloud. Touch a file that shows date "last-updated". Save dateTime and result in memberVar. POST: (dateTime, user'S app ID, filename of zip, zip content) -> (true/false)
     */
    public boolean requestBackup(View view) {
        if (!available) {
            Snackbar.make(view, "Cloud Upload Error: Cloud not available", Snackbar.LENGTH_LONG).show();
            return false;
        }
        String backupMoment = TimeUtils.createMoment();
        String zipArchiveFilename = "Time15_Backup_" + backupMoment + ".zip";
        byte[] zipBytes = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                zipBytes = ZipUtils.backupToBytes(backupMoment);
            } else {
                Snackbar.make(view, "Cloud Upload Error: " + R.string.cloud_backup_requires_o, Snackbar.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e) {
            if (view != null) {
                Snackbar.make(view, "Cloud Upload Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
            e.printStackTrace();
            return false;
        }

        String url = BASE_URL + OP_CLOUD_STORE;
        PostStringRequest request = new PostStringRequest(url, zipBytes);
        try {
            request.getHeaders().put("backupMoment", backupMoment);
            request.getHeaders().put("zipArchiveFilename", zipArchiveFilename);
            // TODO send cloud ID as request header parameter so cloud can store into a directory for the ID
        } catch (AuthFailureError authFailureError) {
            Snackbar.make(view, "Cloud Upload Error: Cannot add backupMoment", Snackbar.LENGTH_LONG).show();
            authFailureError.printStackTrace();
            return false;
        }
        request.addMarker(REQUEST_TAG);
        getRequestQueue().add(request);
        return true;
    }

    private RequestQueue getRequestQueue() {
        // Instantiate the RequestQueue.
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(activity);
        }
        return requestQueue;
    }
}
