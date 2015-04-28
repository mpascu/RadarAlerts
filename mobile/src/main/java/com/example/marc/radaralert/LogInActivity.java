package com.example.marc.radaralert;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class LogInActivity extends ActionBarActivity {

    GoogleCloudMessaging gcm;
    String regid;
    EditText usernameText;
    EditText passwordText;
    AtomicInteger msgId = new AtomicInteger();
    private Boolean registered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        gcm = GoogleCloudMessaging.getInstance(this);
        usernameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);
        registerInBackground();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
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

    public void register(View v) {
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    Bundle data = new Bundle();
                    data.putString("username", params[0]);
                    data.putString("password", params[1]);
                    data.putString("regID", params[2]);
                    data.putString("my_action",
                            "com.google.android.gcm.demo.app.LOGIN");
                    String id = Integer.toString(msgId.incrementAndGet());
                    gcm.send(Globals.GCM_SENDER_ID + "@gcm.googleapis.com", id, data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
        }.execute(usernameText.getText().toString(), passwordText.getText().toString(), regid);
    }

    public void logIn(View v) {

    }

    private void registerInBackground() {
        new AsyncTask<String, String, String>()
        {
            @Override
            protected String doInBackground(String... params)
            {
                String msg = "";
                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(Globals.GCM_SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over
                    // HTTP, so it can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we use upstream GCM messages to send the
                    // registration ID to the 3rd party server

                    // Persist the regID - no need to register again.
                    storeRegistrationId(getApplicationContext(), regid);
                }
                catch (IOException ex)
                {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

        }.execute();
    }

    private void sendRegistrationIdToBackend()
    {
        Log.d(Globals.TAG, "REGISTER USERID: " + regid);
        new AsyncTask<String, Void, String>()
        {
            @Override
            protected String doInBackground(String... params)
            {
                String msg = "";
                try
                {
                    Bundle data = new Bundle();
                    data.putString("action", "backend.eps.udl.hellobackendcomplet.gcmdemo.REGISTER");
                    String id = Integer.toString(msgId.incrementAndGet());
                    gcm.send(Globals.GCM_SENDER_ID + "@gcm.googleapis.com", id, Globals.GCM_TIME_TO_LIVE, data);
                    msg = "Sent registration";
                }
                catch (IOException ex)
                {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg)
            {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void storeRegistrationId(Context context, String regId)
    {
        final SharedPreferences prefs = getSharedPreferences(
                Globals.PREFS_NAME, Context.MODE_PRIVATE);
        int appVersion = Globals.getAppVersion(context);
        Log.i(Globals.TAG, "Saving regId: " + regId + ", on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Globals.PREFS_PROPERTY_REG_ID, regId);
        editor.putInt(Globals.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


}
