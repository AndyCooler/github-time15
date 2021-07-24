package com.mythosapps.time15.storage;

import android.app.Activity;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

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

    private RequestQueue requestQueue;

    private Activity activity;

    private ConfigStorageFacade configStorage;

    // null (never requested or request pending), true (available), false (unavailable)
    private static Boolean available;

    public Boolean isAvailable() {
        return available;
    }

    public void disconnect() {
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUEST_TAG);
        }
        available = false;
    }

    public void requestAvailability(Activity activity, View view) {
        this.activity = activity;

        String url = BASE_URL + "/hello/world";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (null != response) {
                            try {
                                Snackbar.make(view, "Cloud Backup: " + response, Snackbar.LENGTH_LONG).show();
                                available = true;
                            } catch (Exception e) {
                                Snackbar.make(view, "Cloud Backup: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                available = false;
                            }
                        } else {
                            Snackbar.make(view, "Cloud Backup: none.", Snackbar.LENGTH_LONG).show();
                            available = false;
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(view, "Cloud Backup: VE: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                available = false;
            }
        });
        request.addMarker(REQUEST_TAG);
        getRequestQueue().add(request);
    }

    private RequestQueue getRequestQueue() {
        // Instantiate the RequestQueue.
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(activity);
        }
        return requestQueue;
    }
}
