package com.lucidity.game;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.badlogic.gdx.scenes.scene2d.ui.List;

import java.util.ArrayList;

public class QuestionnaireActivity extends AppCompatActivity {
    //Stores the username of the user
    private String username;
    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private ArrayList<String> mListLocations;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        Login login = new Login(getApplicationContext());
        username = login.getUsername();
        TextView locationPrompt = findViewById(R.id.location_prompt);
        locationPrompt.setText("Please list some of the locations that " + username + " have lived");

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mListLocations);

        ListView listView = findViewById(R.id.list_location);
        listView.setAdapter(adapter);

        final EditText editText = findViewById(R.id.addItem);

        Button addLocBtn = (Button) findViewById(R.id.addLocBtn);
        addLocBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mListLocations.add(editText.getText().toString());
                adapter.notifyDataSetChanged();
            }
        });
        //https://stackoverflow.com/questions/22144891/how-to-add-listview-items-on-button-click-using-adapter
        //https://stackoverflow.com/questions/34328235/how-to-extends-listactivity-where-appcompatactivity-in-android-activity
        //https://stackoverflow.com/questions/4540754/dynamically-add-elements-to-a-listview-android


    }
}
