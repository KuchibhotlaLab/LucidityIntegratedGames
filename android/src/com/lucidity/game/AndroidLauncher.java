package com.lucidity.game;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.location.LocationListener;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AndroidLauncher extends AndroidApplication {
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
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        locationListener = new myLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();

		initialize(new WorkingMemoryGame(username, currentDateTimeString, coordinates, isLucid, isPatient, isCare), config);
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
            // TODO Auto-generated method stub
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
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    }

}
