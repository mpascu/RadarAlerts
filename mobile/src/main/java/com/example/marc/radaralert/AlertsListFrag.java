package com.example.marc.radaralert;

import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marc.myapplication.backend.submitAlert.SubmitAlert;
import com.example.marc.myapplication.backend.submitAlert.model.AlertRecord;
import com.example.marc.myapplication.backend.submitAlert.model.CollectionResponseAlertRecord;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Marc on 19/03/2015.
 */
public class AlertsListFrag extends android.support.v4.app.ListFragment implements Observer {
    private String[] titols ={};
    private String[] desciptions ={};
    private Integer[] tags ={};
    private AlertArrayAdapter<String> adapter;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new AlertArrayAdapter<String>(getActivity(),
                R.layout.alert_list_item, titols, desciptions,tags);
        setListAdapter(adapter);
        Globals.instance.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (Globals.instance.getAlertList()!=null){
            int x = 0;
            int count=0;
            for (AlertRecord alert: Globals.instance.getAlertList()){
                if(alert.getRegId().equals(Globals.regid)){
                    count++;
                }
            }
            titols=new String[count];
            desciptions=new String[count];
            tags=new Integer[count];
            for (AlertRecord alert: Globals.instance.getAlertList()){
                //System.out.println("aaaaaaaaaaaaaa");
                if(alert.getRegId().equals(Globals.regid)){
                    //System.out.println("bbbbbbbbbbb");

                    titols[x]="("+alert.getTag()+") "+alert.getAlertId();
                    desciptions[x]=alert.getDescription();
                    tags[x]=alert.getTag();
                    x++;

                }
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new AlertArrayAdapter<String>(getActivity(),R.layout.alert_list_item, titols, desciptions,tags);
                    setListAdapter(adapter);
                }
            });
        }

    }




}

