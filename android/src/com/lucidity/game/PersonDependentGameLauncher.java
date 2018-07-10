package com.lucidity.game;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
/**
 * Created by lixiaoyan on 7/3/18.
 */

public class PersonDependentGameLauncher extends AndroidApplication {
    final int WRITE_REQUEST_CODE = 1;

    private String username;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ask permission to access local files
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Gets the username passed from previous activity
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                username = extras.getString("username");
            }

            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
            }
            AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
            initialize(new FacialMemoryGame(username), config);
        }
    }
}