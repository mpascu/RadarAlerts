package com.example.marc.radaralert;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.widget.Toast;


import com.example.marc.myapplication.backend.submitAlert.SubmitAlert;
import com.example.marc.myapplication.backend.submitAlert.model.AlertRecord;
import com.example.marc.myapplication.backend.submitAlert.model.CollectionResponseAlertRecord;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Montse on 18/03/2015.
 */
public class Globals extends Observable{

    public static Globals instance = new Globals();
    public static boolean notificationsActivated = true;

    private Globals (){

    }
    public static final String TAG = "GCM DEMO";

    public static final String GCM_SENDER_ID = "188957038907";
    // id-2210
    // 650116039430
    public static String URL = "http://mptdataserver.duckdns.org:3000/";
    public static final String PREFS_NAME = "GCM_DEMO";
    public static final String PREFS_PROPERTY_REG_ID = "registration_id";
    public static String regid;
    public static final long GCM_TIME_TO_LIVE = 60L * 60L * 24L * 7L * 4L; // 4 Weeks

    public static final String PROPERTY_APP_VERSION = "1";
    private static List<AlertRecord> alertList;

    public List<AlertRecord> getAlertList(){
        return alertList;
    }
    public void setAlertList(List<AlertRecord>list){
        alertList=list;
        setChanged();
        notifyObservers();
    }

    public static int getAppVersion(Context context) {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException(context.getString(R.string.could_not_get_package_name) + e);
        }
    }

    public void getAlertsFromBackend(final Context context){
        if(Globals.regid == null || Globals.regid.equals("")){
            //Toast.makeText(getApplicationContext(), "You must register first", Toast.LENGTH_LONG).show();
            return;
        }
        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... params){
                String msg = "";
                try{

                    SubmitAlert.Builder builder = new SubmitAlert.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                            .setRootUrl("https://crafty-shelter-88814.appspot.com/_ah/api/");

                    SubmitAlert as = builder.build();
                    CollectionResponseAlertRecord crar=as.listAlerts(100).execute();
                    setAlertList(crar.getItems());
                    msg = context.getString(R.string.alerts_recived);
                }
                catch (IOException ex)
                {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if(Globals.instance.getAlertList()!=null){

                }
                else{
                    msg = context.getString(R.string.non_alert_recived);
                }
                //Toast.makeText(getApp, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
