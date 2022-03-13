package com.mythosapps.time15;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.mythosapps.time15.storage.StorageFacade;
import com.mythosapps.time15.storage.StorageFactory;
import com.mythosapps.time15.types.BalanceType;
import com.mythosapps.time15.types.ColorsUI;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.types.Time15;
import com.mythosapps.time15.util.TimeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
    static final KindOfDay INITIAL_TASK = KindOfDay.WORKDAY;
    private KindOfDay selectedTask = KindOfDay.WORKDAY;
    private SharedPreferences sharedPreferences;
    private BalanceType balanceType;

    private static ViewGroup.LayoutParams TEXTVIEW_LAYOUT_PARAMS_FLOW = new TableRow.LayoutParams(WRAP_CONTENT, MATCH_PARENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_overview);

        storage = StorageFactory.getDataStorage();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarYear);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        id = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        selectedTask = INITIAL_TASK;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("time15", "Instance : " + this.toString());

        initialize();
    }

    private void initialize() {
        setTitle(getString(R.string.year_overview_title) + " " + TimeUtils.getYearDisplayString(id));
        String balanceTypeSetting = sharedPreferences.getString("settings_balance_type", "TOTAL_WORK");
        balanceType = BalanceType.valueOf(balanceTypeSetting);
        if (balanceType == BalanceType.TOTAL_WORK) {
            balanceType = BalanceType.AVERAGE_WORK; // ansonsten ist es 0 wir wollen in der Jahresansicht in jedem Fall einen Durchschnitt
        }

        Spinner yearTaskSpinner = (Spinner) findViewById(R.id.yearTaskSpinner);
        yearTaskSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_text, KindOfDay.dataList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearTaskSpinner.setAdapter(adapter);
        int position = adapter.getPosition(selectedTask.getDisplayString());
        yearTaskSpinner.setSelection(position);

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
        table.setStretchAllColumns(true);

        TableRow row = null;
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

        //List<String> listOfIds = Arrays.asList("Jan", "Feb", "Mär", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez");
        List<String> listOfIds = Arrays.asList("Januar",
                "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember");
        String idFirstJan = "01.01." + TimeUtils.getYearDisplayString(id);
        String idFirstOfMonth = idFirstJan;

        row = new TableRow(this);
        row.setLayoutParams(lp);

        row.addView(createTextView(" ")); // Name des Monats
        row.addView(createTextView("h pro Monat"));  // Stunden pro Monat
        row.addView(createTextView("in Tagen"));  // Tage pro Monat, gerundet auf 1 Nachkommastelle
        row.addView(createTextView("h im Durchschnitt"));
        table.addView(row);

        // separator line
        View line = new View(this);
        line.setBackgroundColor(ColorsUI.DARK_BLUE_DEFAULT);
        line.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
        table.addView(line);

        Time15 totalYear = Time15.fromMinutes(0);
        int balanceYear = 0;
        int balanceYearCountingMonths = 0;
        int monthId = 0;
        for (final String month : listOfIds) {
            monthId++;
            int sumInMinutes = storage.loadTaskSum(this, idFirstOfMonth, selectedTask);
            int balanceValue = 0; // in minutes
            String balanceText = "";
            if (KindOfDay.WORKDAY.equals(selectedTask)) {
                balanceValue = storage.loadBalance(this, idFirstOfMonth, balanceType);
                balanceText = Time15.fromMinutes(balanceValue).toDecimalForDisplayOfAverage();
                balanceText = MainActivity.AVERAGE_SIGN + " " + balanceText;
            }
            Time15 time15 = Time15.fromMinutes(sumInMinutes);
            String hoursPerMonth = time15.toDecimalFormat();
            while (hoursPerMonth.length() < 7) {
                hoursPerMonth = " " + hoursPerMonth;
            }
            totalYear.plus(time15);
            double numDays = (double) time15.toMinutes() / (double) DaysDataNew.DUE_TOTAL_MINUTES;
            String numDaysString = String.format(Locale.US, "%.1f", numDays);

            if (balanceValue > 0) {
                balanceYearCountingMonths++;
                balanceYear += balanceValue;
            }

            row = new TableRow(this);
            row.setLayoutParams(lp);

            row.addView(createTextView(month, ColorsUI.DARK_BLUE_DEFAULT, Gravity.LEFT)); // Name des Monats
            row.addView(createTextView(hoursPerMonth));  // Stunden pro Monat
            row.addView(createTextView(numDaysString));  // Tage pro Monat, gerundet auf 1 Nachkommastelle
            row.addView(createTextView(balanceText));
            int finalMonthId = monthId;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String targetId = "01." + (finalMonthId < 10 ? "0" + finalMonthId : "" + finalMonthId) + "." + TimeUtils.getYearDisplayString(id);
                    Intent intent = new Intent(YearOverviewActivity.this, MonthOverviewActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, targetId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            });

            table.addView(row);
            idFirstOfMonth = TimeUtils.monthForwards(idFirstOfMonth);
        }

        // separator line
        View line2 = new View(this);
        line2.setBackgroundColor(ColorsUI.DARK_BLUE_DEFAULT);
        line2.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
        table.addView(line2);

        // total over year
        String hoursPerYear = totalYear.toDecimalFormat();
        while (hoursPerYear.length() < 7) {
            hoursPerYear = " " + hoursPerYear;
        }

        double numDays = (double) totalYear.toMinutes() / (double) DaysDataNew.DUE_TOTAL_MINUTES;
        String numDaysString = String.format(Locale.US, "%.1f", numDays);

        String balanceYearText = balanceYearCountingMonths == 0 ? Time15.fromMinutes(0).toDecimalForDisplayOfAverage()
                : Time15.fromMinutes(balanceYear / balanceYearCountingMonths).toDecimalForDisplayOfAverage();
        balanceYearText = MainActivity.AVERAGE_SIGN + " " + balanceYearText;

        row = new TableRow(this);
        row.setLayoutParams(lp);

        row.addView(createTextView("Total", ColorsUI.DARK_BLUE_DEFAULT, Gravity.LEFT)); // Name des Monats
        row.addView(createTextView(hoursPerYear));  // Stunden pro Monat
        row.addView(createTextView(numDaysString));  // Tage pro Monat, gerundet auf 1 Nachkommastelle
        row.addView(createTextView(balanceYearText));

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

    private TextView createTextView(String text, int color, int gravity) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setPadding(10, 4, 40, 2);
        textView.setLayoutParams(TEXTVIEW_LAYOUT_PARAMS_FLOW);
        textView.setGravity(gravity);
        //textView.setTypeface(Typeface.MONOSPACE);
        return textView;
    }

    private TextView createTextView(String text) {
        return createTextView(text, ColorsUI.DARK_BLUE_DEFAULT, Gravity.RIGHT);
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
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Object item = parent.getItemAtPosition(position);
        Log.i(getClass().getName(), "item : " + item == null ? "null" : item.toString());
        selectedTask = item == null ? INITIAL_TASK : KindOfDay.fromString(parent.getItemAtPosition(position).toString());
        updateYearOverViewTable();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedTask = INITIAL_TASK;
    }
}
