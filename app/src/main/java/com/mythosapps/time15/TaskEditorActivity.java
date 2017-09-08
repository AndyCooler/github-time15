package com.mythosapps.time15;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mythosapps.time15.storage.ConfigStorageFacade;
import com.mythosapps.time15.storage.StorageFactory;
import com.mythosapps.time15.types.ColorsUI;
import com.mythosapps.time15.types.KindOfDay;
import com.mythosapps.time15.util.TimeUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * Activity für Tasks.
 * siehe https://www.opensourcealternative.org/simple-android-listview-without-listactivity/
 */
public class TaskEditorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // Navigation
    public final static String EXTRA_MESSAGE = "com.mythosapps.time15.MESSAGE";

    // Config storage
    private ConfigStorageFacade configStorage;

    // View state and view state management
    private ListView listView;
    private String[] listItems;
    private ListArrayAdapter<KindOfDay> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getName(), "onCreate() started.");
        setContentView(R.layout.activity_task_editor);

        configStorage = StorageFactory.getConfigStorage();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTaskEditor);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Log.i(getClass().getName(), "intent action : " + action);
        Log.i(getClass().getName(), "intent type   : " + type);

        listView = (ListView) findViewById(R.id.task_list);

        Log.i(getClass().getName(), "onCreate() finished.");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getClass().getName(), "onResume() started.");

        initialize();

        Log.i(getClass().getName(), "onResume() finished.");
    }

    private void initialize() {
        Log.i(getClass().getName(), "initialize() ");
        setTitle("Task Editor ");

        List<KindOfDay> list = configStorage.loadConfigXml(this);

        //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);

        Comparator<KindOfDay> comparator = new Comparator<KindOfDay>() {
            @Override
            public int compare(KindOfDay lhs, KindOfDay rhs) {
                return lhs.getDisplayString().compareTo(rhs.getDisplayString());
            }
        };

        adapter = new ListArrayAdapter(this, R.layout.list_item, R.id.task_name, list, comparator);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        Log.i(getClass().getName(), "initialize() finished.");
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
        if (id == R.id.action_month) {
            startMonthOverviewActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startMonthOverviewActivity() {
        Intent intent = new Intent(this, MonthOverviewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, TimeUtils.createID());
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(getClass().getName(), "editKindOfDay() started: position");

        // TODO new task
        // TODO delete task

        // Modify existing task
        KindOfDay task = (KindOfDay) adapter.getItem(position);
        final String taskName = task.getDisplayString();
        Log.i(getClass().getName(), taskName + " : " + task.getDisplayString());

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aufgabe anpassen");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final TextView taskNameView = new TextView(this);
        taskNameView.setText(taskName);

        // TODO Reichweite auswählbar: x Same month? x Same Year?
        // TODO Anzeige der Tasks immer nur für aktuellen Monat, blätterbar, aber:
        // TODO Idee: Popup "bestehende Einträge anpassen" mit Liste der Monate, in denen der alte Eintrag auch vorkommt anhakbar
        // TODO --> wird ein Monat nicht angehakt, gibt es weiterhin beide Tasks!
        // TODO Idee: Aktionen umbenennen soll undoable sein

        // TODO Idee: soll isBeginEndType tatsächlich editierbar sein? Konsequenz: von-bis Uhrzeiten entfallen! Popup-Warnung!
//        CheckBox beginEndCheckbox = new CheckBox(this);
//        beginEndCheckbox.setText("Mit Von-Bis Uhrzeit");
//        beginEndCheckbox.setChecked(task.isBeginEndType());

        RadioButtonItem blue = new RadioButtonItem("Blau", ColorsUI.DARK_BLUE_DEFAULT);
        RadioButtonItem green = new RadioButtonItem("Grün", ColorsUI.DARK_GREEN_SAVE_SUCCESS);
        RadioButtonItem gray = new RadioButtonItem("Grau", ColorsUI.DARK_GREY_SAVE_ERROR);
        RadioButtonUI colorChoiceUI = new RadioButtonUI(this, Arrays.asList(new RadioButtonItem[]{blue, green, gray}), task.getColor());


        TextView colorLabel = new TextView(this);
        colorLabel.setText("Farbe in Monatsansicht:");

        linearLayout.addView(taskNameView);
        //linearLayout.addView(beginEndCheckbox);
        linearLayout.addView(colorLabel);
        linearLayout.addView(colorChoiceUI.getRadioLayout());

        builder.setView(linearLayout);
        builder.setPositiveButton("Anpassen", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // update task
                //task.setBeginEndType(beginEndCheckbox.isChecked());
                task.setColor((int) colorChoiceUI.getSelectedValue());
                KindOfDay.replaceTaskType(task);
                KindOfDay.saveToExternalConfig(configStorage, TaskEditorActivity.this);
                adapter.clear();
                adapter.addAllListItems(configStorage.loadConfigXml(TaskEditorActivity.this));
                //adapter.notifyDataSetChanged();
                // if update dont work, check where your data comes from!!
            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.show();

        Log.i(getClass().getName(), "editKindOfDay() finished.");
    }

}
