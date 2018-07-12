package com.lucidity.game;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.location.LocationListener;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.text.DateFormat;
import java.util.Date;

public class AndroidLauncher extends AndroidApplication {
    private String username;
    private String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    //private final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1;
    //String locationProvider = LocationManager.NETWORK_PROVIDER;
    //String coordinates;


    @Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //Gets the username passed from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        //getLocation();
        //System.out.println("coordinates are: " + coordinates);
		initialize(new WorkingMemoryGame(username, currentDateTimeString), config);
    }

    /*private void getLocation(){
        System.out.println("Got inside function");

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location;
        System.out.println("network enabled: " + network_enabled);

        if(network_enabled){
            System.out.println("Network Enabled");
            if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                        MY_PERMISSION_ACCESS_COURSE_LOCATION );
            }
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


            if(location!=null){
                coordinates = location.getLongitude() + " " + location.getLatitude();
            } else {
                System.out.println("???????");
            }
        }


        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                coordinates = location.getLongitude() + " " + location.getLatitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    MY_PERMISSION_ACCESS_COURSE_LOCATION );

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

    }*/

}
