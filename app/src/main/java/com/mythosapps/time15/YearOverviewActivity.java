package com.mythosapps.time15;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mythosapps.time15.storage.StorageFacade;
import com.mythosapps.time15.storage.StorageFactory;
import com.mythosapps.time15.types.ColorsUI;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.types.Time15;
import com.mythosapps.time15.util.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This activity lets the user see the sum of hours spent on tasks each month.
 */
public class YearOverviewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Navigation
    public final static String EXTRA_MESSAGE = "com.mythosapps.time15.MESSAGE";

    // Storage
    private StorageFacade storage;

    // View state and view state management
    private String id;
    private Random random = new Random();
    private KindOfDay selectedTask = KindOfDay.VACATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getName(), "onCreate() started.");
        setContentView(R.layout.activity_year_overview);

        storage = StorageFactory.getDataStorage();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarYear);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Log.i(getClass().getName(), "intent action : " + action);
        Log.i(getClass().getName(), "intent type   : " + type);

        id = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        Log.i(getClass().getName(), "onCreate() finished.");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getClass().getName(), "onResume() started with id " + id);

        initialize();

        Log.i(getClass().getName(), "onResume() finished.");
    }

    private void initialize() {
        Log.i(getClass().getName(), "initialize() started with id " + id);
        setTitle("Übersicht " + TimeUtils.getYearDisplayString(id));

        Spinner yearTaskSpinner = (Spinner) findViewById(R.id.yearTaskSpinner);
        List<CharSequence> taskNames = new ArrayList<>();
        for (KindOfDay task : KindOfDay.list) {
            taskNames.add(task.getDisplayString());
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, taskNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearTaskSpinner.setAdapter(adapter);
        yearTaskSpinner.setOnItemSelectedListener(this);

        updateYearOverViewTable();

        Log.i(getClass().getName(), "initialize() finished.");
    }

    private void updateYearOverViewTable() {
        TableLayout table = (TableLayout) findViewById(R.id.tableViewYear);
        table.removeAllViews();

        table.setColumnStretchable(6, true);
        TableRow row = null;
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

        List<String> listOfIds = Arrays.asList(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"});
        String idFirstJan = "01.01." + TimeUtils.getYearDisplayString(id);
        String idFirstOfMonth = idFirstJan;

        Time15 totalYear = Time15.fromMinutes(0);
        for (final String month : listOfIds) {
            int sumInMinutes = storage.loadTaskSum(this, idFirstOfMonth, selectedTask);
            Time15 time15 = Time15.fromMinutes(sumInMinutes);
            totalYear.plus(time15);
            int numDays = 0;
            while (time15.getHours() >= DaysDataNew.DUE_HOURS_PER_DAY) {
                numDays++;
                time15.minus(DaysDataNew.DUE_TOTAL_MINUTES);
            }

            String display = " : " + numDays + " days, " + time15.toDecimalFormat() + " hours";

            row = new TableRow(this);
            row.setLayoutParams(lp);

            row.addView(createTextView(month));
            row.addView(createTextView(display));
            row.addView(createTextView(""));
            row.addView(createTextView(""));
            row.addView(createTextView(""));
            row.addView(createTextView(""));
            table.addView(row);
            idFirstOfMonth = TimeUtils.monthForwards(idFirstOfMonth);
        }

        // separator line
        View line = new View(this);
        line.setBackgroundColor(ColorsUI.DARK_BLUE_DEFAULT);
        line.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
        table.addView(line);

        // total over year
        int numDays = 0;
        while (totalYear.getHours() >= DaysDataNew.DUE_HOURS_PER_DAY) {
            numDays++;
            totalYear.minus(DaysDataNew.DUE_TOTAL_MINUTES);
        }

        String display = " : " + numDays + " days, " + totalYear.toDecimalFormat() + " hours";

        row = new TableRow(this);
        row.setLayoutParams(lp);

        row.addView(createTextView("Total:"));
        row.addView(createTextView(display));
        row.addView(createTextView(""));
        row.addView(createTextView(""));
        row.addView(createTextView(""));
        row.addView(createTextView(""));
        table.addView(row);
        Log.i(getClass().getName(), "updateYearOverViewTable() to task " + selectedTask);
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
        } else {
            //TextView balanceView = createBalanceView(weeksBalanceMap, weekOfYear, false);
            //previousRow.addView(balanceView);
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
            String balanceText = Time15.fromMinutes(weeksBalance).toDisplayStringWithSign();
            balanceView.setText(balanceText);
        } else {
            balanceView.setText("");
        }
        return balanceView;
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
        getMenuInflater().inflate(R.menu.menu_year_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_day) {
            startMainActivity(this.id);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startMainActivity(String withId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, withId);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i(getClass().getName(), "onItemSelected() started with position " + position);

        Object item = parent.getItemAtPosition(position);
        Log.i(getClass().getName(), "item : " + item == null ? "null" : item.toString());
        selectedTask = item == null ? KindOfDay.VACATION : KindOfDay.fromString(parent.getItemAtPosition(position).toString());
        updateYearOverViewTable();
        Log.i(getClass().getName(), "onItemSelected() finished.");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i(getClass().getName(), "onNothingSelected() started.");
        selectedTask = KindOfDay.VACATION;
        Log.i(getClass().getName(), "onNothingSelected() finished.");
    }
}
