package com.mythosapps.time15;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.mythosapps.time15.storage.CloudBackup;

public class SettingsActivity extends AppCompatActivity {

    private CloudBackup cloudBackup;

    private MainSettingsFragment settingsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_settings);

        View statusBarBackground = findViewById(R.id.status_bar_background_settings);
        MaterialToolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView().getRootView(), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Set height of the fake status bar background
            ViewGroup.LayoutParams statusBarParams = statusBarBackground.getLayoutParams();
            statusBarParams.height = insets.top;
            statusBarBackground.setLayoutParams(statusBarParams);
            FrameLayout mainContent = findViewById(R.id.settingsFrame);
            mainContent.setPadding(insets.left, mainContent.getPaddingTop(), insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        //If you want to insert data in your settings
        settingsFragment = new MainSettingsFragment();
        settingsFragment.setHasOptionsMenu(true);
        //settingsFragment. ...
        getSupportFragmentManager().beginTransaction().add(R.id.settingsFrame, settingsFragment).commit();

        cloudBackup = new CloudBackup(null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cloudBackup.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("time15", "Instance : " + this.toString());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
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
            SharedPreferences sharedPreferences = settingsFragment.getPreferenceManager().getSharedPreferences();
            String cloudBackupId = sharedPreferences.getString("settings_cloud_backup_id", "none");
            cloudBackup.requestAvailability(this, findViewById(R.id.settingsFrame), cloudBackupId);
        }

        if (id == R.id.action_check_cloud_restore) {
            SharedPreferences sharedPreferences = settingsFragment.getPreferenceManager().getSharedPreferences();
            String cloudBackupId = sharedPreferences.getString("settings_cloud_backup_id", "none");
            cloudBackup.requestRestore(this, findViewById(R.id.settingsFrame), cloudBackupId);
        }

        return super.onOptionsItemSelected(item);
    }

    public void startMainActivity(String withId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public CloudBackup getCloudBackup() {
        return cloudBackup;
    }
}
