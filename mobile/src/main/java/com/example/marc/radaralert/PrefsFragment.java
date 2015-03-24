package com.example.marc.radaralert;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Marc on 25/02/2015.
 */
public class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference);
        }
}
