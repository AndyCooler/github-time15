package com.mango_apps.time15;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mango_apps.time15.storage.ExternalFileStorage;
import com.mango_apps.time15.storage.NoopStorage;
import com.mango_apps.time15.storage.StorageFacade;
import com.mango_apps.time15.storage.StorageFactory;
import com.mango_apps.time15.types.ColorsUI;
import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.util.DaysDataUtils;
import com.mango_apps.time15.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity lets the user see on how many days they were working in a month, and what kind of
 * day each day was.
 */
public class MonthOverviewActivity extends ActionBarActivity {

    // Navigation
    public final static String EXTRA_MESSAGE = "com.mango_apps.time15.MESSAGE";

    // Storage
    private StorageFacade storage;

    // View state and view state management
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getName(), "onCreate() started.");
        setContentView(R.layout.activity_month_overview);

        storage = StorageFactory.getStorage();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMonth);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        id = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        Log.i(getClass().getName(), "onCreate() finished.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getClass().getName(), "onResume() started.");
        setTitle(TimeUtils.getMonthYearDisplayString(id));

        TableLayout table = (TableLayout) findViewById(R.id.tableView);
        table.setColumnShrinkable(3, true);
        table.setColumnStretchable(3, true);
        TableRow row = null;

        List<String> listOfIds = TimeUtils.getListOfIdsOfMonth(id);
        int lastWeekOfYear = -1;
        boolean sameWeek = true;
        for (final String dayId : listOfIds) {
            DaysData data = storage.loadDaysData(this, dayId);
            if (data != null) {
                String hours = "";
                String extraVacationHours = "";
                if (KindOfDay.isDueDay(data.getDay())) {
                    hours += DaysDataUtils.calculateTotal(data).toDisplayString() + " h";
                    if (KindOfDay.WORKDAY_SOME_VACATION.equals(data.getDay())) {
                        extraVacationHours = " (+" + data.getOtherHours() + " h)";
                    }
                }
                row = new TableRow(this);
                int rowColor = ColorsUI.DARK_BLUE_DEFAULT;
                switch (data.getDay()) {
                    case WORKDAY:
                    case WORKDAY_SOME_VACATION:
                        rowColor = ColorsUI.DARK_BLUE_DEFAULT;
                        break;
                    case HOLIDAY:
                    case VACATION:
                        rowColor = ColorsUI.DARK_GREEN_SAVE_SUCCESS;
                        break;
                    case SICKDAY:
                    case KIDSICKDAY:
                        rowColor = ColorsUI.DARK_GREY_SAVE_ERROR;
                        break;
                }
                row.addView(createTextView(TimeUtils.dayOfWeek(dayId), rowColor));
                row.addView(createTextView(dayId.substring(0, 2), rowColor));
                row.addView(createTextView(data.getDay().getDisplayString(), rowColor));
                row.addView(createTextView(hours, rowColor));
                row.addView(createTextView(extraVacationHours, rowColor));

                TextView view = new TextView(this);
                view.setBackgroundColor(ColorsUI.SELECTION_BG);
                //view.setText(String.valueOf(weekOfYear));
                row.addView(view);

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startMainActivity(dayId);
                    }
                });

                int weekOfYear = TimeUtils.getWeekOfYear(dayId);
                if (weekOfYear != lastWeekOfYear) {
                    if (lastWeekOfYear != -1) {
                        View line = new View(this);
                        line.setBackgroundColor(ColorsUI.DARK_BLUE_DEFAULT);
                        line.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                        table.addView(line);
                    }
                    lastWeekOfYear = weekOfYear;
                }

                table.addView(row);
            }
        }
        Log.i(getClass().getName(), "onResume() finished.");
    }

    private TextView createTextView(String text, int color) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setGravity(Gravity.LEFT);
        textView.setPadding(15, 5, 15, 5);
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

}
