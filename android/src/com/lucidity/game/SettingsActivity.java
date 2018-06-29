package com.lucidity.game;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

    //Stores the username of the user
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Gets the username passed from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#E9E9E9"));

        Button register = findViewById(R.id.MH);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MedHistoryDemo.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        Button wearable = findViewById(R.id.wearable);
        wearable.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddWearableActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        Button test = findViewById(R.id.upload_test_material);
        test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTestMaterialActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

    }
}
