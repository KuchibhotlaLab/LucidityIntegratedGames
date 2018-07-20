package com.lucidity.game;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lixiaoyan on 7/12/18.
 */

public class ObjectRecognitionGameLauncher extends AndroidApplication {
    final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String username;
    private boolean isLucid, isPatient, isCare;
    private String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    String coordinates = " ";

    private LocationManager locationManager;
    private LocationListener locationListener;

    private boolean gps_enabled = false;
    private boolean network_enabled = false;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Gets the username passed from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            isLucid = extras.getBoolean("isLucid");
            isCare = extras.getBoolean("isCare");
            isPatient = extras.getBoolean("isPatient");
        }

        ActionResolverAndroid a = new ActionResolverAndroid(getApplicationContext(), username, isLucid, isCare, isPatient);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        currentDateTimeString = dateFormat.format(date);

        locationListener = new myLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();

        initialize(new ObjectRecognitionGame(a, username, currentDateTimeString, coordinates, isLucid, isPatient, isCare), config);
    }

    private void getLocation(){
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex){
        }
        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex){

        }

        if (!gps_enabled && !network_enabled) {
            System.out.println("cannot determine location");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Not enough permission");
        }

        if (gps_enabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(" ".equals(coordinates)){
                coordinates = lastLocation.getLongitude() + " " + lastLocation.getLatitude();
            }
        }

        if (network_enabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            Location lastLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(" ".equals(coordinates)){
                coordinates = lastLocation.getLongitude() + " " + lastLocation.getLatitude();
            }
        }

    }

    private class myLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            if(location!=null){
                locationManager.removeUpdates(locationListener);
                coordinates = location.getLongitude() + " " + location.getLatitude();
            }
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.e("TAG","lat and long "+latitude+" "+longitude);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }
}
