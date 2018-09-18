package com.lucidity.game;

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

        //gets username and name from shared preferences
        Login login = new Login(getApplicationContext());
        username = login.getUsername();
        name = login.getName();

        // display name at top of page
        TextView nameDisplay = (TextView) findViewById(R.id.name_display);
        nameDisplay.setText("Hello, " + name);

        Button btnGame = findViewById(R.id.game_button);
        btnGame.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GameMenuActivity.class);
                i.putExtra("username", username);
                i.putExtra("isLucid", false);
                i.putExtra("isCare", false);
                i.putExtra("isPatient", true);
                startActivity(i);

            }
        });

        Button btnPhotos = findViewById(R.id.photos_button);
        btnPhotos.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GalleryActivity.class);
                i.putExtra("username", username);
                i.putExtra("mode", 0);
                startActivity(i);

            }
        });
    }
}
