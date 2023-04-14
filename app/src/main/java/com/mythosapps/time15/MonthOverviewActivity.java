package com.mythosapps.time15;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.mythosapps.time15.storage.StorageFacade;
import com.mythosapps.time15.storage.StorageFactory;
import com.mythosapps.time15.types.BeginEndTask;
import com.mythosapps.time15.types.ColorsUI;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.types.Time15;
import com.mythosapps.time15.util.TimeUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This activity lets the user see on how many days they were working in a month, and what kind of
 * day each day was.
 */
public class MonthOverviewActivity extends AppCompatActivity {

    // Navigation
    public final static String EXTRA_MESSAGE = "com.mythosapps.time15.MESSAGE";

    private static ViewGroup.LayoutParams TEXTVIEW_LAYOUT_PARAMS_FLOW = new TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    private static ViewGroup.LayoutParams TEXTVIEW_LAYOUT_PARAMS_MAX = new TableRow.LayoutParams(0, WRAP_CONTENT, 1.0f);

    // Storage
    private StorageFacade storage;

    // View state and view state management
    private String id;

    private boolean showSecondTask = false;
    private Integer billableMinutes;
    private double rate = 100.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_overview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        storage = StorageFactory.getDataStorage();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMonth);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        // importData operation is deactivated in AndroidManifest.xml at the moment
        // by removed intent definitions
        if (Intent.ACTION_VIEW.equals(action)) {
            id = TimeUtils.createID();
            if (type != null) {
                Uri uri = intent.getData();
                importData(uri);
            } else {
                Toast.makeText(MonthOverviewActivity.this.getApplicationContext(), "intent type is null!", Toast.LENGTH_LONG).show();
            }
        } else {
            id = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        }

    }

    private void importData(Uri data) {
        final String scheme = data.getScheme();

        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            try {
                ContentResolver cr = MonthOverviewActivity.this.getContentResolver();
                InputStream is = cr.openInputStream(data);
                if (is == null) {
                    Toast.makeText(MonthOverviewActivity.this.getApplicationContext(), "inputStream is null!", Toast.LENGTH_LONG).show();
                    return;
                }

                // can: statt FOlgendem besser ExternalCsvStorage.loadWholeMonth rufen und
                // parametrisieren mit dem Reader!
                StringBuffer buf = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String str;
                if (is != null) {
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    }
                }
                is.close();

                final String s = buf.toString();

                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage(R.string.dialog_message)
                        .setTitle(R.string.dialog_title);

                // Add the buttons
                builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Toast.makeText(MonthOverviewActivity.this.getApplicationContext(), "yay! " + s, Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(MonthOverviewActivity.this.getApplicationContext(), "nay-nay! " + s, Toast.LENGTH_LONG).show();
                    }
                });


                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();

            } catch (Exception e) {
                Toast.makeText(MonthOverviewActivity.this.getApplicationContext(), "ex: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MonthOverviewActivity.this.getApplicationContext(), "scheme: " + scheme, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("time15", "Instance : " + this.toString());
        initialize();

    }

    private void initialize() {
        setTitle(TimeUtils.getMonthYearDisplayStringShort(id));

        TableLayout table = (TableLayout) findViewById(R.id.tableView);
        table.removeAllViews();
        // use onPostCreate to measure created views and do some more tweaking.. :)

        table.setColumnShrinkable(0, true);
        table.setColumnShrinkable(1, true);
        if (showSecondTask) {
            table.setColumnStretchable(6, true);
        } else {
            table.setColumnStretchable(2, true);
        }

        TableRow row = null;
        TableRow.LayoutParams lp = new TableRow.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1.0f);

        List<String> listOfIds = TimeUtils.getListOfIdsOfMonth(id);

        Set<KindOfDay> tasksThisMonth = new HashSet<>();
        TextView lastSumOfWeekView = null;
        int sumWeek = 0;
        int sumHomeOfficeDays = 0;

        for (final String dayId : listOfIds) {
            DaysDataNew data = storage.loadDaysDataNew(this, dayId);

            if (data == null) {
                if (!TimeUtils.isWeekend(dayId)) {
                    if (TimeUtils.isMonday(dayId)) {
                        updateSumWeek(lastSumOfWeekView, sumWeek);

                        addWeekSeparatorLine(table);
                        sumWeek = 0;
                    }

                    int rowColor = ColorsUI.DARK_BLUE_DEFAULT;
                    row = new TableRow(this);
                    row.setLayoutParams(lp);
                    row.addView(createTextViewInFlow(TimeUtils.dayOfWeek(dayId), rowColor));
                    row.addView(createTextViewInFlow(dayId.substring(0, 2), rowColor));
                    row.addView(createTextViewInFlow("", rowColor));
                    row.addView(createTextViewInFlow("", rowColor));
                    if (showSecondTask) {
                        row.addView(createTextViewInFlow("", rowColor));
                        row.addView(createTextViewInFlow("", rowColor));
                    }
                    lastSumOfWeekView = createBalanceView(sumWeek, dayId);
                    row.addView(lastSumOfWeekView);

                    table.addView(row);
                }
            } else {
                if (data.getHomeOffice()) {
                    sumHomeOfficeDays++;
                }
                if (TimeUtils.isMonday(dayId)) {
                    updateSumWeek(lastSumOfWeekView, sumWeek);

                    sumWeek = 0;
                    addWeekSeparatorLine(table);
                }

                data.collectTaskNames(tasksThisMonth);
                sumWeek += data.getTotalFor(KindOfDay.WORKDAY).toMinutes();

                row = new TableRow(this);
                row.setLayoutParams(lp);

                String hours = "";
                String extraVacationHours = "";
                BeginEndTask task0 = data.getTask(0);
                BeginEndTask task1 = data.getTask(1);
                if (task1 == null) {
                    hours = task0.getTotal().toDecimalForDisplay()+ "  ";// + " h";
                } else {
                    if (task1.getKindOfDay().equals(task0.getKindOfDay())) {
                        Time15 combined = data.getTotalFor(task0.getKindOfDay());
                        hours = combined.toDecimalForDisplay() + " " + getString(R.string.display_sum);
                    } else {
                        hours = task0.getTotal().toDecimalForDisplay() + " *";
                    }
                }

                row.addView(createTextViewInFlow(TimeUtils.dayOfWeek(dayId), data.getHomeOffice() ? ColorsUI.DARK_GREEN_SAVE_SUCCESS : ColorsUI.DARK_BLUE_DEFAULT));
                row.addView(createTextViewInFlow(dayId.substring(0, 2), ColorsUI.DARK_BLUE_DEFAULT));
                int itemColor = calcItemColor(task0.getKindOfDay(), task0.isComplete());
                String kindOf = task0.getKindOfDay().getDisplayString();
                boolean isNoteEmpty = task0.getNote() == null || "".equals(task0.getNote());
                row.addView(createTextViewMaxWidth(KindOfDay.DEFAULT_WORK.equals(kindOf) ? (isNoteEmpty ? kindOf : task0.getNote()) : kindOf, itemColor));
                row.addView(createTextViewInFlow(hours, itemColor));

                if (showSecondTask) {
                    if (task1 != null) {
                        extraVacationHours = task1.getTotal().toDecimalForDisplay();// + " h";
                    }
                    itemColor = calcItemColor(task1 == null ? task0.getKindOfDay() : task1.getKindOfDay(), task1 == null ? task0.isComplete() : task1.isComplete());
                    // long version of second task display:
                    //row.addView(createTextView(task1 == null ? "" : trimmed(task1.getKindOfDay().getDisplayString()), itemColor));
                    // compact version of second task display:
                    row.addView(createTextViewInFlow(task1 == null ? "" : "(2)", itemColor));
                    row.addView(createTextViewInFlow(extraVacationHours, itemColor));
                }
                lastSumOfWeekView = createBalanceView(sumWeek, dayId);
                row.addView(lastSumOfWeekView);

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startMainActivity(dayId);
                    }
                });
                table.addView(row);
            }
        }

        View line = new View(this);
        line.setBackgroundColor(ColorsUI.PURPLE);
        line.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 4));
        table.addView(line);

        // Sum for this month's tasks
        for (KindOfDay task : KindOfDay.list) {
            // row
            if (tasksThisMonth.contains(task)) {
                int sumInMinutes = storage.loadTaskSum(this, id, task);
                Time15 time15 = Time15.fromMinutes(sumInMinutes);
                if (KindOfDay.WORKDAY.equals(task)) {
                    billableMinutes = sumInMinutes;
                }

                int rowColor = ColorsUI.DARK_BLUE_DEFAULT;
                row = new TableRow(this);
                row.setLayoutParams(lp);
                row.addView(createTextViewInFlow("", rowColor));
                row.addView(createTextViewInFlow(getString(R.string.display_sum), rowColor));
                row.addView(createTextViewInFlow(task.getDisplayString(), rowColor));
                if (showSecondTask) {
                    row.addView(createTextViewInFlow("", rowColor));
                    row.addView(createTextViewInFlow("", rowColor));
                }
                row.addView(createTextViewInFlow(time15.toDecimalForDisplay(), rowColor));
                row.addView(createTextViewInFlow(" h", rowColor));
                table.addView(row);
            }
        }
        int rowColor = ColorsUI.DARK_GREEN_SAVE_SUCCESS;
        row = new TableRow(this);
        row.setLayoutParams(lp);
        row.addView(createTextViewInFlow("", rowColor));
        row.addView(createTextViewInFlow(getString(R.string.display_sum), rowColor));
        row.addView(createTextViewInFlow("Home Office Tage", rowColor));
        row.addView(createTextViewInFlow("" + sumHomeOfficeDays, rowColor));
        row.addView(createTextViewInFlow("", rowColor));
        table.addView(row);

    }

    private void updateSumWeek(TextView view, int sumWeek) {
        if (view != null) {
            String weekText = Time15.fromMinutes(sumWeek).toDecimalForDisplay();
            view.setTextColor(ColorsUI.PURPLE);
            view.setText(weekText);
        }
    }

    private int calcItemColor(KindOfDay kindOfDay, boolean isComplete) {

        return isComplete ? kindOfDay.getColor() : ColorsUI.RED_FLAGGED;
    }

    private void addWeekSeparatorLine(TableLayout table) {
        View line = new View(this);
        line.setBackgroundColor(ColorsUI.DARK_BLUE_DEFAULT);
        line.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
        table.addView(line);
    }

    private TextView createBalanceView(int sumWeek, String dayId) {

        String balanceText = "";
        TextView balanceView = createTextViewRight(balanceText, ColorsUI.PURPLE);

        if (TimeUtils.isLastWorkDayOfMonth(dayId)) {
            updateSumWeek(balanceView, sumWeek);
        } else if (TimeUtils.isMonday(dayId)) {
            balanceView.setTextColor(ColorsUI.LIGHT_GREY);
            balanceView.setText(TimeUtils.getCalendarWeekDisplayString(dayId));
        }
        return balanceView;
    }

    private TextView createTextView0(String text, int color) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setPadding(5, 3, 5, 2);
        return textView;
    }

    private TextView createTextViewInFlow(String text, int color) {
        TextView textView = createTextView0(text, color);
        // siehe https://developer.android.com/guide/topics/ui/layout/linear#prioritize-weight
        textView.setLayoutParams(TEXTVIEW_LAYOUT_PARAMS_FLOW);
        textView.setGravity(Gravity.LEFT);
        return textView;
    }

    private TextView createTextViewRight(String text, int color) {
        TextView textView = createTextView0(text, color);
        textView.setLayoutParams(TEXTVIEW_LAYOUT_PARAMS_FLOW);
        textView.setGravity(Gravity.RIGHT);
        return textView;
    }

    private TextView createTextViewMaxWidth(String text, int color) {
        TextView textView = createTextView0(text, color);
        textView.setLayoutParams(TEXTVIEW_LAYOUT_PARAMS_MAX);
        textView.setGravity(Gravity.LEFT);
        return textView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_month_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_day) {
            startMainActivity(this.id);
            return true;
        }
        if (id == R.id.action_month_backwards) {
            this.id = TimeUtils.monthBackwards(this.id);
            initialize();
            return true;
        }
        if (id == R.id.action_month_forwards) {
            this.id = TimeUtils.monthForwards(this.id);
            initialize();
            return true;
        }
