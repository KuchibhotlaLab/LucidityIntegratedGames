package com.lucidity.game;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.location.LocationListener;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
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
    String testSuiteStartTime;

    //gives the names of pictures to use for the person dependent game
    private ArrayList<String> picturesForGame;
    //gives an arraylist of tags for each picture in the picture list
    //tags are ordered so that the name of the person/object is first, then the relation, then other tags
    private ArrayList<ArrayList<String>> tagsForGame;
    //gives an arraylist of genders for each picture in the picture list, ordered the same way
    private ArrayList<String> gendersForGame;
    private ArrayList<String> locations;
    private ArrayList<String> personalEvents;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //JSONArray of results
    JSONArray picturesJSON = null;

    //TODO: getlastlocation returns null if idling for too long- to fix
    @Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        //gets username from shared preferences
        Login login = new Login(getApplicationContext());
        username = login.getUsername();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isLucid = extras.getBoolean("isLucid");
            isCare = extras.getBoolean("isCare");
            isPatient = extras.getBoolean("isPatient");
            gameType = extras.getString("gametype");
            difficulty = extras.getInt("difficulty");
            if (gameType.equals("check order")) {
                order = extras.getString("order");
                gameCounter = extras.getInt("counter");
                testSuiteStartTime = extras.getString("startTime");
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
                    case 4:
                        gameType = "music";
                }
            }
        }
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        ActionResolverAndroid a = new ActionResolverAndroid(getApplicationContext(), username, isLucid, isCare, isPatient, order, gameCounter, difficulty);
        ScorePoster s = new ScorePosterAndroid(getApplicationContext(), testSuiteStartTime);

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
            initialize(new WorkingMemoryGame(a, s, currentDateTimeString, coordinates), config);
        } else if(gameType.equals("dep")){
            picturesForGame = new ArrayList<>();
            tagsForGame = new ArrayList<>();
            gendersForGame = new ArrayList<>();
            GetPicturesAndTags picTask = new GetPicturesAndTags();
            picTask.execute();

            //wait for task to finish
            try {
                picTask.get(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            if(picturesForGame.size() < 4){
                final Context mContext = this;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder
                        .setMessage("Please provide at least \n " +
                                "two pictures of each sex \n")
                        .setCancelable(false)
                        .setPositiveButton("ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.dismiss();
                                ((Activity) mContext).finish();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
            initialize(new FacialMemoryGame(a, s, picturesForGame, tagsForGame, gendersForGame,
                    currentDateTimeString, coordinates), config);
        } else if(gameType.equals("object")) {
            initialize(new ObjectRecognitionGame(a, s, currentDateTimeString, coordinates), config);
        } else if(gameType.equals("space")){
            initialize(new SpatialMemoryGame(a, s, currentDateTimeString, coordinates), config);
        } else if(gameType.equals("recall")) {
            personalEvents = new ArrayList<>();
            locations = new ArrayList<>();

            GetStoredLocation locTask = new GetStoredLocation();
            locTask.execute();
            try {
                locTask.get(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            if(personalEvents.size() < 2){
                final Context mContext = this;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder
                        .setMessage("Please provide at least two\n" +
                                "events in your personal history.")
                        .setCancelable(false)
                        .setPositiveButton("ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.dismiss();
                                ((Activity) mContext).finish();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
            initialize(new RecallGame(a, s, personalEvents, locations, currentDateTimeString,
                    coordinates), config);
        } else if(gameType.equals("music")){
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("audioDir", Context.MODE_PRIVATE);
            File subfolder = new File(directory, username);
            File[] files = new File(subfolder.getAbsolutePath()).listFiles();
            ArrayList<String> validSongs = new ArrayList<>();
            for(int i = 0; i < files.length; i++){
                if("mp3".equals(files[i].getName().substring(files[i].getName().length()-3, files[i].getName().length()))) {
                    validSongs.add(files[i].getAbsolutePath());
                }
            }
            if(validSongs.size() < 2){
                final Context mContext = this;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder
                        .setMessage("Please provide at least \n " +
                                "two songs that you have listened")
                        .setCancelable(false)
                        .setPositiveButton("ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.dismiss();
                                ((Activity) mContext).finish();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
            initialize(new MusicGame(a, s, currentDateTimeString, coordinates), config);
        }
    }

    /**
     * Background Async Task to get the images and tags for the person dependent game
     * */
    class GetPicturesAndTags extends AsyncTask<String, String, String> {

        /**
         * Getting everything from local SQLite database
         * */
        protected String doInBackground(String... args) {
            LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Images")
                    .build();
            ImageDAO imageDAO = database.getImageDAO();

            List<Image> images = imageDAO.getUserImages(username);

            for (Image image : images) {
                picturesForGame.add(image.getFileName());
                ArrayList<String> picTags = new ArrayList<>();
                picTags.add(image.getImageName());
                picTags.add(image.getImageRelation());
                tagsForGame.add(picTags);
                gendersForGame.add(String.valueOf(image.getGender()));
            }
            database.close();

            return "complete";
        }
    }


    /**
     * Background Async Task to get the images and tags for the person dependent game
     * */
    class GetStoredLocation extends AsyncTask<String, String, String> {

        /**
         * Getting everything from local SQLite database
         * */
        protected String doInBackground(String... args) {
            LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Histories")
                    .build();
            HistoryDAO historyDAO = database.getHistoryDAO();

            List<com.lucidity.game.History> events = historyDAO.getUserHistories(username);
            for(com.lucidity.game.History event : events) {
                personalEvents.add(event.getEvent());
                locations.add(event.getLocation());
            }
            database.close();

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
            System.out.println("Not enough permissions");
        }

        /*if (gps_enabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(" ".equals(coordinates)){
                coordinates = lastLocation.getLongitude() + " " + lastLocation.getLatitude();
            }
        }*/
        if (network_enabled) {
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                    @Override
                    public void onProviderEnabled(String provider) {
                    }
                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                    @Override
                    public void onLocationChanged(final Location location) {
                    }
                });


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
