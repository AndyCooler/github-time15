package com.mythosapps.time15.storage;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PostStringRequest extends StringRequest {

    private static Response.Listener RESPONSE_LISTENER = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            if (null != response) {
                try {
                    Log.i("time15", "!! RESPONSE :" + response);
                    CloudBackup.backupSuccess = Boolean.TRUE;
                } catch (Exception e) {
                    //VolleyLog.wtf("onResponse: %s", e.getMessage());
                    CloudBackup.backupSuccess = Boolean.FALSE;
                }
            } else {
            }
        }
    };

    private static Response.ErrorListener ERROR_LISTENER = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("tim15", "!! RESPONSE ERROR :" + error.getMessage());
            //VolleyLog.wtf("onErrorResponse: %s", error.getMessage());
            CloudBackup.backupSuccess = Boolean.FALSE;
        }
    };

    private final byte[] postBytes;

    private final Map<String, String> headersMap = new HashMap<>();

    public PostStringRequest(String url, byte[] postBytes) {
        super(Method.POST, url, RESPONSE_LISTENER, ERROR_LISTENER);
        this.postBytes = postBytes;
    }

    @Override
    public String getBodyContentType() {
        return "application/octet-stream"; //""application/zip";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return postBytes;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headersMap;
    }
}
