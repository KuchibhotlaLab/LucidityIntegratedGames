package com.lucidity.game;

import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CaregiverHomePage extends AppCompatActivity {

    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_home_page);

        Button settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        Button btnGame = findViewById(R.id.administer_games);
        btnGame.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                //Check for internet connection before uploading
                ConnectivityChecker checker = ConnectivityChecker.getInstance(CaregiverHomePage.this);
                if (checker.isConnected()){
                    Intent i = new Intent(getApplicationContext(), FaceDetectActivity.class);
                    startActivity(i);
                } else {
                    checker.displayNoConnectionDialog();
                }
            }
        });

        Button btnTracking = findViewById(R.id.tracking);
        btnTracking.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                Intent i = new Intent(getApplicationContext(), TrackingActivity.class);
                startActivity(i);
            }
        });

    }
}
