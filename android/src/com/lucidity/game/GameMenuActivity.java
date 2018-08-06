package com.lucidity.game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameMenuActivity extends AppCompatActivity {

    //Stores the username and name of the user
    private String username;

    private boolean isLucid, isPatient, isCare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            isLucid = extras.getBoolean("isLucid");
            isCare = extras.getBoolean("isCare");
            isPatient = extras.getBoolean("isPatient");
        }

        Button btnGame1 = findViewById(R.id.game1_button);
        btnGame1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), AndroidLauncher.class);
                i.putExtra("username", username);
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
                Intent i = new Intent(getBaseContext(), AndroidLauncher.class);
                i.putExtra("username", username);
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
                Intent i = new Intent(getBaseContext(), AndroidLauncher.class);
                i.putExtra("username", username);
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
                Intent i = new Intent(getBaseContext(), AndroidLauncher.class);
                i.putExtra("username", username);
                i.putExtra("isLucid", isLucid);
                i.putExtra("isCare", isCare);
                i.putExtra("isPatient", isPatient);
                i.putExtra("gametype", "space");
                i.putExtra("difficulty", -1);
                startActivity(i);

            }
        });
    }
}
