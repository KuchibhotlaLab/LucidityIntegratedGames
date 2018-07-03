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

        //Gets the username passed from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

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
                startActivity(new Intent(getBaseContext(), AndroidLauncher.class));
                //startActivity(new Intent(getBaseContext(), PersonDependentGameLauncher.class));

                //save instance of the game
                //stackoverflow.com/questions/25209480/
                /*Game game = new Game(this)
                View gameView = initializeForView(game, cfg);
                layout.addView(gameView)*/
            }
        });

    }
}
