package com.lucidity.game;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

/**
 * Created by lixiaoyan on 7/12/18.
 */

public class ObjectRecognitionGameLauncher extends AndroidApplication {
    private boolean isLucid, isCare, isPatient;
    private String username;
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

        /*locationListener = new AndroidLauncher.myLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();*/

        initialize(new ObjectRecognitionGame(), config);
    }
}
