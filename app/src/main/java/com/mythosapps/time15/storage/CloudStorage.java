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
import com.mythosapps.time15.types.BalanceType;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;

public class CloudStorage implements StorageFacade {

    private static final String REQUEST_TAG = "time15.request";

    private RequestQueue requestQueue;

    private Activity activity;

    public void disconnect() {
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUEST_TAG);
        }
    }

    public void checkCloudBackupAccessible(Activity activity, View view) {
        this.activity = activity;

        // Instantiate the RequestQueue.
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(activity);
        }
        String url = "https://mythosapps.com/time15app/hello/world";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (null != response) {
                            try {
                                Snackbar.make(view, "Cloud Backup: " + response, Snackbar.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Snackbar.make(view, "Cloud Backup: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(view, "Cloud Backup: none.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(view, "Cloud Backup: VE: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
        request.addMarker(REQUEST_TAG);
        requestQueue.add(request);
    }

    @Override
    public boolean saveDaysDataNew(Activity activity, DaysDataNew data) {
        return false;
    }

    @Override
    public DaysDataNew loadDaysDataNew(Activity activity, String id) {
        return null;
    }

    @Override
    public int loadBalance(Activity activity, String id, BalanceType balanceType) {
        return 0;
    }

    @Override
    public int loadTaskSum(Activity activity, String id, KindOfDay task) {
        return 0;
    }
}
