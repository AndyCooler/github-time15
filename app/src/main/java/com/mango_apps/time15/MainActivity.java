package com.mango_apps.time15;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mango_apps.time15.storage.StorageFacade;
import com.mango_apps.time15.storage.StorageFactory;
import com.mango_apps.time15.types.BeginEndTask;
import com.mango_apps.time15.types.ColorsUI;
import com.mango_apps.time15.types.DaysDataNew;
import com.mango_apps.time15.types.KindOfDay;
import com.mango_apps.time15.types.NumberTask;
import com.mango_apps.time15.types.Task;
import com.mango_apps.time15.types.Time15;
import com.mango_apps.time15.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Main activity lets the user choose the time they start working and the time they stop working.
 * They can also choose the kind of day: work day, vacation, holiday etc.
 */
public class MainActivity extends AppCompatActivity {
// before material design toolbar: was extends AppCompatActivity

    // Navigation
    public final static String EXTRA_MESSAGE = "com.mango_apps.time15.MESSAGE";

    // Storage
    private StorageFacade storage;

    // View state and view state management
    private String id = null;
    private int taskNo = 0;
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
    private HashMap<Integer, Integer> mapBeginnValueToViewId = new HashMap<>();
    private HashMap<Integer, Integer> mapBeginn15ValueToViewId = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> mapEndeValueToViewId = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> mapEnde15ValueToViewId = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> mapPauseValueToViewId = new HashMap<Integer, Integer>();
    private int balanceValue;
    private DaysDataNew originalData;
    private DaysDataNew modifyableData;
    private Integer numberTaskHours = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String intentsId = getIntentsId();
        Log.i(getClass().getName(), "onCreate() started with id " + intentsId);
        storage = StorageFactory.getStorage();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initMapWithIds(mapBeginnValueToViewId, R.id.beginnA, R.id.beginnB, R.id.beginnC, R.id.beginnD);
        initMapWithIds(mapEndeValueToViewId, R.id.endeA, R.id.endeB, R.id.endeC, R.id.endeD);
        initMapWithIds(mapBeginn15ValueToViewId, R.id.beginn00, R.id.beginn15, R.id.beginn30, R.id.beginn45);
        initMapWithIds(mapEnde15ValueToViewId, R.id.ende00, R.id.ende15, R.id.ende30, R.id.ende45);
        initMapWithIds(mapPauseValueToViewId, R.id.pauseA, R.id.pauseB, R.id.pauseC, R.id.pauseD);

        balanceValue = storage.loadBalance(this, intentsId);

