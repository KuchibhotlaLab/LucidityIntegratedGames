package com.lucidity.game;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AndroidLauncher extends AndroidApplication {
    final int WRITE_REQUEST_CODE = 1;
    final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String username;
    private boolean isLucid, isPatient, isCare;
    private String currentDateTimeString;
    String coordinates = " ";
    private String order = "";
    private int gameCounter = 0;
    private int difficulty;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private boolean gps_enabled = false;
    private boolean network_enabled = false;

    String gameType;

    //gives the names of pictures to use for the person dependent game
    private ArrayList<String> picturesForGame;
    //gives an arraylist of tags for each picture in the picture list
    //tags are ordered so that the name of the person/object is first, then the relation, then other tags
    private ArrayList<ArrayList<String>> tagsForGame;
    //gives an arraylist of genders for each picture in the picture list, ordered the same way
    private ArrayList<String> gendersForGame;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //JSONArray of results
    JSONArray picturesJSON = null;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    // url to get pictures and tags
    private static String url_get_pictures_and_tags = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/get_pictures_and_tags.php";

    //TODO: getlastlocation returns null if idling for too long- to fix
    @Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //Gets the username passed from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            isLucid = extras.getBoolean("isLucid");
            isCare = extras.getBoolean("isCare");
            isPatient = extras.getBoolean("isPatient");
            gameType = extras.getString("gametype");
            difficulty = extras.getInt("difficulty");
            if (gameType.equals("check order")) {
                order = extras.getString("order");
                gameCounter = extras.getInt("counter");
                int nextGame = Integer.valueOf(order.substring(gameCounter, gameCounter +1));
                switch (nextGame){
                    case 0:
                        gameType = "memory";
                        break;
                    case 1:
                        gameType = "dep";
                        break;
                    case 2:
                        gameType = "object";
                        break;
                    case 3:
                        gameType = "space";
                        break;
                }
            }
        }
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        ActionResolverAndroid a = new ActionResolverAndroid(getApplicationContext(), username, isLucid, isCare, isPatient, order, gameCounter, difficulty);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        currentDateTimeString = dateFormat.format(date);

        locationListener = new myLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();

        if(gameType.equals("memory")){
            initialize(new WorkingMemoryGame(a, currentDateTimeString, coordinates), config);
        } else if(gameType.equals("dep")){
            picturesForGame = new ArrayList<>();
            tagsForGame = new ArrayList<>();
            gendersForGame = new ArrayList<>();
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

            if(picturesForGame.size() < 4){
                //TODO: call dialogue
                //TODO: end activity
                this.finish();
            }
            initialize(new FacialMemoryGame(a, picturesForGame, tagsForGame, gendersForGame,
                    currentDateTimeString, coordinates), config);
        } else if(gameType.equals("object")) {
            initialize(new ObjectRecognitionGame(a, currentDateTimeString, coordinates), config);
        } else if(gameType.equals("space")){
            initialize(new SpatialMemoryGame(a, currentDateTimeString, coordinates), config);
        }
    }

    /**
     * Background Async Task to get the images and tags for the person dependent game
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
                        gendersForGame.add(picObject.getString("gender"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "complete";
        }
    }

    private void getLocation(){
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex){
        }
        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex){

        }

        if (!gps_enabled && !network_enabled) {
            System.out.println("cannot determine location");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Not enough permission");
        }

        /*if (gps_enabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(" ".equals(coordinates)){
                coordinates = lastLocation.getLongitude() + " " + lastLocation.getLatitude();
            }
        }*/
        if (network_enabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            Location lastLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(" ".equals(coordinates)){
                coordinates = lastLocation.getLongitude() + " " + lastLocation.getLatitude();
            }
        }
    }

    private class myLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            if(location!=null){
                locationManager.removeUpdates(locationListener);
                coordinates = location.getLongitude() + " " + location.getLatitude();
            }
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.e("TAG","lat and long "+latitude+" "+longitude);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

}
