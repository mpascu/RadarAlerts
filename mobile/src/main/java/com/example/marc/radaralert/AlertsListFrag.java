package com.example.marc.radaralert;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Marc on 19/03/2015.
 */
public class AlertsListFrag extends android.support.v4.app.ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] values = new String[] { "Radar1", "Radar2", "Alerta1",
                "Alerta2" };
        String[] desciptions = new String[] { "", "", "Camió volcat",
                "Retencions per obres" };
        AlertArrayAdapter<String> adapter = new AlertArrayAdapter<String>(getActivity(),
                R.layout.alert_list_item, values, desciptions);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // do something with the data
    }

}

