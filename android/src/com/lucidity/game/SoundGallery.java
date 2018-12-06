package com.lucidity.game;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SoundGallery extends AppCompatActivity {
    private List<String> audioName;
    private ArrayAdapter<String> adapter;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_gallery);

        Login login = new Login(getApplicationContext());
        username = login.getUsername();

        ListView gallery = (ListView) findViewById(R.id.sound_gallery);
        getLocalAudio();
        displayAudio(gallery);

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO: play music while clicked
            }

        });
        //TODO: implement audio player for gallery
        //https://www.geeksforgeeks.org/play-audio-file-using-java/
    }

    private void getLocalAudio(){
        audioName = new ArrayList<String>();
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/audioDir
        File directory = cw.getDir("audioDir", Context.MODE_PRIVATE);
        File subfolder = new File(directory, username);
        File[] files = new File(subfolder.getAbsolutePath()).listFiles();
        for(int i = 0; i < files.length; i++){
            audioName.add(files[i].getAbsolutePath());
        }
    }

    private void displayAudio(ListView gallery){
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, audioName);
        gallery.setAdapter(adapter); //Set all the file in the list.
    }
}
