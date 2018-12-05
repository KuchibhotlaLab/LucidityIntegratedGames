package com.lucidity.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddTestMaterialActivity extends AppCompatActivity {

    //Stores the username of the user
    private String username;

    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    //For uploading to AWS S3 storage
    private TransferHelper transferHelper;
    private TransferUtility transferUtility;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;
    private ProgressDialog pDialog2;

    //JSONArray of results
    JSONArray picturesJSON = null;
    JSONArray locationsJSON = null;

    // url to get pictures and tags
    private static String url_get_pictures_and_tags = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/get_pictures_and_tags.php";

    // url to get locations
    private static String url_get_locations = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/get_locations.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    private static final int READ_REQUEST_CODE = 1;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test_material);

        Login login = new Login(getApplicationContext());
        username = login.getUsername();

        //Used to upload to AWS S3 storage
        transferHelper = new TransferHelper();
        transferUtility = transferHelper.getTransferUtility(this);

        //Pop up menu for logging out
        final Button btnMenu = findViewById(R.id.button_menu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(AddTestMaterialActivity.this,btnMenu);
                menu.getMenuInflater().inflate(R.menu.menu_popup_test_material, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sync:
                                ConnectivityChecker checker = ConnectivityChecker.getInstance(AddTestMaterialActivity.this);
                                if (checker.isConnected()){
                                    SyncPic syncPicTask = new SyncPic();
                                    syncPicTask.execute();
                                    SyncLoc syncLocTask = new SyncLoc();
                                    syncLocTask.execute();
                                } else {
                                    checker.displayNoConnectionDialog();
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                menu.show();
            }
        });

        Button btn_photo = findViewById(R.id.add_photo);
        btn_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                AlertDialog.Builder builder = new AlertDialog.Builder(AddTestMaterialActivity.this);
                LayoutInflater inflater = ((Activity) AddTestMaterialActivity.this).getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.add_image_dialog,
                        null);

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.setView(dialogLayout, 0, 0, 0, 0);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                WindowManager.LayoutParams wlmp = dialog.getWindow()
                        .getAttributes();
                wlmp.gravity = Gravity.BOTTOM;


                Button btnGallery = dialogLayout.findViewById(R.id.btn_add_gallery);
                Button btnCamera = dialogLayout.findViewById(R.id.btn_add_camera);
                Button btnStored = dialogLayout.findViewById(R.id.btn_current_gal);
                Button btnSync = dialogLayout.findViewById(R.id.btn_sync);
                Button btnDismiss = dialogLayout.findViewById(R.id.btn_cancel_img_dialog);


                btnGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do nothing if button was recently pressed
                        if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                            return;
                        }
                        prevClickTime = SystemClock.elapsedRealtime();

                        Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                        startActivity(intent);
                    }
                });

                btnCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do nothing if button was recently pressed
                        if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                            return;
                        }
                        prevClickTime = SystemClock.elapsedRealtime();

                        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent);
                    }
                });

                btnStored.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do nothing if button was recently pressed
                        if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                            return;
                        }
                        prevClickTime = SystemClock.elapsedRealtime();

                        Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                        intent.putExtra("mode", 1);
                        startActivity(intent);
                    }
                });

                btnSync.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do nothing if button was recently pressed
                        if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                            return;
                        }
                        prevClickTime = SystemClock.elapsedRealtime();

                        //Check for internet connection before uploading
                        ConnectivityChecker checker = ConnectivityChecker.getInstance(AddTestMaterialActivity.this);
                        if (checker.isConnected()){
                            SyncPic syncPicTask = new SyncPic();
                            syncPicTask.execute();
                        } else {
                            checker.displayNoConnectionDialog();
                        }
                    }
                });

                btnDismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                builder.setView(dialogLayout);

                dialog.show();
            }
        });


        //if chooses to add other non-photo information
        Button btnOtherInfo = findViewById(R.id.add_history);
        btnOtherInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                AlertDialog.Builder builder = new AlertDialog.Builder(AddTestMaterialActivity.this);
                LayoutInflater inflater = ( AddTestMaterialActivity.this).getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.add_location_dialog,
                        null);

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.setView(dialogLayout, 0, 0, 0, 0);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                WindowManager.LayoutParams wlmp = dialog.getWindow()
                        .getAttributes();
                wlmp.gravity = Gravity.BOTTOM;


                Button btnView = dialogLayout.findViewById(R.id.btn_view_location);
                Button btnSync = dialogLayout.findViewById(R.id.btn_sync_location);
                Button btnDismiss = dialogLayout.findViewById(R.id.btn_cancel_location_dialog);


                btnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do nothing if button was recently pressed
                        if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                            return;
                        }
                        prevClickTime = SystemClock.elapsedRealtime();

                        Intent intent = new Intent(getApplicationContext(), HistoryQuestionnaireActivity.class);
                        startActivity(intent);
                    }
                });


                btnSync.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do nothing if button was recently pressed
                        if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                            return;
                        }
                        prevClickTime = SystemClock.elapsedRealtime();

                        //Check for internet connection before uploading
                        ConnectivityChecker checker = ConnectivityChecker.getInstance(AddTestMaterialActivity.this);
                        if (checker.isConnected()){
                            SyncLoc syncLocTask = new SyncLoc();
                            syncLocTask.execute();
                        } else {
                            checker.displayNoConnectionDialog();
                        }
                    }
                });

                btnDismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                builder.setView(dialogLayout);

                dialog.show();
            }
        });

        Button btnSound = findViewById(R.id.add_sound);
        btnSound.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddTestMaterialActivity.this);
                LayoutInflater inflater = (AddTestMaterialActivity.this).getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.add_sound_dialog,
                        null);

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.setView(dialogLayout, 0, 0, 0, 0);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                WindowManager.LayoutParams wlmp = dialog.getWindow()
                        .getAttributes();
                wlmp.gravity = Gravity.BOTTOM;


                Button btnRecord = dialogLayout.findViewById(R.id.btn_add_recording);
                Button btnUpload = dialogLayout.findViewById(R.id.btn_add_online);
                Button btnStored = dialogLayout.findViewById(R.id.btn_recording_gallery);
                Button btnDismiss = dialogLayout.findViewById(R.id.btn_sound_dismiss);


                btnRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do nothing if button was recently pressed
                        if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                            return;
                        }
                        prevClickTime = SystemClock.elapsedRealtime();

                        Intent intent = new Intent(getApplicationContext(), AddSounds.class);
                        startActivity(intent);
                    }
                });

                btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do nothing if button was recently pressed
                        if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                            return;
                        }
                        prevClickTime = SystemClock.elapsedRealtime();

                        //Intent intent = new Intent(getApplicationContext(), AddSpotify.class);

                        openFolder();

                        //Intent intent = new Intent(getApplicationContext(), Music.class);
                        //startActivity(intent);
                    }
                });

                btnStored.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Do nothing if button was recently pressed
                        if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                            return;
                        }
                        prevClickTime = SystemClock.elapsedRealtime();

                        Intent intent = new Intent(getApplicationContext(), SoundGallery.class);
                        intent.putExtra("mode", 1);
                        startActivity(intent);
                    }
                });
                btnDismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                builder.setView(dialogLayout);
                dialog.show();
            }
        });
    }

    /**
     * Background Async Task to sync pictures on phone with user's account
     * */
    class SyncPic extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddTestMaterialActivity.this);
            pDialog.setMessage("Syncing Pictures...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Get picture names from MySQL, then download the ones that aren't on the phone
         */
        protected String doInBackground(String... args) {

            final ArrayList<String> picNames = new ArrayList<>();
            //gives an arraylist of tags for each picture in the picture list
            //tags are ordered so that the name of the person/object is first, then the relation, then other tags
            ArrayList<ArrayList<String>> picTags = new ArrayList<>();
            //gives an arraylist of genders for each picture in the picture list, ordered the same way
            ArrayList<String> picGenders = new ArrayList<>();

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", username));

                // getting pictures from web
                JSONObject json = jsonParser.makeHttpRequest(
                        url_get_pictures_and_tags, "GET", params);

                // check your log for json response
                Log.d("get pictures", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    // Get array of pictures and tags
                    picturesJSON = json.getJSONArray("pictures");

                    // loop through pictures found
                    for (int i = 0; i < picturesJSON.length(); i++) {
                        JSONObject picObject = picturesJSON.getJSONObject(i);

                        // add pictures and tags to Arraylists in order
                        picNames.add(picObject.getString("picname"));
                        ArrayList<String> onePicTags = new ArrayList<>();
                        onePicTags.add(picObject.getString("tagname"));
                        onePicTags.add(picObject.getString("tagrelation"));
                        picTags.add(onePicTags);
                        picGenders.add(picObject.getString("gender"));
                    }

                    File folder = new File("data/user/0/com.lucidity.game/app_imageDir/" + username);

                    if (folder.exists()) {
                        File[] files = folder.listFiles();
                        for (File file : files) {
                            if (!picNames.contains(file.getName())) {
                                //Delete image and tags locally
                                LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Images")
                                        .build();
                                ImageDAO imageDAO = database.getImageDAO();

                                Image image = imageDAO.getImage(username, file.getName());
                                imageDAO.delete(image);

                                file.delete();
                                database.close();
                            }
                        }
                    }

                    for (int i = 0; i < picNames.size(); i++) {
                        File file = new File(folder, picNames.get(i));
                        if (!file.exists()){
                            downloadS3(picNames.get(i), picTags.get(i), picGenders.get(i).charAt(0));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Pictures Synced!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Background Async Task to sync locations on phone with user's account
     * */
    class SyncLoc extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(AddTestMaterialActivity.this);
            pDialog2.setMessage("Syncing Locations...");
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(false);
            pDialog2.show();
        }

        /**
         * Get locations from MySQL, then download the ones that aren't on the phone
         */
        protected String doInBackground(String... args) {

            final ArrayList<String> locations = new ArrayList<>();

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", username));

                // getting pictures from web
                JSONObject json = jsonParser.makeHttpRequest(
                        url_get_locations, "GET", params);

                // check your log for json response
                Log.d("get locations", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    // Get array of locations
                    locationsJSON = json.getJSONArray("locations");

                    // loop through locations found
                    for (int i = 0; i < locationsJSON.length(); i++) {
                        JSONObject locObject = locationsJSON.getJSONObject(i);

                        // add locations to arraylists
                        locations.add(locObject.getString("location"));
                    }

                    LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Locations")
                            .build();
                    LocationDAO locationDAO = database.getLocationDAO();

                    List<Location> localLocations = locationDAO.getUserLocations(username);

                    //Delete locations on local device but not on web server
                    for (Location e: localLocations) {
                        if(!locations.contains(e.getLocation())) {
                            locationDAO.delete(e);
                        }
                    }

                    ArrayList<String> localLocationsStrings = new ArrayList<>();
                    localLocations = locationDAO.getUserLocations(username);
                    for (Location e: localLocations) {
                        localLocationsStrings.add(e.getLocation());
                    }

                    for (String e: locations) {
                        if (!localLocationsStrings.contains(e)){
                            Location location = new Location();
                            location.setUsername(username);
                            location.setLocation(e);

                            locationDAO.insert(location);
                        }
                    }
                    database.close();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog2.dismiss();
            Toast.makeText(getApplicationContext(), "Locations Synced!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * download pictures from web server onto phone
     * */
    public void downloadS3(String pic, ArrayList<String> tags, char gender){
        final String filename = pic;
        final String picturename = tags.get(0);
        final String picturerelation = tags.get(1);
        final char g = gender;

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File subfolder = new File(directory, username);
        File mypath=new File(subfolder, filename);

        TransferObserver observer =
                transferUtility.download(TransferHelper.BUCKETNAME,"public/user-images/"+username+"/" + filename,
                        mypath);

        // Attach a listener to the observer to get notified of the
        // updates in the state and the progress
        observer.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {

                    //Save image name and tags locally
                    Image image = new Image();
                    image.setUsername(username);
                    image.setFileName(filename);
                    image.setImageName(picturename);
                    image.setImageRelation(picturerelation);
                    image.setGender(g);

                    AddImage addTask = new AddImage(image);
                    addTask.execute();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d("GalleryActivity", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });
    }

    /**
     * Background Async Task to add an image that needs to be synced
     * */
    class AddImage extends AsyncTask<String, String, String> {

        private Image image;

        public AddImage(Image syncedImage)
        {
            image = syncedImage;
        }

        protected String doInBackground(String... args) {

            //Save image name and tags locally
            LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Images")
                    .build();
            ImageDAO imageDAO = database.getImageDAO();

            imageDAO.insert(image);
            database.close();

            return null;
        }
    }

    public void openFolder()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/myFolder/");
        intent.setDataAndType(uri, "audio/*");
        //startActivity(Intent.createChooser(intent, "Open folder"));
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {

            Uri currFileURI = data.getData();
            String path = currFileURI.getPath();
            //System.out.println("filepath returned = " + path);
            List folders = Arrays.asList(path.split("/"));
            String name = folders.get(folders.size() - 1).toString();
            if("mp3".equals(name.substring(name.length()-3))){
                Intent intent = new Intent(getApplicationContext(), Music.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
            /*String Fpath = data.getDataString();
            System.out.println("filepath returned = " + Fpath);
            super.onActivityResult(requestCode, resultCode, data);*/
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            File file = new File(data.getData().getPath()) ;
            String path = file.getAbsolutePath() ;
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(path));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append("\n");

                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
            builder.setMessage(path)
                    .show() ;

        }
    }*/

}
