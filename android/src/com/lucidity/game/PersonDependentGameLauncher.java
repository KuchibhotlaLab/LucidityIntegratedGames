package com.lucidity.game;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by lixiaoyan on 7/3/18.
 */

public class PersonDependentGameLauncher extends AndroidApplication {
    final int WRITE_REQUEST_CODE = 1;

    private String username;

    //gives the names of pictures to use to the game
    private ArrayList<String> picturesForGame;
    //gives an arraylist of tags for each picture in the picture list
    //tags are ordered so that the name of the person/object is first, then the relation, then other tags
    private ArrayList<ArrayList<String>> tagsForGame;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // products JSONArray
    JSONArray picturesJSON = null;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    // url to get pictures and tags
    private static String url_get_pictures_and_tags = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/get_pictures_and_tags.php";

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

            picturesForGame = new ArrayList<>();
            tagsForGame = new ArrayList<>();
            GetPicturesAndTags task = new GetPicturesAndTags();

            task.execute();
            //wait for task to finish
            try {
                task.get(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
            initialize(new FacialMemoryGame(username, picturesForGame, tagsForGame), config);
        }
    }

    /**
     * Background Async Task to get the images and tags for the game
     * */
    class GetPicturesAndTags extends AsyncTask<String, String, String> {

        /**
         * Getting everything from MySQL
         * */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));

                // getting pictures and tags from web
                JSONObject json = jsonParser.makeHttpRequest(
                        url_get_pictures_and_tags, "GET", params);

                // check your log for json response
                Log.d("get pictures and tags", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    // Get array of pictures and tags
                    picturesJSON = json.getJSONArray("pictures");

                    // loop through pictures found
                    for (int i = 0; i < picturesJSON.length(); i++) {
                        JSONObject picObject = picturesJSON.getJSONObject(i);

                        // add pictures and tags to Arraylists in order
                        picturesForGame.add(picObject.getString("picname"));
                        ArrayList<String> picTags = new ArrayList<>();
                        picTags.add(picObject.getString("tagname"));
                        picTags.add(picObject.getString("tagrelation"));
                        tagsForGame.add(picTags);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "complete";
        }
    }
}