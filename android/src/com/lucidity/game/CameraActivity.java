package com.lucidity.game;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    //Stores the username of the user
    private String username;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    File photo = null;
    private Bitmap bmp = null;
    private String imageName;
    private String timeS;
    ImageView targetImage;
    private Uri uri;
    //reference: stackoverflow.com/questions/13023788

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        //Gets the username passed from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        targetImage = (ImageView)findViewById(R.id.targetimage);

        //Check for permission to use camera
        if (ContextCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CameraActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            takephoto();
        }

        Button btn_confirm = findViewById(R.id.btn_to_gallery);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reference: stackoverflow.com/questions/11010386
                //Write file
                try {
                    FileOutputStream stream = getApplicationContext().openFileOutput(imageName, Context.MODE_PRIVATE);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Go to gallery and add the image
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                intent.putExtra("image", imageName);
                intent.putExtra("username", username);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //Close Page
                finish();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    takephoto();
                } else {
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    // Make a file for the camera to put the image in, then invoke the camera app
    private void takephoto(){
        timeS = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);;
        try {
            photo = File.createTempFile(timeS, ".jpg", directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        uri = FileProvider.getUriForFile(CameraActivity.this, BuildConfig.APPLICATION_ID + ".provider", photo);

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.putExtra(MediaStore. EXTRA_OUTPUT,
                uri);
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && null != data) {
            //Get bitmap from image saved from camera
            try {
                bmp = getBitmapFromUri(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //delete temperorary photo file
            photo.delete();
            targetImage.setImageBitmap(bmp);
            imageName = timeS + ".jpg";
        }

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
