package com.example.marc.radaralert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marc.myapplication.backend.submitAlert.SubmitAlert;
import com.example.marc.myapplication.backend.submitAlert.model.AlertRecord;
import com.example.marc.myapplication.backend.submitAlert.model.CollectionResponseAlertRecord;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class CustomMapFragment extends com.google.android.gms.maps.SupportMapFragment implements android.location.LocationListener,Observer{

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        googleMap = getMap();
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(final LatLng latLng) {
                //lstLatLngs.add(point);
                final EditText input = new EditText(getActivity());

                new AlertDialog.Builder(getActivity())
                        .setTitle("Afegir incidencia")
                        .setMessage("Descriu breument la incidencia:")
                        .setView(input)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                googleMap.addMarker(new MarkerOptions().position(latLng));
                                sendAlertToBackend(input.getText().toString(), latLng.latitude, latLng.longitude);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();

            }
        });
        Globals.instance.addObserver(this);
        updateAlerts();

}

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        //googleMap.addMarker(new MarkerOptions().position(latLng).title("Posicio actual"));

        googleMap.animateCamera(cameraUpdate);

        for (AlertRecord alert :Globals.instance.getAlertList()){
            Location radarLocation = new Location("radar");
            radarLocation.setLatitude(alert.getLat());
            radarLocation.setLongitude(alert.getLng());
            if(radarLocation.distanceTo(location)<100){
                nearRadarAlarm();
            }
        }
    }

    private void nearRadarAlarm() {
        /////////////////
        /////////////
        //////////////////
        //////////////////
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void updateAlerts(){
        if(Globals.instance.getAlertList()!=null){
            googleMap.clear();
            for (AlertRecord ar : Globals.instance.getAlertList()){
                //System.out.println("LAT:"+ ar.getLat()+"LON:"+ar.getLng());

                LatLng pos= new LatLng(ar.getLat(),ar.getLng());
                System.out.println(pos.toString());
                MarkerOptions marker =new MarkerOptions().position(pos);
                googleMap.addMarker(marker);
            }
        }
    }
    private void sendAlertToBackend(String message, final Double latitude, final Double longitude)
    {
        if(Globals.regid == null || Globals.regid.equals("")){
            Toast.makeText(getActivity(), "You must register first", Toast.LENGTH_LONG).show();
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
                    as.addAlert(params[0],latitude,longitude,Globals.regid,params[0]).execute();
                    msg = "Alerta enviada";
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
                Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(message);
    }

    @Override
    public void update(Observable observable, Object data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateAlerts();
            }
        });
    }
}