/*        if (id == R.id.action_migrate) {
            migrateDataForMonth();
            return true;
        }
*/
        if (id == R.id.action_year) {
            startYearOverviewActivity();
            return true;
        }

        if (id == R.id.action_month_vacation) {
            menuEditMultiDayVacation();
            return true;
        }

        if (id == R.id.action_bill) {
            billButtonClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void menuEditMultiDayVacation() {
        final MultiDayTaskPopupUI taskUI = new MultiDayTaskPopupUI(this);

        taskUI.setOkButton(getString(R.string.edit_task_new), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String von = taskUI.getInputBeginn().getText().toString();
                String bis = taskUI.getInputEnde().getText().toString();

                MonthOverviewActivity.this.saveMultiDayVacation(von, bis);
            }
        });
        taskUI.setCancelButton(getString(R.string.edit_task_cancel));
        taskUI.show();

    }

    private void saveMultiDayVacation(String von, String bis) {

        try {
            String id = von;
            int count = 0;
            ArrayList<DaysDataNew> newData = new ArrayList<>();
            while (count < 16) {
                if (!TimeUtils.isWeekend(id)) {
                    if (storage.loadDaysDataNew(this, id) != null) {
                        Snackbar.make(findViewById(R.id.tableView), "Bereits eingetragen: " + id, Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    DaysDataNew data = new DaysDataNew(id);
                    BeginEndTask task = new BeginEndTask();
                    task.setKindOfDay(KindOfDay.VACATION);
                    task.setTotal(new Time15(KindOfDay.DEFAULT_DUE_TIME_PER_DAY_IN_MINUTES));
                    data.addTask(task);
                    newData.add(data);
                    count++;
                }
                if (id.equals(bis)) {
                    break;
                }
                id = TimeUtils.dateForwards(id);
            }
            for (DaysDataNew data : newData) {
                storage.saveDaysDataNew(this, data);
            }
            initialize();
            Snackbar.make(findViewById(R.id.tableView), "Urlaub eingetragen: " + count + " Tage", Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.tableView), "Hat leider nicht geklappt.", Snackbar.LENGTH_LONG).show();
        }
    }

    public void startMainActivity(View view) {
        startMainActivity(this.id);
    }

    public void startMainActivity(String withId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, withId);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void startYearOverviewActivity() {
        Intent intent = new Intent(this, YearOverviewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void startYearOverviewActivity(View view) {
        startYearOverviewActivity();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    /**
     * Migrates data for all days in month to a new file format,
     * simply by loading and saving each day's data.
     * Works because the load method is always able to load data
     * in the old format and save always saves data in new format.
     */
    private void migrateDataForMonth() {

        // TODO remove: test drive crash reporting feature
        throw new IllegalStateException("Test drive Crashalytics Time15");
/*
        List<String> listOfIds = TimeUtils.getListOfIdsOfMonth(id);

        String success = "Success! ";
        ArrayList<String> good = new ArrayList<String>();
        ArrayList<String> bad = new ArrayList<String>();
        for (final String dayId : listOfIds) {
            DaysDataNew data = storage.loadDaysDataNew(this, dayId);

            if (data != null) {
                if (storage.saveDaysDataNew(this, data)) {
                    good.add(dayId.substring(0, 2));
                } else {
                    bad.add(dayId.substring(0, 2));
                    success = "Fail! ";
                }
            }
        }
        String goodBad = "\n\nMonth: " + TimeUtils.getMonthYearDisplayString(id)
                + "\n\nGood: " + TextUtils.join(",", good) + "\nBad: " + TextUtils.join(",", bad);
        Toast.makeText(MonthOverviewActivity.this, success + goodBad, Toast.LENGTH_LONG).show();
*/
    }

    public void billButtonClicked() {
        final BillPopupUI taskUI = new BillPopupUI(this, billableMinutes, rate);

        taskUI.setOkButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rate = taskUI.getRate();
            }
        });
        taskUI.show();
    }

}
