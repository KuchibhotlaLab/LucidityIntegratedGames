package com.lucidity.game;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CaregiverHomePage extends AppCompatActivity {

    //Stores the username of the user
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_home_page);

        //gets username and name from shared preferences
        Login login = new Login(getApplicationContext());
        username = login.getUsername();

        Button settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        Button btnGame = findViewById(R.id.administer_games);
        btnGame.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                /*Intent i = new Intent(getApplicationContext(), GameMenuActivity.class);
                i.putExtra("username", username);
                i.putExtra("isLucid", false);
                i.putExtra("isCare", true);
                i.putExtra("isPatient", false);
                startActivity(i);*/
                Intent i = new Intent(getApplicationContext(), FaceDetectActivity.class);
                i.putExtra("username", username);
                startActivity(i);
            }
        });

        Button btnTracking = findViewById(R.id.tracking);
        btnTracking.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TrackingActivity.class);
                i.putExtra("username", username);
                startActivity(i);
            }
        });

    }
}
