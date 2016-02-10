package com.mango_apps.time15;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mango_apps.time15.storage.ExternalFileStorage;
import com.mango_apps.time15.storage.NoopStorage;
import com.mango_apps.time15.types.DaysData;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.storage.StorageFacade;
import com.mango_apps.time15.types.Time15;
import com.mango_apps.time15.util.DaysDataUtils;
import com.mango_apps.time15.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Main activity lets the user choose the time they start working and the time they stop working.
 * They can also choose the kind of day: work day, vacation, holiday etc.
 */
public class MainActivity extends ActionBarActivity {
// before material design toolbar: was extends AppCompatActivity

    // Colors
    private static final int DARK_BLUE_DEFAULT = Color.rgb(30, 144, 255);
    private static final int DARK_GREEN_SAVE_SUCCESS = Color.rgb(0, 100, 0);
    private static final int DARK_GREY_SAVE_ERROR = Color.DKGRAY;
    private static final int SELECTION_NONE_BG = Color.TRANSPARENT;
    private static final int SELECTION_BG = Color.rgb(173, 216, 230);

    // Navigation
    public final static String EXTRA_MESSAGE = "com.mango_apps.time15.MESSAGE";

    // Storage
    private StorageFacade storage;

    // View state and view state management
    private String id = null;
    private Integer beginnTime = null;
    private Integer endeTime = null;
    private Integer pauseTime = null;
    private Integer previousSelectionBeginnTime = null;
    private Integer previousSelectionEndeTime = null;
    private Integer previousSelectionPauseTime = null;
    private String previousSelectionKindOfDays = null;
    private Integer beginn15 = null;
    private Integer ende15 = null;
    private String kindOfDay = KindOfDay.WORKDAY.toString();
    private Integer previousSelectionBeginn15 = null;
    private Integer previousSelectionEnde15 = null;
    private HashMap<Integer, Integer> mapBeginnValueToViewId = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> mapBeginn15ValueToViewId = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> mapEndeValueToViewId = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> mapEnde15ValueToViewId = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> mapPauseValueToViewId = new HashMap<Integer, Integer>();
    private int balanceValue;
    private DaysData originalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getName(), "onCreate() started.");
        storage = ExternalFileStorage.getInstance();
        //storage = NoopStorage.getInstance();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initMapWithIds(mapBeginnValueToViewId, R.id.beginnA, R.id.beginnB, R.id.beginnC, R.id.beginnD);
        initMapWithIds(mapEndeValueToViewId, R.id.endeA, R.id.endeB, R.id.endeC, R.id.endeD);
        initMapWithIds(mapBeginn15ValueToViewId, R.id.beginn00, R.id.beginn15, R.id.beginn30, R.id.beginn45);
        initMapWithIds(mapEnde15ValueToViewId, R.id.ende00, R.id.ende15, R.id.ende30, R.id.ende45);
        initMapWithIds(mapPauseValueToViewId, R.id.pauseA, R.id.pauseB, R.id.pauseC, R.id.pauseD);

        balanceValue = storage.loadBalance(this, TimeUtils.createID());

        Log.i(getClass().getName(), "onCreate() finished.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getClass().getName(), "onResume() started.");
        switchToID(null, TimeUtils.createID());
        updateBalance();
        Log.i(getClass().getName(), "onResume() finished.");
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
        if (id == R.id.action_backwards) {
            dateBackwards();
            return true;
        }
        if (id == R.id.action_forwards) {
            dateForwards();
            return true;
        }
        if (id == R.id.action_month) {
            sendMessage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage() {
        Intent intent = new Intent(this, MonthOverviewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        startActivity(intent);
    }

    private void initMapWithIds(Map map, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = (TextView) findViewById(viewId);
            Integer value = Integer.valueOf((String) view.getText());
            map.put(value, viewId);
            Log.i(getClass().getName(), "initMap: " + value + " -> " + viewId);
        }
    }

    private void updateBalance() {
        TextView balance = (TextView) findViewById(R.id.balance);
        String balanceText = Time15.fromMinutes(balanceValue).toDisplayString();
        if (balanceValue > 0) {
            balanceText = "+" + balanceText;
        }
        balance.setText("(" + balanceText + ")");
    }

    private void switchToID(String fromId, String toId) {
        id = toId;
        setTitle(TimeUtils.dayOfWeek(id)
                + ", " + toId);
        DaysData data = storage.loadDaysData(this, id);
        originalData = data;
        resetView();
        if (!TimeUtils.isSameMonth(fromId, id)) {
            balanceValue = storage.loadBalance(this, id);
            updateBalance();
        }
        if (data != null) {
            modelToView(data);
        }
        aktualisiereTotal(false);
        if (data != null) {
            TextView total = (TextView) findViewById(R.id.total);
            total.setTextColor(DARK_GREEN_SAVE_SUCCESS);
        }
    }

    public void dateForwards() {
        Log.i(getClass().getName(), "dateForwards() started.");
        saveKindOfDay();
        switchToID(id, TimeUtils.dateForwards(id));
        Log.i(getClass().getName(), "dateForwards() finished.");
    }

    public void dateBackwards() {
        Log.i(getClass().getName(), "dateBackwards() started.");
        saveKindOfDay();
        switchToID(id, TimeUtils.dateBackwards(id));
        Log.i(getClass().getName(), "dateBackwards() finished.");
    }

    private void saveKindOfDay() {
        if (!previousSelectionKindOfDays.equals(kindOfDay)) {
            DaysData modifiedData = viewToModel();
            TextView day = (TextView) findViewById(R.id.kindOfDay);
            if (storage.saveDaysData(this, modifiedData)) {
                day.setTextColor(DARK_GREEN_SAVE_SUCCESS);
            } else {
                day.setTextColor(DARK_GREY_SAVE_ERROR);
            }
        }
    }

    public void toggleKindOfDay(View v) {
        Log.i(getClass().getName(), "toggleKindOfDay() started.");
        TextView day = (TextView) v;
        kindOfDay = KindOfDay.toggle(kindOfDay);
        aktualisiereKindOfDay(DARK_BLUE_DEFAULT);

        Log.i(getClass().getName(), "toggleKindOfDay() finished.");
    }

    private void aktualisiereKindOfDay(int color) {
        TextView day = (TextView) findViewById(R.id.kindOfDay);
        day.setText(KindOfDay.fromString(kindOfDay).getDisplayString());
        day.setTextColor(color);
    }

    public void verarbeiteKlick(View v) {

        TextView view = (TextView) v;
        int viewId = view.getId();
        boolean isBeginnTime = viewId == R.id.beginnA || viewId == R.id.beginnB || viewId == R.id.beginnC || viewId == R.id.beginnD;
        boolean isEndeTime = viewId == R.id.endeA || viewId == R.id.endeB || viewId == R.id.endeC || viewId == R.id.endeD;
        boolean isBeginn15 = viewId == R.id.beginn00 || viewId == R.id.beginn15 || viewId == R.id.beginn30 || viewId == R.id.beginn45;
        boolean isEnde15 = viewId == R.id.ende00 || viewId == R.id.ende15 || viewId == R.id.ende30 || viewId == R.id.ende45;
        boolean isPauseTime = viewId == R.id.pauseA || viewId == R.id.pauseB || viewId == R.id.pauseC || viewId == R.id.pauseD;
        if (isBeginnTime) {
            setTransparent(previousSelectionBeginnTime);
            view.setBackgroundColor(SELECTION_BG);
            beginnTime = Integer.valueOf((String) view.getText());
            previousSelectionBeginnTime = viewId;
        }
        if (isEndeTime) {
            setTransparent(previousSelectionEndeTime);
            view.setBackgroundColor(SELECTION_BG);
            endeTime = Integer.valueOf((String) view.getText());
            previousSelectionEndeTime = viewId;
        }
        if (isBeginn15) {
            setTransparent(previousSelectionBeginn15);
            view.setBackgroundColor(SELECTION_BG);
            beginn15 = Integer.valueOf((String) view.getText());
            previousSelectionBeginn15 = viewId;
        }
        if (isEnde15) {
            setTransparent(previousSelectionEnde15);
            view.setBackgroundColor(SELECTION_BG);
            ende15 = Integer.valueOf((String) view.getText());
            previousSelectionEnde15 = viewId;
        }
        if (isPauseTime) {
            setTransparent(previousSelectionPauseTime);
            view.setBackgroundColor(SELECTION_BG);
            pauseTime = Integer.valueOf((String) view.getText());
            previousSelectionPauseTime = viewId;
        }
        aktualisiereTotal(true);
    }

    private void aktualisiereTotal(boolean mitSpeichern) {
        TextView total = (TextView) findViewById(R.id.total);
        Time15 totalTime = DaysDataUtils.calculateTotal(beginnTime, beginn15, endeTime, ende15, pauseTime);
        boolean timeSelectionComplete = false;

        if (endeTime != null && beginnTime != null) {
            if (beginn15 != null && ende15 != null) {
                timeSelectionComplete = true;
            }
        }

        total.setText(totalTime.toDisplayString());
        total.setTextColor(DARK_BLUE_DEFAULT);

        if (mitSpeichern && timeSelectionComplete) {
            DaysData modifiedData = viewToModel();
            if (originalData == null) {
                balanceValue += DaysDataUtils.calculateBalance(modifiedData);
            } else {
                balanceValue -= DaysDataUtils.calculateBalance(originalData);
                balanceValue += DaysDataUtils.calculateBalance(modifiedData);
            }

            originalData = modifiedData;
            updateBalance();
            if (storage.saveDaysData(this, modifiedData)) {
                total.setTextColor(DARK_GREEN_SAVE_SUCCESS);
            } else {
                total.setTextColor(DARK_GREY_SAVE_ERROR);
            }
        }
    }

    private DaysData viewToModel() {
        DaysData data = new DaysData(id);
        data.setBegin(beginnTime);
        data.setBegin15(beginn15);
        data.setEnd(endeTime);
        data.setEnd15(ende15);
        data.setPause(pauseTime);
        data.setDay(KindOfDay.fromString(kindOfDay));
        return data;
    }

    private void modelToView(DaysData data) {
        beginnTime = data.getBegin();
        beginn15 = data.getBegin15();
        endeTime = data.getEnd();
        ende15 = data.getEnd15();
        pauseTime = data.getPause();
        kindOfDay = data.getDay().toString();

        previousSelectionPauseTime = mapPauseValueToViewId.get(pauseTime);
        previousSelectionEnde15 = mapEnde15ValueToViewId.get(ende15);
        previousSelectionEndeTime = mapEndeValueToViewId.get(endeTime);
        previousSelectionBeginnTime = mapBeginnValueToViewId.get(beginnTime);
        previousSelectionBeginn15 = mapBeginn15ValueToViewId.get(beginn15);
        previousSelectionKindOfDays = kindOfDay;

        setSelected(previousSelectionPauseTime);
        setSelected(previousSelectionEnde15);
        setSelected(previousSelectionEndeTime);
        setSelected(previousSelectionBeginnTime);
        setSelected(previousSelectionBeginn15);

        aktualisiereKindOfDay(DARK_GREEN_SAVE_SUCCESS);
    }

    private void resetView() {

        setTransparent(previousSelectionPauseTime);
        setTransparent(previousSelectionEnde15);
        setTransparent(previousSelectionEndeTime);
        setTransparent(previousSelectionBeginnTime);
        setTransparent(previousSelectionBeginn15);

        beginnTime = null;
        beginn15 = null;
        endeTime = null;
        ende15 = null;
        pauseTime = null;
        kindOfDay = KindOfDay.WORKDAY.toString();
        aktualisiereKindOfDay(DARK_BLUE_DEFAULT);
        previousSelectionPauseTime = null;
        previousSelectionEnde15 = null;
        previousSelectionEndeTime = null;
        previousSelectionBeginnTime = null;
        previousSelectionBeginn15 = null;
        previousSelectionKindOfDays = kindOfDay;
    }

    private void setTransparent(Integer viewId) {
        if (viewId != null) {
            TextView previousView = (TextView) findViewById(viewId);
            previousView.setBackgroundColor(SELECTION_NONE_BG);
        }
    }

    private void setSelected(Integer viewId) {
        if (viewId != null) {
            TextView previousView = (TextView) findViewById(viewId);
            previousView.setBackgroundColor(SELECTION_BG);
        }
    }
}
