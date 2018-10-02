package com.lucidity.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

import java.io.File;
import java.util.ArrayList;
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

    //JSONArray of results
    JSONArray picturesJSON = null;

    // url to get pictures and tags
    private static String url_get_pictures_and_tags = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/get_pictures_and_tags.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test_material);

        Login login = new Login(getApplicationContext());
        username = login.getUsername();

        //Used to upload to AWS S3 storage
        transferHelper = new TransferHelper();
        transferUtility = transferHelper.getTransferUtility(this);

        Button btn_photo = findViewById(R.id.add_photo);
        btn_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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


                Button btnGallery = (Button) dialogLayout.findViewById(R.id.btn_add_gallery);
                Button btnCamera = (Button) dialogLayout.findViewById(R.id.btn_add_camera);
                Button btnStored = (Button) dialogLayout.findViewById(R.id.btn_current_gal);
                Button btnSync = (Button) dialogLayout.findViewById(R.id.btn_sync);
                Button btnDismiss = (Button) dialogLayout.findViewById(R.id.btn_cancel_img_dialog);


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
                            Sync syncTask = new Sync();
                            syncTask.execute();
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
    }

    /**
     * Background Async Task to sync pictures on phone with user's account
     * */
    class Sync extends AsyncTask<String, String, String> {

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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
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
                        for (int i = 0; i < files.length; i++) {
                            File file = files[i];
                            if (!picNames.contains(file.getName())) {
                                //Delete image and tags locally
                                LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Images")
                                        .build();
                                ImageDAO imageDAO = database.getImageDAO();

                                Image image = imageDAO.getImage(username, file.getName());
                                imageDAO.delete(image);

                                file.delete();
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

            return null;
        }
    }

}
