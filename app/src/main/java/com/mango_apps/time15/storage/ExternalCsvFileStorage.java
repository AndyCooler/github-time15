package com.mango_apps.time15.storage;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.util.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

/**
 * Spec by example:
 * Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note
 * 08.06.2016,WORKDAY,10:00,14:00,00:45,"1,25",,VACATION,,,,"4,0","endlich Urlaub"
 * 09.06.2016,WORKDAY,10:00,14:00,00:45,"4,75","Übergabe",SICK,,,,"6,0"
 */
public class ExternalCsvFileStorage extends FileStorage implements StorageFacade {

    private String csv_version = "1_0";

    private Activity activity;

    @Override
    public boolean saveDaysDataNew(Activity activity, DaysDataNew data) {

        if (!initialized && !init()) {
            return false;
        }
        this.activity = activity;
        String csvHeadline = getHeadline(csv_version);
        if (csvHeadline == null) {
            return false;
        }
        List<String> csvMonth = loadWholeMonth(getFilename(data.getId()));
        List<String> csvMonthNew = new ArrayList<String>();

        String newCsvLine = toCsvLine(data, csv_version);
        for (String csvLine : csvMonth) {
            if (csvLine.startsWith(data.getId())) {
                csvMonthNew.add(newCsvLine);
            } else {
                csvMonthNew.add(csvLine);
            }
        }

        return saveWholeMonth(getFilename(data.getId()), csvMonth);
    }

    private boolean saveWholeMonth(String filename, List<String> csvMonth) {
        File file = new File(storageDir, filename);

        boolean result = false;
        try {
            FileOutputStream fos = new FileOutputStream(file, true);

            PrintWriter pw = new PrintWriter(fos);
            for (String csvLine : csvMonth) {
                pw.println(csvLine);
            }
            pw.flush();
            pw.close();
            fos.close();

            Log.i(getClass().getName(), "Saved file " + filename);
            result = true;
        } catch (IOException e) {
            fatal("saveWholeMonth", "Error saving file " + filename);
            Log.e(getClass().getName(), "Error saving file " + filename + " as " + file.getAbsolutePath(), e);
        }
        return result;
    }

    private String toCsvLine(DaysDataNew data, String version) {
        return null; // TODO toCsvLine
    }

    public List<String> loadWholeMonth(String filename) {

        return null; // TODO loadWholeMonth
    }

    private String getHeadline(String version) {
        if (csv_version.equals(version)) {
            return "Date,Task,Begin,End,Break,Total,Note,Task,Begin,End,Break,Total,Note";
        }
        fatal("getHeadline", "Version " + version + " unsupported!");
        return null;
    }

    private void fatal(String method, String msg) {

        if (activity != null) {
            Toast.makeText(activity, method + " : " + msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public DaysDataNew loadDaysDataNew(Activity activity, String id) {
        return null;
    }

    @Override
    public int loadBalance(Activity activity, String id) {
        return 0;
    }

    private String getFilename(String id) {
        return TimeUtils.getMonthYearOfID(id) + "__Time15__" + csv_version + ".csv";
    }
}
