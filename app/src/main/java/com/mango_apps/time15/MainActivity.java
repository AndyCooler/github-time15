package com.mango_apps.time15;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mango_apps.time15.storage.PrefStorage;
import com.mango_apps.time15.util.TimeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private Integer beginnTime = null;
    private Integer endeTime = null;
    private Integer pauseTime = null;
    private Integer previousSelectionBeginnTime = null;
    private Integer previousSelectionEndeTime = null;
    private Integer previousSelectionPauseTime = null;
    private Integer beginn15 = null;
    private Integer ende15 = null;
    private Integer previousSelectionBeginn15 = null;
    private Integer previousSelectionEnde15 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefStorage storage = new PrefStorage();

        String date = TimeUtils.createID();

        setTitle(date);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void verarbeiteKlick(View v) {

        int selectionBg = Color.rgb(173, 216, 230);
        TextView view = (TextView) v;
        int viewId = view.getId();
        boolean isBeginnTime = viewId == R.id.beginnA || viewId == R.id.beginnB || viewId == R.id.beginnC || viewId == R.id.beginnD;
        boolean isEndeTime = viewId == R.id.endeA|| viewId == R.id.endeB || viewId == R.id.endeC|| viewId == R.id.endeD;
        boolean isBeginn15 = viewId == R.id.beginn00 || viewId == R.id.beginn15 || viewId == R.id.beginn30 || viewId == R.id.beginn45;
        boolean isEnde15 = viewId == R.id.ende00|| viewId == R.id.ende15 || viewId == R.id.ende30|| viewId == R.id.ende45;
        boolean isPauseTime = viewId == R.id.pauseA|| viewId == R.id.pauseB || viewId == R.id.pauseC|| viewId == R.id.pauseD;
        if (isBeginnTime) {
            setPreviousSelectionTransparent(previousSelectionBeginnTime);
            view.setBackgroundColor(selectionBg);
            beginnTime = Integer.valueOf((String) view.getText());
            previousSelectionBeginnTime = viewId;
        }
        if (isEndeTime) {
            setPreviousSelectionTransparent(previousSelectionEndeTime);
            view.setBackgroundColor(selectionBg);
            endeTime = Integer.valueOf((String) view.getText());
            previousSelectionEndeTime = viewId;
        }
        if (isBeginn15) {
            setPreviousSelectionTransparent(previousSelectionBeginn15);
            view.setBackgroundColor(selectionBg);
            beginn15 = Integer.valueOf((String) view.getText());
            previousSelectionBeginn15 = viewId;
        }
        if (isEnde15) {
            setPreviousSelectionTransparent(previousSelectionEnde15);
            view.setBackgroundColor(selectionBg);
            ende15 = Integer.valueOf((String) view.getText());
            previousSelectionEnde15 = viewId;
        }
        if (isPauseTime) {
            setPreviousSelectionTransparent(previousSelectionPauseTime);
            view.setBackgroundColor(selectionBg);
            pauseTime = Integer.valueOf((String) view.getText());
            previousSelectionPauseTime = viewId;
        }

        TextView total = (TextView) findViewById(R.id.total);
        int difference = 0;
        int difference15 = 0;
        if (endeTime != null && beginnTime != null) {
            difference = endeTime - beginnTime;
            if (beginn15 != null && ende15 != null) {
                difference15 = ende15 - beginn15;
                if (difference15 < 0) {
                    difference--;
                    difference15 = 60 + difference15;
                }
            }
            if (pauseTime != null) {
                while (pauseTime > 60) {
                    difference--;
                    pauseTime -= 60;
                }
                difference15 -= pauseTime;
                if (difference15 < 0) {
                    difference--;
                    difference15 = 60 + difference15;
                }
            }
        }

        total.setText(zweiZiffern(difference) + ":" + zweiZiffern(difference15));
        total.setTextColor(Color.rgb(30,144,255));
    }

    private String zweiZiffern(int difference) {
        String result = String.valueOf(difference);
        return result.length() < 2 ? "0" + result : result;
    }

    private void setPreviousSelectionTransparent(Integer previousSelectionId) {
        if (previousSelectionId != null) {
            TextView previousView = (TextView) findViewById(previousSelectionId);
            previousView.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
