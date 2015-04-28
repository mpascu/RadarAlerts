package com.example.marc.radaralert;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Montse on 18/03/2015.
 */
public class Globals
{
    public static final String TAG = "GCM DEMO";

    public static final String GCM_SENDER_ID = "424345632408";
    // id-2210
    // 650116039430

    public static final String PREFS_NAME = "GCM_DEMO";
    public static final String PREFS_PROPERTY_REG_ID = "registration_id";

    public static final long GCM_TIME_TO_LIVE = 60L * 60L * 24L * 7L * 4L; // 4 Weeks

    public static final String PROPERTY_APP_VERSION = "1";

    public static int getAppVersion(Context context) {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
