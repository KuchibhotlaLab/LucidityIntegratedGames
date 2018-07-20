package com.lucidity.game;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

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

/**
 * Created by lixiaoyan on 7/3/18.
 */

public class PersonDependentGameLauncher extends AndroidApplication {
    final int WRITE_REQUEST_CODE = 1;
    final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    String coordinates = " ";

    private LocationManager locationManager;
    private LocationListener locationListener;

    private boolean gps_enabled = false;
    private boolean network_enabled = false;

    private boolean isLucid, isPatient, isCare;
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
                isLucid = extras.getBoolean("isLucid");
                isCare = extras.getBoolean("isCare");
                isPatient = extras.getBoolean("isPatient");

            }

            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
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

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            currentDateTimeString = dateFormat.format(date);

            locationListener = new myLocationListener();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            getLocation();

            AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
            initialize(new FacialMemoryGame(username, picturesForGame, tagsForGame,
                    currentDateTimeString, coordinates, isLucid, isPatient, isCare), config);
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
        }

        if (gps_enabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(" ".equals(coordinates)){
                coordinates = lastLocation.getLongitude() + " " + lastLocation.getLatitude();
            }
        }

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
            // TODO Auto-generated method stub
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
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    }
}