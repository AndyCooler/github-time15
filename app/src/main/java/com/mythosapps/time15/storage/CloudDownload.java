package com.mythosapps.time15.storage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.mythosapps.time15.util.ZipUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class CloudDownload extends AsyncTask<String, String, String> {

    private final Activity activity;
    private final View view;
    private ProgressDialog pDialog;
    private ByteArrayOutputStream output;

    public CloudDownload(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgressDialog();
    }

    private void showProgressDialog() {
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... urlStrings) {
        int count;
        output = new ByteArrayOutputStream();
        InputStream input = null;
        try {
            URL url = new URL(urlStrings[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conection.getContentLength();

            // download the file
            input = new BufferedInputStream(url.openStream(), 8192);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            input.close();
        } catch (Exception e) {
            Snackbar.make(view, "Download fehlgeschlagen: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // nicht schlimm aber
                }
            }
        }
        return null;
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        pDialog.setProgress(Integer.parseInt(progress[0]));
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String result) {
        // dismiss the dialog after the file was downloaded
        pDialog.dismiss();

        if (output == null) {
            Snackbar.make(view, "Download fehlgeschlagen: No stream!", Snackbar.LENGTH_LONG).show();
            return;
        }

        byte[] zipBytes = output.toByteArray();
        if (zipBytes == null || zipBytes.length == 0) {
            Snackbar.make(view, "Download fehlgeschlagen: Empty file!", Snackbar.LENGTH_LONG).show();
            return;
        }

        ZipUtils.restoreFromBytes(zipBytes, activity);
        Snackbar.make(view, "Restore success! " + zipBytes.length, Snackbar.LENGTH_LONG).show();
        //TODO ConfigStorage.initFromConfig..
    }
}
