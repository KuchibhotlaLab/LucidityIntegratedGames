package com.lucidity.game;

import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#E9E9E9"));

        Button register = findViewById(R.id.MH);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                Intent intent = new Intent(getApplicationContext(), MedHistoryDemo.class);
                startActivity(intent);
            }
        });

        Button wearable = findViewById(R.id.wearable);
        wearable.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                Intent intent = new Intent(getApplicationContext(), AddWearableActivity.class);
                startActivity(intent);
            }
        });

        Button test = findViewById(R.id.upload_test_material);
        test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                Intent intent = new Intent(getApplicationContext(), AddTestMaterialActivity.class);
                startActivity(intent);
            }
        });

    }
}
