package com.mythosapps.time15;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mythosapps.time15.storage.StorageFacade;
import com.mythosapps.time15.storage.StorageFactory;
import com.mythosapps.time15.types.BeginEndTask;
import com.mythosapps.time15.types.ColorsUI;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.types.Time15;
import com.mythosapps.time15.util.AppVersion;
import com.mythosapps.time15.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Main activity lets the user choose the time they start working and the time they stop working.
 * They can also choose the kind of day: work day, vacation, holiday etc.
 */
public class MainActivity extends AppCompatActivity {
// before material design toolbar: was extends AppCompatActivity

    // Navigation
    public final static String EXTRA_MESSAGE = "com.mythosapps.time15.MESSAGE";

    // Storage
    private StorageFacade storage;

    // View state and view state management
    private String id = null;
    private int taskNo = 0;
    private Integer beginnTime = null;
    private Integer endeTime = null;
    private Integer pauseTime = null;
    private Integer beginn15 = null;
    private Integer ende15 = null;
    private String kindOfDay = KindOfDay.WORKDAY.toString();
    private Integer previousSelectionBeginnTime = null;
    private Integer previousSelectionEndeTime = null;
    private Integer previousSelectionPauseTime = null;
    private Integer previousSelectionBeginn15 = null;
    private Integer previousSelectionEnde15 = null;
    private String previousSelectionKindOfDays = null;
    private HashMap<Integer, Integer> mapBeginnValueToViewId = new HashMap<>();
    private HashMap<Integer, Integer> mapBeginn15ValueToViewId = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> mapEndeValueToViewId = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> mapEnde15ValueToViewId = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> mapPauseValueToViewId = new HashMap<Integer, Integer>();
    private int balanceValue;
    private DaysDataNew originalData;
    private DaysDataNew modifiableData;
    private Integer numberTaskHours = null;
    private Integer numberTaskMinutes = null;
    private TextView total;
    private TextView totalSemi;
    private TextView total15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String intentsId = getIntentsId();
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
    }

    private void setBeginEndSelectionActivated(boolean activated) {
        for (Integer viewId : mapBeginnValueToViewId.values()) {
            setActivation(viewId, activated);
        }
        for (Integer viewId : mapEndeValueToViewId.values()) {
            setActivation(viewId, activated);
        }
        for (Integer viewId : mapBeginn15ValueToViewId.values()) {
            setActivation(viewId, activated);
        }
        for (Integer viewId : mapEnde15ValueToViewId.values()) {
            setActivation(viewId, activated);
        }
        for (Integer viewId : mapPauseValueToViewId.values()) {
            setActivation(viewId, activated);
        }
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

        switchToID(null, intentsId);
        updateBalance();
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
            save();
            startMonthOverviewActivity();
            return true;
        }
        if (id == R.id.action_about) {
            Toast.makeText(MainActivity.this, "Time15 von Andreas, \nVersion: " + AppVersion.getVersionName(this) + "\nBuild-ID:" + AppVersion.getVersionCode(this), Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_year) {
            save();
            startYearOverviewActivity();
            return true;
        }
        if (id == R.id.action_delete) {
            deleteTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteTask() {
        if (modifiableData == null || modifiableData.getNumberOfTasks() == 0) {
            Toast.makeText(MainActivity.this, "Nichts zu löschen!", Toast.LENGTH_SHORT).show();
        } else {
            originalData = null;
            modifiableData.deleteTask(modifiableData.getTask(taskNo));
            if (modifiableData.getNumberOfTasks() == 0) {
                modifiableData = new DaysDataNew(id);
            }
            taskNo = 0;
            resetView();
            modelToView();
            save();
        }
    }


    public void startMonthOverviewActivity() {
        Intent intent = new Intent(this, MonthOverviewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        startActivity(intent);
    }

    public void startYearOverviewActivity() {
        Intent intent = new Intent(this, YearOverviewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        startActivity(intent);
    }

    private void initMapWithIds(Map map, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = (TextView) findViewById(viewId);
            Integer value = Integer.valueOf((String) view.getText());
            map.put(value, viewId);
        }
    }

    private void updateMapWithIds(Map map, int newBeginValue, int... viewIds) {
        Integer value = newBeginValue;
        map.clear();
        for (int viewId : viewIds) {
            TextView view = (TextView) findViewById(viewId);
            view.setText(value < 10 ? "0" + String.valueOf(value) : String.valueOf(value));
            map.put(value, viewId);
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
        modifiableData = DaysDataNew.copy(originalData);
        if (modifiableData == null) {
            modifiableData = new DaysDataNew(id);
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
        saveKindOfDay();
        switchToID(id, TimeUtils.dateForwards(id));
    }

    public void dateBackwards() {
        saveKindOfDay();
        switchToID(id, TimeUtils.dateBackwards(id));
    }

    public void dateToday() {
        saveKindOfDay();
        switchToID(id, TimeUtils.createID());
    }

    public void beginEarlier(View view) {
        TextView textView = (TextView) findViewById(R.id.beginnA);
        Integer hour = Integer.valueOf((String) textView.getText()) - 1;
        updateMapToBeginAt(intoRange(hour));
    }

    public void beginLater(View view) {
        TextView textView = (TextView) findViewById(R.id.beginnA);
        Integer hour = Integer.valueOf((String) textView.getText()) + 1;
        updateMapToBeginAt(intoRange(hour));
    }

    public void beginAt(Integer hour) {
        if (hour == null || isBeginHourVisible(hour)) {
            return;
        }
        updateMapToBeginAt(intoRange(hour));
    }

    public void endAt(Integer hour) {
        if (hour == null || isEndHourVisible(hour)) {
            return;
        }
        updateMapToEndAt(intoRange(hour));
    }

    private void updateMapToEndAt(Integer newValue) {
        resetView();
        updateMapWithIds(mapEndeValueToViewId, newValue, R.id.endeA, R.id.endeB, R.id.endeC, R.id.endeD);
        modelToView();
    }

    private boolean isBeginHourVisible(int hour) {
        return mapBeginnValueToViewId.get(hour) != null;
    }

    private boolean isEndHourVisible(int hour) {
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
        TextView textView = (TextView) findViewById(R.id.endeA);
        Integer hour = Integer.valueOf((String) textView.getText()) - 1;
        updateMapToEndAt(intoRange(hour));
    }

    public void endLater(View view) {
        TextView textView = (TextView) findViewById(R.id.endeA);
        Integer hour = Integer.valueOf((String) textView.getText()) + 1;
        updateMapToEndAt(intoRange(hour));
    }

    private void saveKindOfDay() {
        if (!previousSelectionKindOfDays.equals(kindOfDay)) {
            save();
        }
    }

    public void toggleKindOfDay(View v) {
        kindOfDay = KindOfDay.toggle(kindOfDay);
        if (KindOfDay.isBeginEndType(KindOfDay.fromString(kindOfDay))) {
            numberTaskHours = null;
            numberTaskMinutes = null;
        } else {
            beginnTime = null;
            beginn15 = null;
            endeTime = null;
            ende15 = null;
            pauseTime = null;
            numberTaskHours = DaysDataNew.DUE_HOURS_PER_DAY; // TODO init with due hours per task
            numberTaskMinutes = 0;
        }
        viewToModel();
        resetView();
        modelToView();

    }

    private void aktualisiereKindOfDay(int color) {
        TextView day = (TextView) findViewById(R.id.kindOfDay);
        day.setText(KindOfDay.fromString(kindOfDay).getDisplayString() + ">>");
        day.setTextColor(color);
        setSelected(R.id.kindOfDay);
    }

    public void addTask(View v) {
        if (modifiableData.getNumberOfTasks() > 1) {
            Toast.makeText(MainActivity.this, "Nur 2 Aufgaben pro Tag!", Toast.LENGTH_SHORT).show();
        } else {
            taskNo = 1;
            BeginEndTask task1 = new BeginEndTask();
            task1.setKindOfDay(KindOfDay.VACATION);
            task1.setTotal(Time15.fromMinutes(0));
            modifiableData.addTask(task1);
            resetView();
            modelToView();
        }
    }

    public void switchTasks(View v) {
        if (modifiableData.getNumberOfTasks() == 2) {
            resetView();
            taskNo = (taskNo + 1) % modifiableData.getNumberOfTasks();
            modelToView();
        } else {
            Toast.makeText(MainActivity.this, "Erst mit + eine Aufgabe hinzufügen!", Toast.LENGTH_SHORT).show();
        }
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

        if (KindOfDay.isBeginEndType(kindOfDay)) {

        TextView view = (TextView) v;
        int viewId = view.getId();
        boolean isBeginnTime = viewId == R.id.beginnA || viewId == R.id.beginnB || viewId == R.id.beginnC || viewId == R.id.beginnD;
        boolean isEndeTime = viewId == R.id.endeA || viewId == R.id.endeB || viewId == R.id.endeC || viewId == R.id.endeD;
        boolean isBeginn15 = viewId == R.id.beginn00 || viewId == R.id.beginn15 || viewId == R.id.beginn30 || viewId == R.id.beginn45;
        boolean isEnde15 = viewId == R.id.ende00 || viewId == R.id.ende15 || viewId == R.id.ende30 || viewId == R.id.ende45;
        boolean isPauseTime = viewId == R.id.pauseA || viewId == R.id.pauseB || viewId == R.id.pauseC || viewId == R.id.pauseD;
        boolean isSelected = false;
        boolean isDeselected = false;
        if (isBeginnTime) {
            if (previousSelectionBeginnTime != null && viewId == previousSelectionBeginnTime) {
                setTransparent(viewId);
                beginnTime = null;
                previousSelectionBeginnTime = null;
                isDeselected = true;
            } else {
                setTransparent(previousSelectionBeginnTime);
                view.setBackgroundColor(ColorsUI.SELECTION_BG);
                beginnTime = Integer.valueOf((String) view.getText());
                previousSelectionBeginnTime = viewId;
                isSelected = true;
            }
        }
        if (isEndeTime) {
            if (previousSelectionEndeTime != null && viewId == previousSelectionEndeTime) {
                setTransparent(viewId);
                endeTime = null;
                previousSelectionEndeTime = null;
                isDeselected = true;
            } else {
                setTransparent(previousSelectionEndeTime);
                view.setBackgroundColor(ColorsUI.SELECTION_BG);
                endeTime = Integer.valueOf((String) view.getText());
                previousSelectionEndeTime = viewId;
                isSelected = true;
            }
        }
        if (isBeginn15) {
            if (previousSelectionBeginn15 != null && viewId == previousSelectionBeginn15) {
                setTransparent(viewId);
                beginn15 = null;
                previousSelectionBeginn15 = null;
                isDeselected = true;
            } else {
                setTransparent(previousSelectionBeginn15);
                view.setBackgroundColor(ColorsUI.SELECTION_BG);
                beginn15 = Integer.valueOf((String) view.getText());
                previousSelectionBeginn15 = viewId;
                isSelected = true;
            }
        }
        if (isEnde15) {
            if (previousSelectionEnde15 != null && viewId == previousSelectionEnde15) {
                setTransparent(viewId);
                ende15 = null;
                previousSelectionEnde15 = null;
                isDeselected = true;
            } else {
                setTransparent(previousSelectionEnde15);
                view.setBackgroundColor(ColorsUI.SELECTION_BG);
                ende15 = Integer.valueOf((String) view.getText());
                previousSelectionEnde15 = viewId;
                isSelected = true;
            }
        }
        if (isPauseTime) {
            if (previousSelectionPauseTime != null && viewId == previousSelectionPauseTime) {
                setTransparent(viewId);
                pauseTime = null;
                previousSelectionPauseTime = null;
                isDeselected = true;
            } else {
                setTransparent(previousSelectionPauseTime);
                view.setBackgroundColor(ColorsUI.SELECTION_BG);
                pauseTime = Integer.valueOf((String) view.getText());
                previousSelectionPauseTime = viewId;
                isSelected = true;
            }
        }

        if (isSelected || isDeselected) {
            save();
        }
        }
    }

    private void aktualisiereTotal(int color) {

        Time15 totalTime = null;
        if (modifiableData == null || modifiableData.getTask(taskNo) == null) {
            totalTime = Time15.fromMinutes(0);
        } else {
            totalTime = modifiableData.getTask(taskNo).getTotal();
        }
        total = (TextView) findViewById(R.id.total);
        totalSemi = (TextView) findViewById(R.id.totalSemi);
        total15 = (TextView) findViewById(R.id.total15);

        total.setText(totalTime.getHoursDisplayString());
        total.setTextColor(color);
        totalSemi.setTextColor(color);
        total15.setText(totalTime.getMinutesDisplayString());
        total15.setTextColor(color);
    }

    public void save() {
        viewToModel();

        // save only if user changed something
        if (originalData == null) {
            if (modifiableData.isInInitialState()) {
                return;
            }
        } else {
            if (originalData.equals(modifiableData)) {
                return;
            }
        }

        if (originalData == null) {
            balanceValue += modifiableData.getBalance();
        } else {
            balanceValue -= originalData.getBalance();
            balanceValue += modifiableData.getBalance();
        }

        int totalNewColor = 0;
        if (storage.saveDaysDataNew(this, modifiableData)) {
            if (modifiableData.getNumberOfTasks() == 0) {
                totalNewColor = ColorsUI.DARK_BLUE_DEFAULT;
            } else {
                totalNewColor = ColorsUI.DARK_GREEN_SAVE_SUCCESS;
            }
        } else {
            totalNewColor = ColorsUI.DARK_GREY_SAVE_ERROR;
        }

        aktualisiereTotal(totalNewColor);
        aktualisiereKindOfDay(totalNewColor);
        originalData = modifiableData;
        modifiableData = DaysDataNew.copy(originalData);
        modelToView(); // TODO shouldn't be necessary
        updateBalance();
    }

    public void toggleTotal(View v) {
        if (!KindOfDay.isBeginEndType(kindOfDay)) {
            if (numberTaskHours != null) {
                numberTaskHours++;
                if (numberTaskHours == 24 && numberTaskMinutes != null && numberTaskMinutes != 0) {
                    numberTaskHours = 0;
                } else if (numberTaskHours >= 25) {
                    numberTaskHours = 0;
                }
                save();
            }
        }
    }

    public void toggleTotal15(View v) {
        if (!KindOfDay.isBeginEndType(kindOfDay)) {
            if (numberTaskMinutes != null) {
                if (numberTaskHours != null && numberTaskHours < 24) {
                    numberTaskMinutes += 15;
                    if (numberTaskMinutes >= 60) {
                        numberTaskMinutes = 0;
                    }
                }
                save();
            }
        }
    }

    private void viewToModel() {
        BeginEndTask task0 = (BeginEndTask) modifiableData.getTask(taskNo);
        if (task0 == null) {
            task0 = new BeginEndTask();
            modifiableData.addTask(task0);
        }

        task0.setKindOfDay(KindOfDay.fromString(kindOfDay));
        if (KindOfDay.isBeginEndType(task0.getKindOfDay())) {
            task0.setBegin(beginnTime);
            task0.setBegin15(beginn15);
            task0.setEnd(endeTime);
            task0.setEnd15(ende15);
            task0.setPause(pauseTime);
            task0.calcTotal();
        } else {
            task0.setBegin(null);
            task0.setBegin15(null);
            task0.setEnd(null);
            task0.setEnd15(null);
            task0.setPause(null);
            task0.setTotal(new Time15(numberTaskHours == null ? 0 : numberTaskHours, numberTaskMinutes == null ? 0 : numberTaskMinutes));
        }
    }

    private void modelToView() {
        if (modifiableData.getTask(taskNo) == null) {
            setBeginEndSelectionActivated(true);
            return;
        }
        BeginEndTask task = modifiableData.getTask(taskNo);
        boolean isLoadedData = originalData != null;
        int totalNewColor = isLoadedData ? ColorsUI.DARK_GREEN_SAVE_SUCCESS : ColorsUI.DARK_BLUE_DEFAULT;

        if (KindOfDay.isBeginEndType(task.getKindOfDay())) {
            setBeginEndSelectionActivated(true);
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
            setTransparent(R.id.totalSemi);
            setTransparent(R.id.total15);
        } else {

            setBeginEndSelectionActivated(false);
            numberTaskHours = task.getTotal().getHours();
            numberTaskMinutes = task.getTotal().getMinutes();

            setSelected(R.id.total);
            setSelected(R.id.totalSemi);
            setSelected(R.id.total15);
        }
        previousSelectionKindOfDays = kindOfDay;
        kindOfDay = task.getKindOfDay().toString();
        if (previousSelectionKindOfDays == null) {
            previousSelectionKindOfDays = kindOfDay;
        }
        aktualisiereTaskNo();
        aktualisiereTotal(totalNewColor);
        aktualisiereKindOfDay(totalNewColor);
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
        numberTaskMinutes = null;
        setTransparent(R.id.total);
        setTransparent(R.id.totalSemi);
        setTransparent(R.id.total15);
    }

    private void setTransparent(Integer viewId) {
        if (viewId != null) {
            TextView view = (TextView) findViewById(viewId);
            view.setBackgroundColor(ColorsUI.SELECTION_NONE_BG);
        }
    }

    private void setSelected(Integer viewId) {
        if (viewId != null) {
            TextView view = (TextView) findViewById(viewId);
            view.setBackgroundColor(ColorsUI.SELECTION_BG);
        }
    }

    private void setActivation(Integer viewId, boolean activated) {
        if (viewId != null) {
            TextView view = (TextView) findViewById(viewId);
            view.setTextColor(activated ? ColorsUI.ACTIVATED : ColorsUI.DEACTIVATED);
        }
    }
}