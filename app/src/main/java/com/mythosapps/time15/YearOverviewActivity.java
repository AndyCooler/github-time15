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
import com.mythosapps.time15.types.BalanceType;
import com.mythosapps.time15.types.ColorsUI;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.types.Time15;
import com.mythosapps.time15.util.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.mythosapps.time15.MainActivity.BALANCE_TYPE;

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

    private static ViewGroup.LayoutParams TEXTVIEW_LAYOUT_PARAMS_FLOW = new TableRow.LayoutParams(WRAP_CONTENT, MATCH_PARENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_overview);

        storage = StorageFactory.getDataStorage();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarYear);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        id = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

    }


    @Override
    protected void onResume() {
        super.onResume();

        initialize();
    }

    private void initialize() {
        setTitle(getString(R.string.year_overview_title) + " " + TimeUtils.getYearDisplayString(id));

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

    }

    private void updateYearOverViewTable() {
        TableLayout table = (TableLayout) findViewById(R.id.tableViewYear);
        table.removeAllViews();

        /*
        In general, android:gravity="right" is different from android:layout_gravity="right".
        The first one affects the position of the text itself within the View,
          so if you want it to be right-aligned, then layout_width= should be either "fill_parent" or "match_parent".
        The second one affects the View's position inside its parent,
          in other words - aligning the object itself (edit box or text view) inside the parent view.
         */
        table.setColumnShrinkable(0, true);
        table.setColumnShrinkable(1, true);
        table.setColumnShrinkable(2, true);
        table.setColumnShrinkable(3, true);
        table.setColumnShrinkable(4, true);
        table.setColumnShrinkable(5, true);
        table.setColumnShrinkable(6, true);
        //table.setColumnShrinkable(4, true);

        //table.setColumnStretchable(2, true);
        TableRow row = null;
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

        List<String> listOfIds = Arrays.asList("Jan", "Feb", "Mär", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez");
        String idFirstJan = "01.01." + TimeUtils.getYearDisplayString(id);
        String idFirstOfMonth = idFirstJan;

        Time15 totalYear = Time15.fromMinutes(0);
        for (final String month : listOfIds) {
            int sumInMinutes = storage.loadTaskSum(this, idFirstOfMonth, selectedTask);
            int balanceValue = 0; // in minutes
            String balanceText = "";
            if (KindOfDay.WORKDAY.equals(selectedTask)) {
                balanceValue = storage.loadBalance(this, idFirstOfMonth, BALANCE_TYPE);
                if (BALANCE_TYPE == BalanceType.AVERAGE_WORK) {
                    balanceText = Time15.fromMinutes(balanceValue).toDecimalForDisplayOfAverage();
                    balanceText = "   (" + MainActivity.AVERAGE_SIGN + " " + balanceText + ")";
                }
            }
            Time15 time15 = Time15.fromMinutes(sumInMinutes);
            String hoursPerMonth = time15.toDecimalFormat();
            while (hoursPerMonth.length() < 7) {
                hoursPerMonth = " " + hoursPerMonth;
            }
            totalYear.plus(time15);
            double numDays = (double) time15.toMinutes() / (double) DaysDataNew.DUE_TOTAL_MINUTES;
            String numDaysString = String.format(Locale.US, "%.1f", numDays);

            row = new TableRow(this);
            row.setLayoutParams(lp);

            row.addView(createTextView(month)); // Name des Monats
            row.addView(createTextView(": "));
            row.addView(createTextView(hoursPerMonth));  // Stunden pro Monat
            row.addView(createTextView(" h = "));
            row.addView(createTextView(numDaysString));  // Tage pro Monat, gerundet auf 1 Nachkommastelle
            row.addView(createTextView(" Tage "));
            row.addView(createTextView(balanceText));
            table.addView(row);
            idFirstOfMonth = TimeUtils.monthForwards(idFirstOfMonth);
        }

        // separator line
        View line = new View(this);
        line.setBackgroundColor(ColorsUI.DARK_BLUE_DEFAULT);
        line.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
        table.addView(line);

        // total over year
        String hoursPerYear = totalYear.toDecimalFormat();
        while (hoursPerYear.length() < 7) {
            hoursPerYear = " " + hoursPerYear;
        }

        double numDays = (double) totalYear.toMinutes() / (double) DaysDataNew.DUE_TOTAL_MINUTES;
        String numDaysString = String.format(Locale.US, "%.1f", numDays);

        row = new TableRow(this);
        row.setLayoutParams(lp);

        row.addView(createTextView("Total")); // Name des Monats
        row.addView(createTextView(": "));
        row.addView(createTextView(hoursPerYear));  // Stunden pro Monat
        row.addView(createTextView(" h = "));
        row.addView(createTextView(numDaysString));  // Tage pro Monat, gerundet auf 1 Nachkommastelle
        row.addView(createTextView(" Tage "));

        table.addView(row);
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
        //textView.setPadding(10, 5, 5, 5);
        textView.setPadding(5, 3, 5, 2);
        textView.setLayoutParams(TEXTVIEW_LAYOUT_PARAMS_FLOW);
        textView.setGravity(Gravity.RIGHT);
        //textView.setTypeface(Typeface.MONOSPACE);
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

        if (id == R.id.action_year_backwards) {
            this.id = TimeUtils.yearBackwards(this.id);
            initialize();
            return true;
        }
        if (id == R.id.action_year_forwards) {
            this.id = TimeUtils.yearForwards(this.id);
            initialize();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_month) {
            startMonthOverviewActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startMonthOverviewActivity() {
        Intent intent = new Intent(this, MonthOverviewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Object item = parent.getItemAtPosition(position);
        Log.i(getClass().getName(), "item : " + item == null ? "null" : item.toString());
        selectedTask = item == null ? KindOfDay.VACATION : KindOfDay.fromString(parent.getItemAtPosition(position).toString());
        updateYearOverViewTable();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedTask = KindOfDay.VACATION;
    }
}
