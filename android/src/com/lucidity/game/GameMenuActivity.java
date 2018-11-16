package com.lucidity.game;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameMenuActivity extends AppCompatActivity {

    //Stores the username and name of the user
    private String username;

    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    private boolean isLucid, isPatient, isCare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        //gets username from shared preferences
        Login login = new Login(getApplicationContext());
        username = login.getUsername();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isLucid = extras.getBoolean("isLucid");
            isCare = extras.getBoolean("isCare");
            isPatient = extras.getBoolean("isPatient");
        }

        Button btnGame1 = findViewById(R.id.game1_button);
        btnGame1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                Intent i = new Intent(getBaseContext(), AndroidLauncher.class);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                i.putExtra("gametype", "memory");
                i.putExtra("difficulty", -1);
                startActivity(i);
            }
        });

        Button btnGame2 = findViewById(R.id.game2_button);
        btnGame2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                Intent i = new Intent(getBaseContext(), AndroidLauncher.class);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                i.putExtra("gametype", "dep");
                i.putExtra("difficulty", -1);
                startActivity(i);
            }
        });

        Button btnGame3 = findViewById(R.id.game3_button);
        btnGame3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                Intent i = new Intent(getBaseContext(), AndroidLauncher.class);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                i.putExtra("gametype", "object");
                i.putExtra("difficulty", -1);
                startActivity(i);
            }
        });

        Button btnGame4 = findViewById(R.id.game4_button);
        btnGame4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                Intent i = new Intent(getBaseContext(), AndroidLauncher.class);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                i.putExtra("gametype", "space");
                i.putExtra("difficulty", -1);
                startActivity(i);
            }
        });


        Button btnGame5 = findViewById(R.id.game5_button);
        btnGame5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                Intent i = new Intent(getBaseContext(), AndroidLauncher.class);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                i.putExtra("gametype", "recall");
                i.putExtra("difficulty", -1);
                startActivity(i);
            }
        });

        Button btnGame6 = findViewById(R.id.game6_button);
        btnGame6.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                Intent i = new Intent(getBaseContext(), AndroidLauncher.class);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                i.putExtra("gametype", "music");
                i.putExtra("difficulty", -1);
                startActivity(i);
            }
        });
    }
}
