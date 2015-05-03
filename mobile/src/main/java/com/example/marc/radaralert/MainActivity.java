package com.example.marc.radaralert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marc.myapplication.backend.registration.Registration;
import com.example.marc.myapplication.backend.submitAlert.SubmitAlert;
import com.example.marc.myapplication.backend.submitAlert.model.AlertRecord;
import com.example.marc.myapplication.backend.submitAlert.model.CollectionResponseAlertRecord;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends ActionBarActivity {
    private ObservableSwitchChangeListener listener;
    public static FragmentManager fragmentManager;
    GoogleCloudMessaging gcm;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        listener = new ObservableSwitchChangeListener();
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SwipeTabsPagerAdapter(getSupportFragmentManager(), context, listener));
        Globals.instance.getAlertsFromBackend(context);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem toggleSwitch = menu.findItem(R.id.myswitch);
        Switch connectionSwitch = (Switch) toggleSwitch.getActionView().findViewById(R.id.connectionSwitch);
        connectionSwitch.setChecked(Globals.notificationsActivated);
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
            Globals.notificationsActivated=isChecked;
        }
    }
}