        Log.i(getClass().getName(), "onCreate() finished.");
    }

    private String getIntentsId() {
        String result = TimeUtils.createID();
        Intent intent = getIntent();
        if (intent != null) {
            String withId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
            if (withId != null) {
                result = withId;
            }
        }
        return result;
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
        String intentsId = getIntentsId();
        Log.i(getClass().getName(), "onResume() started with id " + intentsId);
        switchToID(null, intentsId);
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
        if (id == R.id.action_today) {
            dateToday();
            return true;
        }
        if (id == R.id.action_month) {
            startMonthOverviewActivity();
            return true;
        }
        if (id == R.id.action_about) {
            Toast.makeText(MainActivity.this, "Time15 von Andreas, Version: 0.5", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startMonthOverviewActivity() {
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

    private void updateMapWithIds(Map map, int newBeginValue, int... viewIds) {
        Integer value = newBeginValue;
        map.clear();
        for (int viewId : viewIds) {
            TextView view = (TextView) findViewById(viewId);
            view.setText(value < 10 ? "0" + String.valueOf(value) : String.valueOf(value));
            map.put(value, viewId);
            Log.i(getClass().getName(), "updateMap: " + value + " -> " + viewId);
            value++;
        }
    }

    private void updateBalance() {
        TextView balance = (TextView) findViewById(R.id.balance);
        String balanceText = Time15.fromMinutes(balanceValue).toDisplayStringWithSign();
        balance.setText("(" + balanceText + ")");
    }

    private void switchToID(String fromId, String toId) {
        id = toId;
        setTitle(TimeUtils.getMainTitleString(id));
        originalData = storage.loadDaysDataNew(this, id);
        modifyableData = DaysDataNew.copy(originalData);
        if (modifyableData == null) {
            modifyableData = new DaysDataNew(id);
        }
        taskNo = 0;
        resetView();
        if (fromId != null && !TimeUtils.isSameMonth(fromId, id)) {
            balanceValue = storage.loadBalance(this, id);
            updateBalance();
        }
        modelToView();
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

    public void dateToday() {
        Log.i(getClass().getName(), "dateToday() started.");
        saveKindOfDay();
        switchToID(id, TimeUtils.createID());
        Log.i(getClass().getName(), "dateToday() finished.");

    }

    public void beginEarlier(View view) {
        Log.i(getClass().getName(), "beginEarlier() started.");
        TextView textView = (TextView) findViewById(R.id.beginnA);
        Integer hour = Integer.valueOf((String) textView.getText()) - 1;
        updateMapToBeginAt(intoRange(hour));
        Log.i(getClass().getName(), "beginEarlier() finished.");
    }

    public void beginLater(View view) {
        Log.i(getClass().getName(), "beginLater() started.");
        TextView textView = (TextView) findViewById(R.id.beginnA);
        Integer hour = Integer.valueOf((String) textView.getText()) + 1;
        updateMapToBeginAt(intoRange(hour));
        Log.i(getClass().getName(), "beginLater() finished.");
    }

    public void beginAt(Integer hour) {
        Log.i(getClass().getName(), "beginAt() started." + hour);
        if (hour == null || beginHourVisible(hour)) {
            return;
        }
        updateMapToBeginAt(intoRange(hour));
        Log.i(getClass().getName(), "beginAt() finished.");
    }

    public void endAt(Integer hour) {
        Log.i(getClass().getName(), "endAt() started." + hour);
        if (hour == null || endHourVisible(hour)) {
            return;
        }
        updateMapToEndAt(intoRange(hour));
        Log.i(getClass().getName(), "endAt() finished.");
    }

    private void updateMapToEndAt(Integer newValue) {
        resetView();
        updateMapWithIds(mapEndeValueToViewId, newValue, R.id.endeA, R.id.endeB, R.id.endeC, R.id.endeD);
        modelToView();
    }

    private boolean beginHourVisible(int hour) {
        return mapBeginnValueToViewId.get(hour) != null;
    }

    private boolean endHourVisible(int hour) {
        return mapEndeValueToViewId.get(hour) != null;
    }

    private void updateMapToBeginAt(Integer newValue) {
        resetView();
        updateMapWithIds(mapBeginnValueToViewId, newValue, R.id.beginnA, R.id.beginnB, R.id.beginnC, R.id.beginnD);
        modelToView();
    }

    private Integer intoRange(int hour) {
        Integer newValue = hour < 0 ? 0 : hour;
        if (hour < 0) {
            newValue = 0;
        }
        if (hour > 20) {
            newValue = 20;
        }
        return newValue;
    }

    public void endEarlier(View view) {
        Log.i(getClass().getName(), "endEarlier() started.");
        TextView textView = (TextView) findViewById(R.id.endeA);
        Integer hour = Integer.valueOf((String) textView.getText()) - 1;
        updateMapToEndAt(intoRange(hour));
        Log.i(getClass().getName(), "endEarlier() finished.");
    }

    public void endLater(View view) {
        Log.i(getClass().getName(), "endLater() started.");
        TextView textView = (TextView) findViewById(R.id.endeA);
        Integer hour = Integer.valueOf((String) textView.getText()) + 1;
        updateMapToEndAt(intoRange(hour));
        Log.i(getClass().getName(), "endLater() finished.");
    }

    private void saveKindOfDay() {
        if (!previousSelectionKindOfDays.equals(kindOfDay)) {
            save();
        }
    }

    public void toggleKindOfDay(View v) {
        Log.i(getClass().getName(), "toggleKindOfDay() started.");
        kindOfDay = KindOfDay.toggle(kindOfDay);
        aktualisiereKindOfDay(ColorsUI.DARK_BLUE_DEFAULT);

        Log.i(getClass().getName(), "toggleKindOfDay() finished.");
    }

    private void aktualisiereKindOfDay(int color) {
        TextView day = (TextView) findViewById(R.id.kindOfDay);
        day.setText(KindOfDay.fromString(kindOfDay).getDisplayString() + ">>");
        day.setTextColor(color);
    }

    public void addTask(View v) {
        Log.i(getClass().getName(), "addTask() started at task #" + taskNo);
        if (modifyableData.getNumberOfTasks() > 1) {
            Toast.makeText(MainActivity.this, "Nur 2 Aufgaben pro Tag!", Toast.LENGTH_SHORT).show();
        } else {
            taskNo = 1;
            NumberTask task1 = new NumberTask();
            task1.setKindOfDay(KindOfDay.VACATION);
            task1.setTotal(Time15.fromMinutes(0));
            modifyableData.addTask(task1);
            resetView();
            modelToView();
        }
        Log.i(getClass().getName(), "addTask() finished at task #" + taskNo);
    }

    public void switchTasks(View v) {
        Log.i(getClass().getName(), "switchTasks() started at task #" + taskNo);
        if (modifyableData.getNumberOfTasks() == 2) {
            resetView();
            taskNo = (taskNo + 1) % modifyableData.getNumberOfTasks();
            modelToView();
        } else {
            Toast.makeText(MainActivity.this, "Erst mit + eine Aufgabe hinzufügen!", Toast.LENGTH_SHORT).show();
        }
        Log.i(getClass().getName(), "switchTasks() finished at task #" + taskNo);
    }

    public void aktualisiereTaskNo() {
        Button switchTasksButton = (Button) findViewById(R.id.switchTasksButton);
        switchTasksButton.setText(String.valueOf(taskNo + 1));
    }

    /**
     * Click in dialog area for begin-end task.
     *
     * @param v
     */
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
            view.setBackgroundColor(ColorsUI.SELECTION_BG);
            beginnTime = Integer.valueOf((String) view.getText());
            previousSelectionBeginnTime = viewId;
        }
        if (isEndeTime) {
            setTransparent(previousSelectionEndeTime);
            view.setBackgroundColor(ColorsUI.SELECTION_BG);
            endeTime = Integer.valueOf((String) view.getText());
            previousSelectionEndeTime = viewId;
        }
        if (isBeginn15) {
            setTransparent(previousSelectionBeginn15);
            view.setBackgroundColor(ColorsUI.SELECTION_BG);
            beginn15 = Integer.valueOf((String) view.getText());
            previousSelectionBeginn15 = viewId;
        }
        if (isEnde15) {
            setTransparent(previousSelectionEnde15);
            view.setBackgroundColor(ColorsUI.SELECTION_BG);
            ende15 = Integer.valueOf((String) view.getText());
            previousSelectionEnde15 = viewId;
        }
        if (isPauseTime) {
            setTransparent(previousSelectionPauseTime);
            view.setBackgroundColor(ColorsUI.SELECTION_BG);
            pauseTime = Integer.valueOf((String) view.getText());
            previousSelectionPauseTime = viewId;
        }

        if (beginEndTimeSelectionComplete()) {
            save();
        }
    }

    private boolean beginEndTimeSelectionComplete() {
        if (endeTime != null && beginnTime != null) {
            if (beginn15 != null && ende15 != null) {
                return true;
            }
        }
        return false;
    }

    private void aktualisiereTotal(int color) {
        TextView total = (TextView) findViewById(R.id.total);
        Time15 totalTime = null;
        if (modifyableData == null || modifyableData.getTask(taskNo) == null) {
            totalTime = Time15.fromMinutes(0);
        } else {
            totalTime = modifyableData.getTask(taskNo).getTotal();
        }

        total.setText(totalTime.toDisplayString());
        total.setTextColor(color);
    }

    public void save() {
        Log.i(getClass().getName(), "saving...");
        viewToModel();
        if (originalData == null) {
            balanceValue += modifyableData.getBalance();
        } else {
            balanceValue -= originalData.getBalance();
            balanceValue += modifyableData.getBalance();
        }

        // TODO only if hasChanged d.h. if modifyableData != originalData
        int totalNewColor = storage.saveDaysDataNew(this, modifyableData) ? ColorsUI.DARK_GREEN_SAVE_SUCCESS : ColorsUI.DARK_GREY_SAVE_ERROR;
        aktualisiereTotal(totalNewColor);
        aktualisiereKindOfDay(totalNewColor);
        originalData = modifyableData;
        modifyableData = DaysDataNew.copy(originalData);
        //modelToView(); // TODO shouldn't be necessary
        updateBalance();
        Log.i(getClass().getName(), "saving...done.");
    }

    public void totalMore(View v) {
        if (modifyableData != null && modifyableData.getTask(taskNo) != null) {
            if (numberTaskHours != null) {
                numberTaskHours++;
                save();
            }
        }
    }

    public void totalLess(View v) {
        if (modifyableData != null && modifyableData.getTask(taskNo) != null) {
            if (numberTaskHours != null) {
                numberTaskHours--;
                save();
            }
        }
    }

    private void viewToModel() {
        Log.i(getClass().getName(), "viewToModel() started.");
        Task task = modifyableData.getTask(taskNo);
        if (task == null) {
            // TODO erstmal werden nur neue BeginEndTask im Modell gespeichert:
            task = new BeginEndTask();
            modifyableData.addTask(task);
        } else {
            task = modifyableData.getTask(taskNo);
        }

        if (task instanceof BeginEndTask) {
            BeginEndTask task0 = (BeginEndTask) task;
            task0.setBegin(beginnTime);
            task0.setBegin15(beginn15);
            task0.setEnd(endeTime);
            task0.setEnd15(ende15);
            task0.setPause(pauseTime);
            task0.setKindOfDay(KindOfDay.fromString(kindOfDay));
        } else if (task instanceof NumberTask) {
            NumberTask task1 = (NumberTask) task;
            task1.setTotal(Time15.fromMinutes(numberTaskHours == null ? 0 : numberTaskHours * 60));
            task1.setKindOfDay(KindOfDay.fromString(kindOfDay));
        }
        Log.i(getClass().getName(), "viewToModel() finished.");
    }

    private void modelToView() {
        Log.i(getClass().getName(), "modelToView() started.");
        if (modifyableData.getTask(taskNo) == null) {
            return;
        }
        Task task = modifyableData.getTask(taskNo);
        boolean isLoadedData = originalData != null;
        int totalNewColor = isLoadedData ? ColorsUI.DARK_GREEN_SAVE_SUCCESS : ColorsUI.DARK_BLUE_DEFAULT;

        if (task instanceof BeginEndTask) {
            BeginEndTask task0 = (BeginEndTask) task;
            beginnTime = task0.getBegin();
            beginn15 = task0.getBegin15();
            endeTime = task0.getEnd();
            ende15 = task0.getEnd15();
            pauseTime = task0.getPause();

            beginAt(beginnTime);
            endAt(endeTime);

            previousSelectionPauseTime = mapPauseValueToViewId.get(pauseTime);
            previousSelectionEnde15 = mapEnde15ValueToViewId.get(ende15);
            previousSelectionEndeTime = mapEndeValueToViewId.get(endeTime);
            previousSelectionBeginnTime = mapBeginnValueToViewId.get(beginnTime);
            previousSelectionBeginn15 = mapBeginn15ValueToViewId.get(beginn15);

            setSelected(previousSelectionPauseTime);
            setSelected(previousSelectionEnde15);
            setSelected(previousSelectionEndeTime);
            setSelected(previousSelectionBeginnTime);
            setSelected(previousSelectionBeginn15);

            setTransparent(R.id.total);
        } else if (task instanceof NumberTask) {

            NumberTask task1 = (NumberTask) task;
            numberTaskHours = task1.getTotal().getHours();

            setSelected(R.id.total);
        } else {
            throw new IllegalStateException(id + " : unknown type of task : " + task == null ? "null" : task.getClass().getName());
        }

        kindOfDay = task.getKindOfDay().toString();
        previousSelectionKindOfDays = kindOfDay;
        aktualisiereTaskNo();
        aktualisiereTotal(totalNewColor);
        aktualisiereKindOfDay(totalNewColor);
        Log.i(getClass().getName(), "modelToView() finished.");
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
        aktualisiereKindOfDay(ColorsUI.DARK_BLUE_DEFAULT);
        aktualisiereTotal(ColorsUI.DARK_BLUE_DEFAULT);
        previousSelectionPauseTime = null;
        previousSelectionEnde15 = null;
        previousSelectionEndeTime = null;
        previousSelectionBeginnTime = null;
        previousSelectionBeginn15 = null;
        previousSelectionKindOfDays = kindOfDay;
        numberTaskHours = null;
        setTransparent(R.id.total);
    }

    private void setTransparent(Integer viewId) {
        if (viewId != null) {
            TextView previousView = (TextView) findViewById(viewId);
            previousView.setBackgroundColor(ColorsUI.SELECTION_NONE_BG);
        }
    }

    private void setSelected(Integer viewId) {
        if (viewId != null) {
            TextView previousView = (TextView) findViewById(viewId);
            previousView.setBackgroundColor(ColorsUI.SELECTION_BG);
        }
    }

    private void setDeactivated(Integer viewId) {
        if (viewId != null) {
            TextView previousView = (TextView) findViewById(viewId);
            previousView.setBackgroundColor(ColorsUI.DEACTIVATED);
        }
    }
}
