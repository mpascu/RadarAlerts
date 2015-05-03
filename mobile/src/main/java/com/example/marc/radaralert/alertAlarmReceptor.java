package com.example.marc.radaralert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Marc on 03/05/2015.
 */
public class alertAlarmReceptor extends BroadcastReceiver {
    public alertAlarmReceptor() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent t = new Intent(context, musicService.class);
        context.startService(t);
    }
}
