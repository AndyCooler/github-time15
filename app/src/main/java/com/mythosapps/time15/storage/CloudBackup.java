package com.mythosapps.time15.storage;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

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

import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Cloud backup is a class that offers a backup on a remote server, i.e. in the cloud.
 * It is different from StorageFacade in that it doesn't save every small input the user makes
 * in a store. It is exactly the same functionality as email backup. What means backup? Backup means
 * all data is exported to outside the app, so that the user knows all their data is backed up
 * and they don't need to worry about losing it.
 * Cloud backup must be activated first in settings using settings_cloud_backup. Then a
 * settings_cloud_backup_id is generated automatically when the app launches again.
 * This can take place on demand (as with email backup) or automatically based on an interval.
 * This can be configured in in the settings dialog using settings_cloud_backup_frequency.
 * Using scoped-storage instead of file system storage. Together with cloud
 * backup, this makes for a good backup mechanism as data remains local (and within app scope),
 * but pushed to the cloud frequently.
 */
public class CloudBackup {

    private static final String REQUEST_TAG = "time15.request";

    private static final String BASE_URL = "https://mythosapps.com/time15app";

    private static final String CLOUD_FILE_URL = "https://mythosapps.com/time15app/cloud/";

    private static final String OP_CLOUD_AVAILABLE = "/hello/";

    private static final String OP_CLOUD_STORE = "/store";

    private static final long TIMER_DELAY = 500;

    /**
     * From settings.
     */
    private final Boolean activated;
    /**
     * From settings.
     */
    private final Integer backupFrequency;

    private RequestQueue requestQueue;

    private Activity activity;

    private Timer timer = new java.util.Timer();

    // null (never requested or request pending), true (available), false (unavailable)
    private static Boolean available;

    // null (never requested or request pending or unavailable), date of last backup (available)
    private static LocalDate lastBackupDate;

    // null (never requested, request pending or failed to send request), true (response reported success), false (response reported failure)
    public static Boolean backupSuccess;

    public CloudBackup(Boolean activated, Integer backupFrequency) {
        this.activated = activated;
        this.backupFrequency = backupFrequency;
    }

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
    public void requestAvailability(Activity activity, View view, String cloudId) {
        this.activity = activity;

        String url = BASE_URL + OP_CLOUD_AVAILABLE + cloudId;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (null != response) {
                            try {
                                if (view != null) {
                                    Snackbar.make(view, "Cloud Backup: " + response, Snackbar.LENGTH_LONG).show();
                                }
                                if (response.startsWith("Stand: ")) {
                                    available = true;
                                    LocalDate now = LocalDate.now();
                                    lastBackupDate = LocalDate.parse(response.substring(7, 17));
                                    if (backupFrequency != null && backupFrequency > 0 && lastBackupDate.plusDays(backupFrequency).isBefore(now)) {
                                        timer.schedule(new CloudBackup.RequestBackupAsyncTask(cloudId), TIMER_DELAY);
                                    }
                                } else if (response.startsWith("Backup noch nicht erstellt")) {
                                    available = true;
                                    lastBackupDate = LocalDate.now();
                                    if (backupFrequency != null && backupFrequency > 0) {
                                        timer.schedule(new CloudBackup.RequestBackupAsyncTask(cloudId), TIMER_DELAY);
                                    }
                                } else {
                                    available = false;
                                    lastBackupDate = null;
                                }
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
    public boolean requestBackup(View view, String cloudBackupId) {
        if (!available) {
            Snackbar.make(view, "Cloud Upload Error: Cloud not available", Snackbar.LENGTH_LONG).show();
            return false;
        }
        String backupMoment = TimeUtils.createMoment();
        String zipArchiveFilename = "Time15_Backup_" + backupMoment + ".zip";
        byte[] zipBytes = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                zipBytes = ZipUtils.backupToBytes(backupMoment, activity);
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
            request.getHeaders().put("cloudBackupId", cloudBackupId);
        } catch (AuthFailureError authFailureError) {
            Snackbar.make(view, "Cloud Upload Error: Cannot add backupMoment", Snackbar.LENGTH_LONG).show();
            authFailureError.printStackTrace();
            return false;
        }
        request.addMarker(REQUEST_TAG);
        getRequestQueue().add(request);
        return true;
    }

    /**
     * Restore zip file from Cloud. POST: (user'S app ID) -> (zip content)
     */
    public boolean requestRestore(FragmentActivity activity, View view, String cloudBackupId) {
        this.activity = activity;
        if (!available) {
            Snackbar.make(view, "Cloud Upload Error: Cloud not available", Snackbar.LENGTH_LONG).show();
            return false;
        }
        final String url = BASE_URL + OP_CLOUD_AVAILABLE + cloudBackupId;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (null != response) {
                            try {
                                if (view != null) {
                                    // Zeige den Timestamp des letzten Cloud Backup an und frage ob dieser Stand wiederherzustellen ist
                                    //Snackbar.make(view, "Cloud Restore: " + response, Snackbar.LENGTH_LONG).show();
                                    String url = response;
                                    //String filename = url.substring(url.lastIndexOf("/"));

                                    Snackbar snackbar = Snackbar.make(view, "Folgenden Stand aus Cloud wiederherstellen? " + url,
                                            Snackbar.LENGTH_LONG).setAction("OK", v -> {
                                        try {
                                            String retrievingFrom = CLOUD_FILE_URL + cloudBackupId + "/latest.zip";
                                            new CloudDownload(activity, view).execute(retrievingFrom);

                                        } catch (Exception de) {
                                            Snackbar.make(view, "Fehlgeschlagen: " + de.getMessage(), Snackbar.LENGTH_LONG).show();
                                            de.printStackTrace();
                                        }
                                    });
                                    snackbar.show();
                                }
                                available = true;
                            } catch (Exception e) {
                                if (view != null) {
                                    Snackbar.make(view, "Cloud Restore: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                                available = false;
                            }
                        } else {
                            if (view != null) {
                                Snackbar.make(view, "Cloud Restore: none.", Snackbar.LENGTH_LONG).show();
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

        return true;
    }

    private RequestQueue getRequestQueue() {
        // Instantiate the RequestQueue.
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(activity);
        }
        return requestQueue;
    }

    class RequestBackupAsyncTask extends TimerTask {

        private String requestCloudId;

        public RequestBackupAsyncTask(String requestCloudId) {
            this.requestCloudId = requestCloudId;
        }

        @Override
        public void run() {
            CloudBackup.this.requestBackup(null, requestCloudId);
        }
    }
}
