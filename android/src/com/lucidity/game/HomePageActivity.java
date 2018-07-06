package com.lucidity.game;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomePageActivity extends AppCompatActivity {

    //Stores the username and name of the user
    private String username;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            name = extras.getString("name");
        }

        // display name at top of page
        TextView nameDisplay = (TextView) findViewById(R.id.name_display);
        nameDisplay.setText("Hello, " + name);

        Button btnGame = findViewById(R.id.game_button);
        btnGame.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //startActivity(new Intent(getBaseContext(), AndroidLauncher.class));
                Intent i = new Intent(getBaseContext(), PersonDependentGameLauncher.class);
                i.putExtra("username", username);
                startActivity(i);

            }
        });
    }
}
