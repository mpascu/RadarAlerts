package com.example.marc.radaralert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Observable;


public class MainActivity extends ActionBarActivity {

    private ObservableSwitchChangeListener listener;
    public static FragmentManager fragmentManager;
    private String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        listener = new ObservableSwitchChangeListener();
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SwipeTabsPagerAdapter(getSupportFragmentManager(), listener));
        regId = getRegistrationId(this);
        if (regId.equals("")) {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            boolean registered = data.getBooleanExtra("Registered", false);
            if (!registered) {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem toggleSwitch = menu.findItem(R.id.myswitch);
        Switch connectionSwitch = (Switch) toggleSwitch.getActionView().findViewById(R.id.connectionSwitch);
        connectionSwitch.setOnCheckedChangeListener(listener);
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
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SetPreferenceActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ObservableSwitchChangeListener extends Observable implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            setChanged();
            notifyObservers(isChecked);
        }
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(Globals.PREFS_PROPERTY_REG_ID, "");
        if (registrationId == null || registrationId.equals(""))
        {
            Log.i(Globals.TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(Globals.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = Globals.getAppVersion(context);
        if (registeredVersion != currentVersion)
        {
            Log.i(Globals.TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGcmPreferences(Context context) {
        return getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
    }

}
