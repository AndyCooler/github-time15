package com.mythosapps.time15;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mythosapps.time15.storage.CloudBackup;

public class SettingsActivity extends AppCompatActivity {

    private CloudBackup cloudBackup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);


        //If you want to insert data in your settings
        MainSettingsFragment settingsFragment = new MainSettingsFragment();
        settingsFragment.setHasOptionsMenu(true);
        //settingsFragment. ...
        getSupportFragmentManager().beginTransaction().add(R.id.settingsFrame, settingsFragment).commit();

        cloudBackup = new CloudBackup();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cloudBackup.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_day) {
            startMainActivity("");
            return true;
        }

        if (id == R.id.action_check_cloud_available) {
            cloudBackup.requestAvailability(this, findViewById(R.id.settingsFrame));
        }

        return super.onOptionsItemSelected(item);
    }

    public void startMainActivity(String withId) {
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, withId);
        startActivity(intent);
    }
}
