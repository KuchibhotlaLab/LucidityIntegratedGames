package com.lucidity.game;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Music extends AppCompatActivity {
    private String username;
    private String soundName = "new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        Login login = new Login(getApplicationContext());
        username = login.getUsername();



        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/audioDir
        File directory = cw.getDir("audioDir", Context.MODE_PRIVATE);
        File subfolder = new File(directory, username);
        File mypath=new File(subfolder,"audio");

    }
}
