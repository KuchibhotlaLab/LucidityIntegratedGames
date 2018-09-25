package com.lucidity.game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DisplayImageActivity extends AppCompatActivity {

    //Stores the username of the user
    private String username;

    private int mode;

    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    //For communicating with AWS S3 storage
    private TransferHelper transferHelper;
    private AmazonS3Client s3;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    // url to add image data
    private static String url_delete_image = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/delete_image.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        //gets username from shared preferences
        Login login = new Login(getApplicationContext());
        username = login.getUsername();

        //Gets the mode from the previous activity
        mode = getIntent().getExtras().getInt("mode");

        //Used to delete file from AWS S3 storage
        transferHelper = new TransferHelper();
        s3 = transferHelper.getS3Client(this);

        ImageView targetImage = findViewById(R.id.targetimage);
        Bitmap bmp = null;
        //open file with the path passed here
        //and show on full screen in imageview
        String url = getIntent().getStringExtra("image");
        final File f = new File(url);
        try {
            bmp = BitmapFactory.decodeStream(new FileInputStream(f));
            targetImage.setImageBitmap(bmp);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        Button btnCancel = findViewById(R.id.cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnDelete = findViewById(R.id.delete);
        if (mode == 0){
            btnDelete.setVisibility(View.GONE);
        } else {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Do nothing if button was recently pressed
                    if (SystemClock.elapsedRealtime() - prevClickTime < 5000){
                        return;
                    }
                    prevClickTime = SystemClock.elapsedRealtime();

                    //Check for internet connection before deleting
                    ConnectivityChecker checker = ConnectivityChecker.getInstance(DisplayImageActivity.this);
                    if (checker.isConnected()){
                        f.delete();
                        //Delete the file in AWS S3 storage
                        DeleteS3 deleteS3 = new DeleteS3(f.getName());
                        deleteS3.execute();
                        //Delete the file in MySQL database
                        DeleteMySQL deleteMySQL = new DeleteMySQL(username, f.getName());
                        deleteMySQL.execute();
                        Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("mode", mode);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        checker.displayNoConnectionDialog();
                    }
                }
            });
        }
    }

    /**
     * Background Async Task to delete the file in AWS S3
     * */
    class DeleteS3 extends AsyncTask<String, String, String> {

        private String fileName;

        public DeleteS3(String file)
        {
            fileName = file;
        }

        /**
         * Delete the file in background
         * */
        protected String doInBackground(String... args) {

            //delete from AWS S3 storage
            try {
                s3.deleteObject(new DeleteObjectRequest(TransferHelper.BUCKETNAME, "public/user-images/"+username+"/" + fileName));
            }
            catch(AmazonServiceException e) {
                // The call was transmitted successfully, but Amazon S3 couldn't process
                // it, so it returned an error response.
                e.printStackTrace();
            }

            return null;
        }

    }

    /**
     * Background Async Task to delete the file in MySQL
     * */
    class DeleteMySQL extends AsyncTask<String, String, String> {

        private String uname;
        private String filename;

        public DeleteMySQL(String username, String file) {
            uname = username;
            filename = file;
        }

        /**
         * Delete the file in background
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", uname));
            params.add(new BasicNameValuePair("filename", filename));

            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(
                    url_delete_image, "POST", params);

            // check your log for json response
            Log.d("Delete Image", json.toString());

            // Check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                String msg = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    Log.d("Image Deleted", "Success");
                } else {
                    Log.d("Image Deleted", msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
