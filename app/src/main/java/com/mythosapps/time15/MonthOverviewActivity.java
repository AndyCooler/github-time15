package com.mythosapps.time15;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This activity lets the user see on how many days they were working in a month, and what kind of
 * day each day was.
 */
public class MonthOverviewActivity extends AppCompatActivity {

    // Navigation
    public final static String EXTRA_MESSAGE = "com.mythosapps.time15.MESSAGE";

    // Storage
    private StorageFacade storage;

    // View state and view state management
    private String id;
    private Random random = new Random();

    private boolean showSecondTask = true;

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

        initialize();

    }

    private void initialize() {
        setTitle(TimeUtils.getMonthYearDisplayStringShort(id));

        TableLayout table = (TableLayout) findViewById(R.id.tableView);
        table.removeAllViews();
        //table.setColumnShrinkable(7, true);
        //table.setColumnStretchable(7, true);
        if (showSecondTask) {
            table.setColumnStretchable(6, true);
        } else {
            table.setColumnStretchable(4, true);
        }
        TableRow row = null;
        TableRow previousRow = null;
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

        List<String> listOfIds = TimeUtils.getListOfIdsOfMonth(id);
        int previousWeekOfYear = -1;

        Set<KindOfDay> tasksThisMonth = new HashSet<>();

        Map<Integer, Integer> weeksBalanceMap = new HashMap<Integer, Integer>();
        for (final String dayId : listOfIds) {
            DaysDataNew data = storage.loadDaysDataNew(this, dayId);

            if (data == null) {
                if (!TimeUtils.isWeekend(dayId)) {
                    int rowColor = ColorsUI.DARK_BLUE_DEFAULT;
                    previousRow = row;
                    row = new TableRow(this);
                    row.setLayoutParams(lp);
                    row.addView(createTextView(TimeUtils.dayOfWeek(dayId), rowColor));
                    row.addView(createTextView(dayId.substring(0, 2), rowColor));
                    row.addView(createTextView("", rowColor));
                    row.addView(createTextView("", rowColor));
                    if (showSecondTask) {
                        row.addView(createTextView("", rowColor));
                        row.addView(createTextView("", rowColor));
                    }
                    previousWeekOfYear = addWeekSeparatorLine(dayId, weeksBalanceMap, table, previousWeekOfYear, row, previousRow);
                    table.addView(row);
                }
            } else {
                data.collectTaskNames(tasksThisMonth);

                previousRow = row;
                row = new TableRow(this);
                row.setLayoutParams(lp);
                addToBalance(data, weeksBalanceMap);

                String hours = "";
                String extraVacationHours = "";
                BeginEndTask task0 = data.getTask(0);
                BeginEndTask task1 = data.getTask(1);
                //if (KindOfDay.isBeginEndType(task0.getKindOfDay())) {
                hours += task0.getTotal().toDecimalForDisplay();// + " h";

                row.addView(createTextView(TimeUtils.dayOfWeek(dayId)));
                row.addView(createTextView(dayId.substring(0, 2)));
                int itemColor = calcItemColor(task0.getKindOfDay(), task0.isComplete());
                String kindOf = task0.getKindOfDay().getDisplayString();
                row.addView(createTextView(trimmed(KindOfDay.DEFAULT_WORK.equals(kindOf) ? (task0.getNote() == null ? kindOf : task0.getNote()) : kindOf), itemColor));
                row.addView(createTextView(hours, itemColor));

                if (showSecondTask) {
                    if (task1 != null) {
                        extraVacationHours = task1.getTotal().toDecimalForDisplay();// + " h";
                    }
                    itemColor = calcItemColor(task1 == null ? task0.getKindOfDay() : task1.getKindOfDay(), task1 == null ? task0.isComplete() : task1.isComplete());
                    row.addView(createTextView(task1 == null ? "" : trimmed(task1.getKindOfDay().getDisplayString()), itemColor));
                    row.addView(createTextView(extraVacationHours, itemColor));
                }

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startMainActivity(dayId);
                    }
                });
                previousWeekOfYear = addWeekSeparatorLine(dayId, weeksBalanceMap, table, previousWeekOfYear, row, previousRow);
                table.addView(row);
            }
        }

        View line = new View(this);
        line.setBackgroundColor(ColorsUI.DARK_GREY_SAVE_ERROR);
        line.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 4));
        table.addView(line);

        // Sum for this month's tasks
        for (KindOfDay task : KindOfDay.list) {
            // row
            if (tasksThisMonth.contains(task)) {
                int sumInMinutes = storage.loadTaskSum(this, id, task);
                Time15 time15 = Time15.fromMinutes(sumInMinutes);

                int rowColor = ColorsUI.DARK_BLUE_DEFAULT;
                previousRow = row;
                row = new TableRow(this);
                row.setLayoutParams(lp);
                row.addView(createTextView("", rowColor));
                row.addView(createTextView("", rowColor));
                row.addView(createTextView(trimmed(task.getDisplayString()), rowColor));
                row.addView(createTextView(time15.toDecimalForDisplay(), rowColor));
                if (showSecondTask) {
                    row.addView(createTextView(getString(R.string.month_sum), rowColor));
                    row.addView(createTextView("", rowColor));
                }
                table.addView(row);
            }
        }

    }

    private String trimmed(String displayString) {
        if (showSecondTask) {
            return displayString.length() > 10 ? displayString.substring(0, 10) : displayString;
        } else {
            return displayString.length() > 60 ? displayString.substring(0, 60) : displayString;
        }

    }

    private int calcItemColor(KindOfDay kindOfDay, boolean isComplete) {

        return isComplete ? kindOfDay.getColor() : ColorsUI.RED_FLAGGED;
    }

    private int addWeekSeparatorLine(String dayId, Map<Integer, Integer> weeksBalanceMap, TableLayout table, int previousWeekOfYear, ViewGroup row, ViewGroup previousRow) {
        int weekOfYear = TimeUtils.getWeekOfYear(dayId);
        int newPreviousWeekOfYear = previousWeekOfYear;
        if (weekOfYear != previousWeekOfYear) {
            if (previousWeekOfYear != -1) {
                TextView balanceView = createBalanceView(weeksBalanceMap, previousWeekOfYear, true);
                previousRow.addView(balanceView);
                View line = new View(this);
                line.setBackgroundColor(ColorsUI.DARK_BLUE_DEFAULT);
                line.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                table.addView(line);
            }
            newPreviousWeekOfYear = weekOfYear;
        } else if (TimeUtils.isLastWorkDayOfMonth(dayId)) { // if id is last in month
            // add text view with balance to current row
            TextView balanceView = createBalanceView(weeksBalanceMap, weekOfYear, true);
            row.addView(balanceView);
        }
        return newPreviousWeekOfYear;
    }

    private TextView createBalanceView(Map<Integer, Integer> weeksBalanceMap, int weekOfYear, boolean show) {
        TextView balanceView = new TextView(this);
        //balanceView.setWidth(0);
        balanceView.setGravity(Gravity.RIGHT);
        //balanceView.setBackgroundColor(ColorsUI.SELECTION_BG);
        balanceView.setPadding(5, 5, 10, 5);
        balanceView.setTextColor(ColorsUI.DARK_GREY_SAVE_ERROR);

        //view.setText(String.convert(TimeUtils.getWeekOfYear(dayId)));
        if (show) {
            int weeksBalance = weeksBalanceMap.get(weekOfYear) == null ? 0 : weeksBalanceMap.get(weekOfYear);
            String balanceText = Time15.fromMinutes(weeksBalance).toDecimalForDisplay();
            balanceView.setText(balanceText);
        } else {
            balanceView.setText("");
        }
        return balanceView;
    }

    private void addToBalance(DaysDataNew data, Map<Integer, Integer> weeksBalanceMap) {
        int weekOfYear = TimeUtils.getWeekOfYear(data.getId());
        int balanceInMinutes = data.getTotalFor(KindOfDay.WORKDAY).toMinutes();
        Integer current = weeksBalanceMap.get(weekOfYear);
        if (current == null) {
            weeksBalanceMap.put(weekOfYear, balanceInMinutes);
        } else {
            weeksBalanceMap.put(weekOfYear, current + balanceInMinutes);
        }
    }

    private TextView createTextView(String text, int color) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setBackgroundColor(random.nextInt(4));
        textView.setGravity(Gravity.LEFT);
        textView.setPadding(5, 5, 5, 5); // 15, 5, 15, 5
        textView.setPadding(10, 5, 5, 5);
        return textView;
    }

    private TextView createTextView(String text) {
        return createTextView(text, ColorsUI.DARK_BLUE_DEFAULT);
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
        if (id == R.id.action_send) {
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS) + File.separator + STORAGE_DIR);
            String subject = TimeUtils.getMonthYearDisplayString(this.id);
            EmailUtils.sendEmail(this, ExternalCsvFileStorage.getFilename(this.id), storageDir, subject);
            return true;
        }*/
        if (id == R.id.action_year) {
            startYearOverviewActivity();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void startMainActivity(String withId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, withId);
        startActivity(intent);
    }

    public void startYearOverviewActivity() {
        Intent intent = new Intent(this, YearOverviewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        startActivity(intent);
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

}
