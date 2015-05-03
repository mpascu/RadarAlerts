package com.example.marc.radaralert;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marc.myapplication.backend.submitAlert.SubmitAlert;
import com.example.marc.myapplication.backend.submitAlert.model.AlertRecord;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class CustomMapFragment extends com.google.android.gms.maps.SupportMapFragment implements android.location.LocationListener, Observer {

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private GoogleMap googleMap;
    private UiSettings uiSettings;
    private LocationManager locationManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        googleMap = getMap();
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        googleMap.setMyLocationEnabled(true);
        uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(final LatLng latLng) {
                //lstLatLngs.add(point);
                final EditText input = new EditText(getActivity());
                input.setText("Radar");
                new AlertDialog.Builder(getActivity())
                        .setTitle("Afegir radar")
                        .setMessage("Titol: (radar fixe, mobil, semaforic)")
                        .setView(input)
                        .setPositiveButton("Afegir", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                googleMap.addMarker(new MarkerOptions().position(latLng));
                                Location l= new Location("");
                                l.setLatitude(latLng.latitude);
                                l.setLongitude(latLng.longitude);
                                sendAlertToBackend(input.getText().toString(), latLng.latitude, latLng.longitude, getNameFromLocation(l));
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
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
        if (Globals.instance.getAlertList()!=null){
            for (AlertRecord alert : Globals.instance.getAlertList()) {
                Location radarLocation = new Location("radar");
                radarLocation.setLatitude(alert.getLat());
                radarLocation.setLongitude(alert.getLng());
                if (radarLocation.distanceTo(location) < 100) {
                    nearRadarAlarm(radarLocation);
                }
            }
        }
    }

    private void nearRadarAlarm(Location l) {
        if (Globals.notificationsActivated) {
            Intent receptor = new Intent(getActivity(), alertAlarmReceptor.class);
            getActivity().sendBroadcast(receptor);
            showNotification(getNameFromLocation(l));
        }
    }

    private String getNameFromLocation(Location loc){
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String result = null;

        try {
            List<Address> addressList = geocoder.getFromLocation(
                    loc.getLatitude(), loc.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getSubThoroughfare()).append("\n");

                result = sb.toString();
                System.out.println(result);
            }
        } catch (IOException e) {
        }
        return result;
    }

    private void showNotification(String location) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Alerta, t'estas aproximant a un radar")
                        .setContentText(location);
        //Intent resultIntent = new Intent(getActivity(), MainActivity.class);


        /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);*/
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
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

    private void updateAlerts() {
        if (Globals.instance.getAlertList() != null) {
            googleMap.clear();
            for (AlertRecord ar : Globals.instance.getAlertList()) {
                //System.out.println("LAT:"+ ar.getLat()+"LON:"+ar.getLng());

                LatLng pos = new LatLng(ar.getLat(), ar.getLng());
                System.out.println(pos.toString());
                MarkerOptions marker = new MarkerOptions().position(pos);
                googleMap.addMarker(marker);
            }
        }
    }

    private void sendAlertToBackend(String message, final Double latitude, final Double longitude, String location) {
        if (Globals.regid == null || Globals.regid.equals("")) {
            Toast.makeText(getActivity(), "You must register first", Toast.LENGTH_LONG).show();
            return;
        }
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {

                    SubmitAlert.Builder builder = new SubmitAlert.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                            .setRootUrl("https://crafty-shelter-88814.appspot.com/_ah/api/");

                    SubmitAlert as = builder.build();
                    as.addAlert(params[0], latitude, longitude, Globals.regid, params[1]).execute();
                    msg = "Alerta enviada";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(message, location);
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
