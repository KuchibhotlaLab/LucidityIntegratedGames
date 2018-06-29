package com.lucidity.game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DisplayImageActivity extends AppCompatActivity {

    //Stores the username of the user
    private String username;

    //For communicating with AWS S3 storage
    private TransferHelper transferHelper;
    private AmazonS3Client s3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        //Gets the username passed from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

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
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f.delete();
                //Delete the file in AWS S3 storage
                DeleteS3 deleteS3 = new DeleteS3(f.getName());
                deleteS3.execute();
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                intent.putExtra("username", username);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });
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
}
