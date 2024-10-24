package com.mythosapps.time15;

import static com.mythosapps.time15.storage.FileStorage.STORAGE_DIR;
import static com.mythosapps.time15.types.KindOfDay.DEFAULT_DUE_TIME_PER_DAY_IN_MINUTES;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.mythosapps.time15.storage.CloudBackup;
import com.mythosapps.time15.storage.ConfigStorageFacade;
import com.mythosapps.time15.storage.StorageFacade;
import com.mythosapps.time15.storage.StorageFactory;
import com.mythosapps.time15.types.BalanceType;
import com.mythosapps.time15.types.BeginEndTask;
import com.mythosapps.time15.types.ColorsUI;
import com.mythosapps.time15.types.DaysDataNew;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.types.ScrollViewType;
import com.mythosapps.time15.types.Time15;
import com.mythosapps.time15.util.AppVersion;
import com.mythosapps.time15.util.EmailUtils;
import com.mythosapps.time15.util.TimeUtils;
import com.mythosapps.time15.util.ZipUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Main activity lets the user choose the time they start working and the time they stop working.
 * They can also choose the kind of day: work day, vacation, holiday etc.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
// before material design toolbar: was extends AppCompatActivity

    // Navigation
    public final static String EXTRA_MESSAGE = "com.mythosapps.time15.MESSAGE";
    private static final String UNLOCK_SYMBOL = new String(Character.toChars(128275));
    private static final String SMILEY_SIGN = new String(Character.toChars(128521));
    private static final long TIMER_DELAY = 3000;

    // Storage
    private StorageFacade storage;
    private ConfigStorageFacade configStorage;
    private CloudBackup cloudBackup;

    // Balance Type
    public BalanceType balanceType = BalanceType.TOTAL_WORK;
    // U+00F8
    private static final int AV_CHAR = 0x00F8;
    public static final String AVERAGE_SIGN = Character.toString((char) AV_CHAR);

    private ReminderAction reminderAction;

    // View state and view state management
    private String id = null;
    private int taskNo = 0;
    private Integer beginnTime = null; //value
    private Integer endeTime = null; //value
    private Integer pauseTime = null; //value
    private Integer beginn15 = null; //value
    private Integer ende15 = null; //value
    private String note = null; // value
    private boolean homeOffice = false; // value
    private String kindOfDay = KindOfDay.WORKDAY.toString();
    private String kindOfDayEdited = null;
    private Integer previousSelectionBeginnTime = null; //viewId
    private Integer previousSelectionEndeTime = null; //viewId
    private Integer previousSelectionPauseTime = null; //viewId
    private Integer previousSelectionBeginn15 = null; //viewId
    private Integer previousSelectionEnde15 = null; //viewId
    private String previousSelectionKindOfDays = null;
    private boolean hasToggledHomeOffice = false;
    private boolean isPaused = false;
    private boolean isCreateComplete = false;
    // here are the maps from value to viewId that enable re-use of 4 TextViews for full range 0-24
    // TODO for ScrollView, just extend initialization to full range of values
    // TODO later: maps can be avoided by makeing better use of TextView (has ID, has value, so
    // no need for mappings as we now use the full range)

    // TODO ScrollView#requestChildFocus(View) scroll bis ein child View sichtbar wird,
    // TODO oder #scrollTo #smoothScrollTo mit int Y. Param Y fuer scrollTO ist getTop() oder getBottom() von TextView
    private HashMap<Integer, TextView> mapBeginValueToView = new HashMap<>();
    private HashMap<Integer, TextView> mapBegin15ValueToView = new HashMap<>();
    private HashMap<Integer, TextView> mapEndValueToView = new HashMap<>();
    private HashMap<Integer, TextView> mapEnd15ValueToView = new HashMap<>();
    private HashMap<Integer, Integer> mapPauseValueToViewId = new HashMap<Integer, Integer>();
    private int balanceValue;
    private DaysDataNew originalData;
    private DaysDataNew modifiableData;
    private Integer numberTaskHours = null;
    private Integer numberTaskMinutes = null;
    private TextView total;
    private TextView total15;
    private ScrollView scrollViewBegin;
    private ScrollView scrollViewBegin15;
    private ScrollView scrollViewEnd;
    private ScrollView scrollViewEnd15;
    private Switch onOffSwitch;
    private SharedPreferences sharedPreferences;

    private View.OnClickListener scrollUIListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            verarbeiteKlick(v);
        }
    };
    private boolean isEditable; // UI is editable on today's date or after unlocking
    private boolean isUnLockButtonPressed; // pressing unlock makes UI editable
    private Menu menu;
    private Timer timer = new java.util.Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCreateComplete = false;
        storage = StorageFactory.getDataStorage();
        configStorage = StorageFactory.getConfigStorage();
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initMapWithIds(mapPauseValueToViewId, R.id.pauseA, R.id.pauseB, R.id.pauseC, R.id.pauseD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            KindOfDay.initializeFromConfig(configStorage, this);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                KindOfDay.initializeFromConfig(configStorage, this);
            }
        }

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        List<String> list = KindOfDay.dataList();
        if (list.isEmpty()) {
            list.add(KindOfDay.DEFAULT_WORK);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, list);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        int position = dataAdapter.getPosition(kindOfDay);
        spinner.setSelection(position);

        onOffSwitch = (Switch) findViewById(R.id.home_office_switch);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                homeOffice = isChecked;
                hasToggledHomeOffice = true;
            }
        });

        //kindOfDayView.setOnClickListener(v -> toggleKindOfDay(v));

        scrollViewBegin = (ScrollView) findViewById(R.id.scrollBegin);
        ScrollViewUI.populateHoursUI(scrollUIListener, this, scrollViewBegin, mapBeginValueToView, 1000, 8, ScrollViewType.BEGIN);

        scrollViewBegin15 = (ScrollView) findViewById(R.id.scrollBegin15);
        ScrollViewUI.populateFifteensUI(scrollUIListener, this, scrollViewBegin15, mapBegin15ValueToView, 2000);

        scrollViewEnd = (ScrollView) findViewById(R.id.scrollEnd);
        ScrollViewUI.populateHoursUI(scrollUIListener, this, scrollViewEnd, mapEndValueToView, 3000, 16, ScrollViewType.END);

        scrollViewEnd15 = (ScrollView) findViewById(R.id.scrollEnd15);
        ScrollViewUI.populateFifteensUI(scrollUIListener, this, scrollViewEnd15, mapEnd15ValueToView, 4000);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        reminderAction = new ReminderAction(this);
        // Look some days back and check if user forgot to enter their times
        if (sharedPreferences.getBoolean("setting_reminder_popup_active", true)) {

            List<String> missingEntries = reminderAction.remindOfLastWeeksEntries();
            if (!missingEntries.isEmpty()) {
                Snackbar.make(findViewById(R.id.total), "Erinnerung: Für " + missingEntries.get(0) + " noch eintragen " + SMILEY_SIGN,
                        Snackbar.LENGTH_LONG).show();
            }
        }

        String cloudBackupId = sharedPreferences.getString("settings_cloud_backup_id", "none");
        if ("none".equals(cloudBackupId)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("settings_cloud_backup_id", AppVersion.generateUniqueId());
            editor.commit();
        }

        cloudBackup = new CloudBackup(sharedPreferences.getBoolean("settings_cloud_backup", false),
                sharedPreferences.getInt("settings_cloud_backup_frequency", 0));
        cloudBackup.requestAvailability(this, null, cloudBackupId);
        // can: use ProGuard to obfuscate the code
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cloudBackup.disconnect();
    }

    // only for UI: sets view's text color to black when active, gray when inactive
    private void setBeginEndSelectionActivated(boolean activated) {
        for (TextView view : mapBeginValueToView.values()) {
            setActivation(view.getId(), activated);
        }
        for (TextView view : mapEndValueToView.values()) {
            setActivation(view.getId(), activated);
        }
        for (TextView view : mapBegin15ValueToView.values()) {
            setActivation(view.getId(), activated);
        }
        for (TextView view : mapEnd15ValueToView.values()) {
            setActivation(view.getId(), activated);
        }
        for (Integer viewId : mapPauseValueToViewId.values()) {
            setActivation(viewId, activated);
        }
    }

    private String getIntentsId() {
        Intent intent = getIntent();
        if (intent != null) {
            String withId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
            if (withId != null) {
                return withId;
            }
        }
        return null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                KindOfDay.initializeFromConfig(configStorage, this);
                onResume();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("time15", "Instance : " + this.toString() + " : isPaused: " + isPaused);
        String intentsId = getIntentsId();
        if (intentsId == null) {
            intentsId = id == null ? TimeUtils.createID() : id;
        }
        isPaused = false;
        String balanceTypeSetting = sharedPreferences.getString("settings_balance_type", "TOTAL_WORK");
        balanceType = BalanceType.valueOf(balanceTypeSetting);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            switchToID(null, intentsId, null);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                balanceValue = storage.loadBalance(this, intentsId, balanceType);
                switchToID(null, intentsId, null);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Provide an additional rationale to the user if the permission was not granted
                    // and the user would benefit from additional context for the use of the permission.
                    // Display a SnackBar with cda button to request the missing permission.
                    Snackbar.make(findViewById(R.id.total), "App liest und schreibt .csv Dateien",
                            Snackbar.LENGTH_INDEFINITE).setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request the permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    123);
                        }
                    }).show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123);
                }
            }
        }
        if (TimeUtils.isLastWorkDayOfMonth(TimeUtils.createID())) {
            Snackbar.make(findViewById(R.id.total), "Tipp: Für ein Backup, nutze Email Backup im Menü oder aktiviere Cloud Backup in den Settings",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void updateCloudMenuItem() {
        boolean cloudBackupActivated = sharedPreferences.getBoolean("settings_cloud_backup", false);
        if (cloudBackupActivated) {
            if (menu != null) {
                Boolean cloudAvailable = cloudBackup.isAvailable();
                if (null == cloudAvailable || Boolean.FALSE.equals(cloudAvailable)) {
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_cloud_off_24));
                } else {
                    Boolean cloudBackupSuccess = cloudBackup.isBackupSuccess();
                    if (Boolean.FALSE.equals(cloudBackupSuccess)) {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_cloud_queue_24));
                    } else if (Boolean.TRUE.equals(cloudBackupSuccess)) {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_cloud_done_24));
                    } else {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_cloud_24));
                    }
                }
            }
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_cloud_off_24));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        if (id == R.id.action_about) {
            String about = "Time15 von Andreas, \n";
            about += "Version: " + AppVersion.getVersionName(this) + "\n";
            about += "Build-ID:" + AppVersion.getVersionCode(this) + "\n";
            about += "Code: Andreas. Viele viele Tests: Julian" + "\n";

            Toast.makeText(getApplicationContext(), about, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_new) {
            menuNewTask();
            return true;
        }
        if (id == R.id.action_edit) {
            menuEditTask();
            return true;
        }
        if (id == R.id.action_note) {
            menuEditNote();
            return true;
        }
        if (id == R.id.action_delete) {
            deleteTask();
            return true;
        }
        if (id == R.id.action_send) {
            performEmailBackup();
            return true;
        }
        if (id == R.id.action_settings) {
            if (isStateChangedByUser()) {
                save(false);
            }
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_cloud_backup) {
            boolean cloudBackupActivated = sharedPreferences.getBoolean("settings_cloud_backup", false);
            if (cloudBackupActivated) {
                if (menu != null) {
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_cloud_queue_24));
                }
                String cloudBackupId = sharedPreferences.getString("settings_cloud_backup_id", "none");
                cloudBackup.requestBackup(findViewById(R.id.addTaskButton), cloudBackupId);
                timer.schedule(new UpdateCloudMenuItemTask(), TIMER_DELAY);
            } else {
                String status = "Cloud Backup (experimentell)\n";
                status += "Selbst Einschalten erforderlich\n";
                status += "im Menü unter Settings";
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (id == R.id.action_cloud_status) {
            boolean cloudBackupActivated = sharedPreferences.getBoolean("settings_cloud_backup", false);
            if (cloudBackupActivated) {
                String cloudBackupId = sharedPreferences.getString("settings_cloud_backup_id", "none");
                cloudBackup.requestAvailability(this, findViewById(R.id.addTaskButton), cloudBackupId);
                timer.schedule(new UpdateCloudMenuItemTask(), TIMER_DELAY);
            } else {
                String status = "Cloud Backup (experimentell)\n";
                status += "Selbst Einschalten erforderlich\n";
                status += "im Menü unter Settings";
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
            }
            updateCloudMenuItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void unlock() {
        //change button
        Button addTaskButton = (Button) findViewById(R.id.addTaskButton);
        addTaskButton.setText("+"); // add task symbol +
        addTaskButton.setBackground(getResources().getDrawable(R.drawable.roundbutton));

        // reinitialize day with id this.id
        isUnLockButtonPressed = true;
        switchToID(null, this.id, taskNo);
        isUnLockButtonPressed = false;
    }

    private void lock() {
        //change button
        Button addTaskButton = (Button) findViewById(R.id.addTaskButton);
        addTaskButton.setText(UNLOCK_SYMBOL); // open lock symbol
        addTaskButton.setBackground(getResources().getDrawable(R.drawable.roundbutton_unlock));

        // reinitialize day with id this.id
        isUnLockButtonPressed = false;
    }

    private void deleteTask() {
        if (isEditable) {
            if (modifiableData == null || modifiableData.getNumberOfTasks() == 0) {
                Toast.makeText(MainActivity.this.getApplicationContext(), R.string.main_delete_error, Toast.LENGTH_SHORT).show();
            } else {
                originalData = null;
                modifiableData.deleteTask(modifiableData.getTask(taskNo));
                if (modifiableData.getNumberOfTasks() == 0) {
                    modifiableData = new DaysDataNew(id);
                }
                taskNo = 0;
                resetView();
                modelToView();
                save(true);
            }
        } else {
            Toast.makeText(MainActivity.this.getApplicationContext(), R.string.main_delete_unlock_first, Toast.LENGTH_SHORT).show();
        }
    }

    // Menu: Email backup
    private void performEmailBackup() {
        final SendEmailPopupUI sendEmailPopupUI = new SendEmailPopupUI(this);

        sendEmailPopupUI.setOkButton(getString(R.string.send_email_button), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sendToAddress = sendEmailPopupUI.getInputTextField().getText().toString();
                if (sendToAddress == null || sendToAddress.isEmpty() || !sendToAddress.contains("@") || !sendToAddress.contains(".")) {
                    Toast.makeText(MainActivity.this.getApplicationContext(), R.string.send_email_correction, Toast.LENGTH_LONG).show();
                } else {

                    File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS) + File.separator + STORAGE_DIR);
                    String[] allFiles = storageDir.list(ZipUtils.EXPORT_FILE_FILTER);
                    String backupMoment = TimeUtils.createMoment();
                    String zipArchiveFilename = "Time15_Backup_" + backupMoment + ".time15";
                    try {
                        ZipUtils.createZipFile(storageDir, zipArchiveFilename, allFiles);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this.getApplicationContext(), R.string.send_email_zip_problem, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        return;
                    }

                    String subject = "Time15 Export " + backupMoment;
                    EmailUtils.sendEmail(MainActivity.this, zipArchiveFilename, storageDir, subject, sendToAddress);
                }
            }
        });
        sendEmailPopupUI.setCancelButton(getString(R.string.edit_task_cancel));
        sendEmailPopupUI.show();
    }

    public void startMonthOverviewActivity(View v) {
        if (isStateChangedByUser()) {
            save(false);
        }
        Intent intent = new Intent(this, MonthOverviewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void startYearOverviewActivity(View view) {
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

        String balanceText = "";
        switch (balanceType) {
            case AVERAGE_WORK: {
                balanceText = Time15.fromMinutes(balanceValue).toDecimalForDisplayOfAverage();
                balanceText = "(" + AVERAGE_SIGN + " " + balanceText + ")";
                break;
            }
            case BALANCE: {
                balanceText = Time15.fromMinutes(balanceValue).toDecimalForDisplay();
                balanceText = "(" + balanceText + ")";
                break;
            }
            case TOTAL_WORK: {
                if (modifiableData != null && modifiableData.getNumberOfTasks() > 1) {
                    balanceText = Time15.fromMinutes(balanceValue).toDecimalForDisplay();
                    balanceText = "(" + getString(R.string.display_sum) + " " + balanceText + ")";
                }
                break;
            }
            default: {
                break;
            }
        }
        balance.setText(balanceText);
    }


    private void switchToID(String fromId, String toId, Integer taskNumber) {
        id = toId;
        setTitle(TimeUtils.getMainTitleString(id));
        isEditable = TimeUtils.isOkayToEdit(id) || isUnLockButtonPressed;
        if (isEditable) {
            if (!isUnLockButtonPressed) {
                unlock();
            }
        } else {
            lock();
        }
        originalData = storage.loadDaysDataNew(this, id);
        modifiableData = DaysDataNew.copy(originalData);
        if (modifiableData == null) {
            modifiableData = new DaysDataNew(id);
            BeginEndTask task0 = new BeginEndTask();
            task0.setKindOfDay(KindOfDay.WORKDAY);
            modifiableData.addTask(task0);
        }
        if (taskNumber != null) {
            taskNo = taskNumber;
        } else {
            taskNo = 0;
        }
        resetView();
        balanceValue = storage.loadBalance(this, id, balanceType);
        updateBalance();
        modelToView();
        isCreateComplete = true;

        timer.schedule(new UpdateCloudMenuItemTask(), TIMER_DELAY);
    }

    public void dateForwards() {
        saveKindOfDay();
        switchToID(id, TimeUtils.dateForwards(id), null);
    }

    public void dateBackwards() {
        saveKindOfDay();
        switchToID(id, TimeUtils.dateBackwards(id), null);
    }

    public void dateToday() {
        saveKindOfDay();
        switchToID(id, TimeUtils.createID(), null);
    }

    // ensures that begin hour is visible
    public void beginAt(Integer hour) {
        if (hour != null && !ScrollViewUI.isBeginHourVisible(hour)) {
            ScrollViewUI.scrollToChild(scrollViewBegin, intoRange(hour), ScrollViewType.BEGIN);
        }
    }

    // ensures that end hour is visible
    public void endAt(Integer hour) {
        if (hour != null && !ScrollViewUI.isEndHourVisible(hour)) {
            ScrollViewUI.scrollToChild(scrollViewEnd, intoRange(hour), ScrollViewType.END);
        }
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

    private void saveKindOfDay() {
        if (isStateChangedByUser()) {
            save(false);
        }
    }

    private boolean isStateChangedByUser() {
        return !previousSelectionKindOfDays.equals(kindOfDay) || hasToggledHomeOffice;
    }

    public void toggleKindOfDay(View v) {
        KindOfDay newKindOfDay = KindOfDay.toggle(kindOfDay);
        activateKindOfDay(newKindOfDay);
    }

    private void activateKindOfDay(KindOfDay newKindOfDay) {
        kindOfDay = newKindOfDay.getDisplayString();
        if (newKindOfDay.isBeginEndType()) {
            numberTaskHours = null;
            numberTaskMinutes = null;
        } else {
            beginnTime = null;
            beginn15 = null;
            endeTime = null;
            ende15 = null;
            pauseTime = null;
        }
        viewToModel();
        resetView();
        modelToView();
    }

    public void menuEditNote() {
        final EditNotePopupUI taskUI = new EditNotePopupUI(this, modifiableData.getTask(taskNo).getNote());

        taskUI.setOkButton(getString(R.string.edit_task_new), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNote = taskUI.getInputTextField().getText().toString();
                if (newNote == null || newNote.isEmpty()) {
                    MainActivity.this.note = null;
                } else {
                    MainActivity.this.note = newNote.replaceAll(",", ";");
                }

                MainActivity.this.save(false);
            }
        });
        taskUI.setCancelButton(getString(R.string.edit_task_cancel));
        taskUI.show();
    }

    public void menuNewTask() {
        final TaskPopupUI taskUI = new TaskPopupUI(this, getString(R.string.new_task_title), kindOfDay, true);

        taskUI.setOkButton(getString(R.string.edit_task_new), new DialogInterface.OnClickListener() {
            private String getTaskName() {
                String taskName = taskUI.getInputTextField().getText().toString();
                if (taskName != null) {
                    taskName = taskName.trim();
                }
                return taskName;
            }

            private void updateTask() {
                int colorChosen = taskUI.getInputRadioButtonGroup().getCheckedRadioButtonId();
                int taskColor = colorChosen == 0 ? ColorsUI.DARK_BLUE_DEFAULT : (colorChosen == 1 ? ColorsUI.DARK_GREEN_SAVE_SUCCESS : ColorsUI.DARK_GREY_SAVE_ERROR);

                KindOfDay.addTaskType(new KindOfDay(kindOfDayEdited, taskColor, taskUI.getCheckBox().isChecked()));
                KindOfDay.saveToExternalConfig(configStorage, MainActivity.this);

                activateKindOfDay(KindOfDay.fromString(kindOfDayEdited));
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                kindOfDayEdited = getTaskName();
                if (KindOfDay.fromString(kindOfDayEdited) != null || kindOfDay.equalsIgnoreCase(kindOfDayEdited)) {
                    Toast.makeText(MainActivity.this.getApplicationContext(), R.string.edit_task_error_task_exists, Toast.LENGTH_SHORT).show();
                    return;
                }
                updateTask();
            }
        });
        taskUI.setCancelButton(getString(R.string.edit_task_cancel));
        taskUI.show();
    }

    public void menuEditTask() {
        final TaskPopupUI taskUI = new TaskPopupUI(this, getString(R.string.edit_task_title), kindOfDay, false);

        taskUI.setOkButton(getString(R.string.edit_task_edit), new DialogInterface.OnClickListener() {
            private String getTaskName() {
                String taskName = taskUI.getInputTextField().getText().toString();
                if (taskName != null) {
                    taskName = taskName.trim();
                }
                return taskName;
            }

            private void updateTask() {
                int colorChosen = taskUI.getInputRadioButtonGroup().getCheckedRadioButtonId();
                int taskColor = colorChosen == 0 ? ColorsUI.DARK_BLUE_DEFAULT : (colorChosen == 1 ? ColorsUI.DARK_GREEN_SAVE_SUCCESS : ColorsUI.DARK_GREY_SAVE_ERROR);

                KindOfDay.replaceTaskType(new KindOfDay(kindOfDayEdited, taskColor, taskUI.getCheckBox().isChecked()));
                KindOfDay.saveToExternalConfig(configStorage, MainActivity.this);

                activateKindOfDay(KindOfDay.fromString(kindOfDayEdited));
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                kindOfDayEdited = getTaskName();
                updateTask();
            }
        });
        taskUI.setCancelButton(getString(R.string.edit_task_cancel));
        taskUI.show();
    }

    private void aktualisiereKindOfDay(int color) {
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        int i = 0;
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(kindOfDay);
        spinner.setSelection(position);

        spinner.setEnabled(isEditable);
        spinner.setClickable(isEditable);
        TextView view = (TextView) spinner.getChildAt(0);
        if (view != null) {
            view.setTextColor(isEditable ? ColorsUI.ACTIVATED : ColorsUI.DEACTIVATED);
        }
    }

    public void addTask(View v) {
        if (isEditable) {
            if (modifiableData.getNumberOfTasks() > 1) {
                Toast.makeText(MainActivity.this, R.string.tasks_per_day_limit, Toast.LENGTH_SHORT).show();
            } else {
                taskNo = 1;
                BeginEndTask task1 = new BeginEndTask();
                task1.setKindOfDay(KindOfDay.VACATION); // TODO Default Task
                task1.setTotal(Time15.fromMinutes(DEFAULT_DUE_TIME_PER_DAY_IN_MINUTES));
                modifiableData.addTask(task1);
                resetView();
                modelToView();
            }
        } else {
            unlock();
        }
    }

    public void switchTasks(View v) {
        if (modifiableData.getNumberOfTasks() == 2) {
            saveKindOfDay();
            resetView();
            taskNo = (taskNo + 1) % modifiableData.getNumberOfTasks();
            modelToView();
        } else {
            Toast.makeText(MainActivity.this.getApplicationContext(), R.string.task_add_by_pressing_plus, Toast.LENGTH_SHORT).show();
        }
    }

    public void aktualisiereTaskNo() {
        Button switchTasksButton = (Button) findViewById(R.id.switchTasksButton);
        switchTasksButton.setText(String.valueOf(taskNo + 1));
        setColorOfButton(switchTasksButton, modifiableData.getNumberOfTasks() > 1 ? ColorsUI.SELECTION_BG_BUTTON : ColorsUI.LIGHT_GREY_BUTTON);
    }

    private void setColorOfButton(Button button, int color) {
        Drawable background = button.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(color);
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(color);
        }
    }

    /**
     * Click in dialog area for begin-end task.
     *
     * @param v
     */
    public void verarbeiteKlick(View v) {

        if (isEditable) {

            if (KindOfDay.isBeginEndType(kindOfDay)) {

                TextView view = (TextView) v;
                int viewId = view.getId();
                boolean isBeginnTime = mapBeginValueToView.containsValue(view);
                boolean isEndeTime = mapEndValueToView.containsValue(view);
                boolean isBeginn15 = mapBegin15ValueToView.containsValue(view);
                boolean isEnde15 = mapEnd15ValueToView.containsValue(view);
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
                        setSelected(viewId);
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
                        setSelected(viewId);
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
                        setSelected(viewId);
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
                        setSelected(viewId);
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
                        setSelected(viewId);
                        pauseTime = Integer.valueOf((String) view.getText());
                        previousSelectionPauseTime = viewId;
                        isSelected = true;
                    }
                }

                if (isSelected || isDeselected) {
                    save(false);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.main_delete_unlock_first, Toast.LENGTH_SHORT).show();
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
        total15 = (TextView) findViewById(R.id.total15);

        total.setText(totalTime.getHoursDisplayString());
        total.setTextColor(color);
        total15.setText(totalTime.getMinutesDisplayString());
        total15.setTextColor(color);
    }

    public void save(boolean isAfterDeleteTask) {
        if (!isAfterDeleteTask) {
            viewToModel();
        }

        // save only if user changed something
        if (originalData == null) {
            if (modifiableData.isInInitialState()) {
                if (isAfterDeleteTask) {
                } else {
                    return;
                }
            }
        } else {
            if (originalData.equals(modifiableData)) {
                return;
            }
        }

        int totalNewColor = 0;
        if (storage.saveDaysDataNew(this, modifiableData)) {
            if (modifiableData.getNumberOfTasks() == 0) {
                totalNewColor = ColorsUI.DARK_BLUE_DEFAULT;
            } else {
                totalNewColor = KindOfDay.fromString(kindOfDay).isBeginEndType() ? ColorsUI.DARK_GREEN_SAVE_SUCCESS : ColorsUI.LIGHT_GREEN_SAVE_SUCCESS;
            }
            balanceValue = storage.loadBalance(this, id, balanceType);
        } else {
            totalNewColor = KindOfDay.fromString(kindOfDay).isBeginEndType() ? ColorsUI.DARK_GREY_SAVE_ERROR : ColorsUI.LIGHT_GREY_SAVE_ERROR;
        }

        aktualisiereTotal(totalNewColor);
        aktualisiereKindOfDay(totalNewColor);
        onOffSwitch.setEnabled(isEditable);
        onOffSwitch.setClickable(isEditable);
        originalData = modifiableData;
        modifiableData = DaysDataNew.copy(originalData);
        //modelToView(); // can: shouldn't be necessary
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
                save(false);
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
                save(false);
            }
        }
    }

    private void viewToModel() {
        BeginEndTask task0 = modifiableData.getTask(taskNo);

        if (task0 == null) {
            task0 = new BeginEndTask();
            modifiableData.addTask(task0);
        }

        task0.setKindOfDay(KindOfDay.fromString(kindOfDay));
        if (task0.getKindOfDay() == null) {
            // wenn irgendwas mit dem laden der configuration nicht gklappt hat z.b.
            // NPE hier schon gesehen, wenn es nicht gesetzt war das wird wohl der grund sein
            task0.setKindOfDay(KindOfDay.WORKDAY);
        }
        task0.setNote(note);
        if (task0.getKindOfDay().isBeginEndType()) {
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
        if (hasToggledHomeOffice) {
            modifiableData.setHomeOffice(homeOffice);
        }
    }

    private void modelToView() {
        if (modifiableData.getTask(taskNo) == null) {
            setBeginEndSelectionActivated(true); // seems like the right place to initialize kindOfDay for task with taskNo here
            return;
        }
        BeginEndTask task = modifiableData.getTask(taskNo);
        boolean isLoadedData = originalData != null;

        if (task.getKindOfDay().isBeginEndType()) {
            setBeginEndSelectionActivated(true);
            BeginEndTask task0 = task;
            beginnTime = task0.getBegin();
            beginn15 = task0.getBegin15();
            endeTime = task0.getEnd();
            ende15 = task0.getEnd15();
            pauseTime = task0.getPause();

            beginAt(beginnTime);
            endAt(endeTime);

            previousSelectionPauseTime = mapPauseValueToViewId.get(pauseTime);
            previousSelectionEnde15 = mapEnd15ValueToView.get(ende15) == null ? null : mapEnd15ValueToView.get(ende15).getId();
            previousSelectionEndeTime = mapEndValueToView.get(endeTime) == null ? null : mapEndValueToView.get(endeTime).getId();
            previousSelectionBeginnTime = mapBeginValueToView.get(beginnTime) == null ? null : mapBeginValueToView.get(beginnTime).getId();
            previousSelectionBeginn15 = mapBegin15ValueToView.get(beginn15) == null ? null : mapBegin15ValueToView.get(beginn15).getId();

            setSelected(previousSelectionPauseTime);
            setSelected(previousSelectionEnde15);
            setSelected(previousSelectionEndeTime);
            setSelected(previousSelectionBeginnTime);
            setSelected(previousSelectionBeginn15);

            setTransparent(R.id.total);
            setTransparent(R.id.total15);
            int totalNewColor = isLoadedData ? ColorsUI.DARK_GREEN_SAVE_SUCCESS : ColorsUI.DARK_BLUE_DEFAULT;
            aktualisiereTotal(totalNewColor);
        } else {

            setBeginEndSelectionActivated(false);
            numberTaskHours = task.getTotal().getHours();
            numberTaskMinutes = task.getTotal().getMinutes();

            setSelected(R.id.total);
            setSelected(R.id.total15);
            int totalNewColor = isLoadedData ? ColorsUI.LIGHT_GREEN_SAVE_SUCCESS : Color.WHITE;
            aktualisiereTotal(totalNewColor);
        }
        previousSelectionKindOfDays = kindOfDay;
        kindOfDay = task.getKindOfDay().toString();
        note = task.getNote();
        if (previousSelectionKindOfDays == null) {
            previousSelectionKindOfDays = kindOfDay;
        }
        homeOffice = modifiableData.getHomeOffice();
        aktualisiereTaskNo();
        aktualisiereKindOfDay(0);
        onOffSwitch.setChecked(homeOffice);
        onOffSwitch.setEnabled(isEditable);
        onOffSwitch.setClickable(isEditable);
        hasToggledHomeOffice = false;
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
        kindOfDay = KindOfDay.WORKDAY.toString(); // TODO Default kind of day
        note = null;
        homeOffice = false;
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
        setTransparent(R.id.total15);
        onOffSwitch.setChecked(false);
        onOffSwitch.setEnabled(isEditable);
        onOffSwitch.setClickable(isEditable);
        hasToggledHomeOffice = false;
    }

    private void setTransparent(Integer viewId) {
        if (viewId != null) {
            TextView view = (TextView) findViewById(viewId);
            view.setBackgroundColor(Color.WHITE);
            view.setTextColor(Color.BLACK);
        }
    }

    private void setSelected(Integer viewId) {
        if (viewId != null) {
            TextView view = (TextView) findViewById(viewId);
            view.setBackgroundColor(isEditable ? ColorsUI.SELECTION_NONE_BG : ColorsUI.DEACTIVATED);
            view.setTextColor(Color.WHITE);
        }
    }

    private void setActivation(Integer viewId, boolean activated) {
        if (viewId != null) {
            TextView view = (TextView) findViewById(viewId);
            view.setTextColor(activated ? ColorsUI.ACTIVATED : ColorsUI.DEACTIVATED);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        if (isCreateComplete && item != null) {
            activateKindOfDay(KindOfDay.fromString(item));
        }
        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    class UpdateCloudMenuItemTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCloudMenuItem();
                }
            });
        }
    }
}
