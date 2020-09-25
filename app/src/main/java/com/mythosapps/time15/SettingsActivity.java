package com.mythosapps.time15;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

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

    }
}
