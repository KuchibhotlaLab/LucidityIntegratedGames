package com.lucidity.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    //Stores the username of the user
    private String username;

    private static int RESULT_LOAD_IMAGE = 1;
    private Bitmap bmp = null;
    private String image;
    ImageView targetImage;
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

        //call intent to get image from gallery
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);


        /*FloatingActionButton loadImg = findViewById(R.id.loadimage);
        loadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call intent to get image from gallery
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });*/

        final RadioButton rb_male = findViewById(R.id.photo_male);
        final RadioButton rb_female = findViewById(R.id.photo_female);
        final RadioButton rb_other = findViewById(R.id.photo_other);

        //Only one can be checked
        rb_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_female.setChecked(false);
                rb_other.setChecked(false);
            }
        });

        rb_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_male.setChecked(false);
                rb_other.setChecked(false);
            }
        });

        rb_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_male.setChecked(false);
                rb_female.setChecked(false);
            }
        });

        Button btn_confirm = findViewById(R.id.btn_to_gallery);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText imName = (EditText)findViewById(R.id.image_name_input);
                EditText imRelation = (EditText)findViewById(R.id.image_relation_input);

                if(imName.getText().toString().trim().length() == 0){
                    imName.setError("Please enter the name of the person/object pictured");
                } else if(imRelation.getText().toString().trim().length() == 0){
                    imRelation.setError("Please enter the user's relation to the person/object pictured");
                } else if (rb_male.isChecked() || rb_female.isChecked() || rb_other.isChecked()){

                    //reference: stackoverflow.com/questions/11010386
                    //Write file
                    try {

                        FileOutputStream stream = getApplicationContext().openFileOutput(image, Context.MODE_PRIVATE);
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String gender;
                    if (rb_male.isChecked()){
                        gender = "M";
                    } else if (rb_female.isChecked()){
                        gender = "F";
                    } else {
                        gender = "O";
                    }

                    Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                    intent.putExtra("image", image);
                    intent.putExtra("username", username);
                    intent.putExtra("image-name", imName.getText().toString());
                    intent.putExtra("image-relation", imRelation.getText().toString());
                    intent.putExtra("gender", gender);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //Close Page
                    finish();
                } else {
                    Toast.makeText(PhotoActivity.this,
                            "Please Select a Gender", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            File imageFile = new File(picturePath);
            image = imageFile.getName();
            cursor.close();

            try {
                //this is the single image gotten from gallery
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            targetImage.setImageBitmap(bmp);

            //to know about the selected image width and height
            //Toast.makeText(PhotoActivity.this, targetImage.getDrawable().getIntrinsicWidth()+" & "+targetImage.getDrawable().getIntrinsicHeight(), Toast.LENGTH_SHORT).show();
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

    //https://stackoverflow.com/questions/13023788/how-to-load-an-image-in-image-view-from-gallery
    //source of reference

}
