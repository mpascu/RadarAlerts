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
    private static Registration regService = null;
    AtomicInteger msgId = new AtomicInteger();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // GCM startup
        gcm = GoogleCloudMessaging.getInstance(this);
        context = getApplicationContext();
        Globals.regid = getRegistrationId(context);
        if (Globals.regid==null || Globals.regid==""){
            registerInBackground();
        }
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        listener = new ObservableSwitchChangeListener();
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SwipeTabsPagerAdapter(getSupportFragmentManager(), listener));
        Globals.instance.getAlertsFromBackend();
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

    private String getRegistrationId(Context context)
    {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(Globals.PREFS_PROPERTY_REG_ID, "");
        if (registrationId == null || registrationId.equals(""))
        {
            Log.i(Globals.TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Globals.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion)
        {
            Log.i(Globals.TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    private void storeRegistrationId(Context context, String regId)
    {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(Globals.TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Globals.PREFS_PROPERTY_REG_ID, regId);
        editor.putInt(Globals.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    private void registerInBackground()
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params) {
                if (regService == null) {
                    Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                            .setRootUrl("https://crafty-shelter-88814.appspot.com/_ah/api/");

                    regService = builder.build();
                }

                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    String regId = gcm.register(Globals.GCM_SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    regService.register(regId).execute();
                    storeRegistrationId(getApplicationContext(),regId);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    msg = "Error: " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
            }

        }.execute();
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context)
    {
        // This sample app persists the registration ID in shared preferences,
        // but how you store the regID in your app is up to you.
        return getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
    }
}
