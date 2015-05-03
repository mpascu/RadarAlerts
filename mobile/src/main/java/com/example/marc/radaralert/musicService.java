package com.example.marc.radaralert;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by Marc on 03/05/2015.
 */
public class musicService extends Service {
    private MediaPlayer mp;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mp = MediaPlayer.create(this, R.raw.alarm);

        mp.start();
        return startId;
    }

    @Override
    public void onDestroy() {
        mp.stop();
    }
}
