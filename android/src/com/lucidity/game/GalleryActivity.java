package com.lucidity.game;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    //Stores the username of the user
    private String username;

    ArrayList<Bitmap> images = new ArrayList<>();
    BitmapAdapter gridAdapter = null;

    //For uploading to AWS S3 storage
    private TransferHelper transferHelper;
    private TransferUtility transferUtility;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // url to add image data
    private static String url_add_image = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_image.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //Gets the username passed from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        GridView gallery = findViewById(R.id.photoGalary);
        Bitmap bmp = null;
        FloatingActionButton addImg = findViewById(R.id.add_img);

        //Used to upload to AWS S3 storage
        transferHelper = new TransferHelper();
        transferUtility = transferHelper.getTransferUtility(this);

        //load the images saved in app locally
        //stackoverflow.com/questions/5694385/
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File folder = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File subfolder = new File(folder, username);
        if(!subfolder.exists()){
            subfolder.mkdir();
        }
        File[] listOfFiles = subfolder.listFiles();
        ArrayList<File> listOfImages = new ArrayList<>();

        for(int i = 0; i < listOfFiles.length; i++){
            if(listOfFiles[i].isFile()) {
                listOfImages.add(listOfFiles[i]);
                images.add(loadFromInternalStorage(listOfFiles[i]));
            }
        }


        //get the image sent by selected
        //reference: stackoverflow.com/questions/11010386
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(bmp != null){
            //display file on this page
            images.add(bmp);

            //Don't overwrite existing images with the same name
            File fullpath = new File(subfolder,filename);
            int count = 1;
            while(fullpath.isFile()) {
                String extension = filename.substring(filename.lastIndexOf("."));
                String tempName = filename.substring(0, filename.lastIndexOf("."));
                if (tempName.endsWith("(" + (count - 1) + ")")) {
                    tempName = tempName.substring(0, tempName.lastIndexOf("("));
                }
                filename = tempName + "(" + count + ")" + extension;
                fullpath = new File(subfolder, filename);
                count++;
            }
            //save file locally
            String newUrl = saveToInternalStorage(bmp, filename);
            File newFile = new File(newUrl + "/" + filename);
            listOfImages.add(newFile);

            //Save file on AWS S3 storage
            TransferObserver observer = transferUtility.upload(TransferHelper.BUCKETNAME, "public/user-images/"+username+"/"+newFile.getName(),
                    newFile);
            observer.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        Toast.makeText(getApplicationContext(), "Upload Completed!", Toast.LENGTH_SHORT).show();

                    } else if (TransferState.FAILED == state) {
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;

                    Log.d("My app","ID:" + id + "|bytesCurrent: " + bytesCurrent + "|bytesTotal: " + bytesTotal + "|" + percentDone + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    ex.printStackTrace();
                }

            });

            //Save image name and tags in MySQL
            String imageName = getIntent().getStringExtra("image-name");
            String imageRelation = getIntent().getStringExtra("image-relation");
            SaveImage saveTask = new SaveImage(username, filename, imageName, imageRelation);
            saveTask.execute();

        }

        gridAdapter = new BitmapAdapter(getBaseContext(), images, listOfImages);
        gallery.setAdapter(gridAdapter);
        gallery.setOnItemClickListener(itemClickListener);



        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            ImageObject l = (ImageObject)gridAdapter.getItem(position);
            Intent intent = new Intent(getApplicationContext(), DisplayImageActivity.class);
            intent.putExtra("image", l.getUrl());
            intent.putExtra("username", username);
            startActivity(intent);
            //TODO: figure out why the newly added picture doesn't show up(bug can be deleted)
        }
    };


    public class BitmapAdapter extends BaseAdapter {

        private Context mContext;
        List<Bitmap> gridViewitems;
        List<File> files;

        public BitmapAdapter(Context c, List<Bitmap> gridViewitems, List<File> files){

            mContext = c;
            this.gridViewitems = gridViewitems;
            this.files = files;
        }

        @Override
        public int getCount() {
            return gridViewitems.size();
        }

        @Override
        public Object getItem(int position) {
            ImageObject io = new ImageObject(
                    files.get(position).getName(), files.get(position).getAbsolutePath());
            return io;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setPadding(20, 20, 20, 20);
            }
            else
            {
                imageView = (ImageView) convertView;
            }
            imageView.setImageBitmap(gridViewitems.get(position));
            return imageView;
        }


    }

    public class ImageObject {
        private String title;
        private String url;

        public ImageObject(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return title;
        }
    }


    private String saveToInternalStorage(Bitmap bitmapImage, String imgName){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File subfolder = new File(directory, username);

        // Create imageDir
        File mypath=new File(subfolder,imgName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return subfolder.getAbsolutePath();
    }


    private Bitmap loadFromInternalStorage(File f)
    {
        Bitmap b = null;

        try {
            //File f=new File(path, imgName);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imgPicker);
            //img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return b;

    }

    /**
     * Background Async Task to save image details to MySQL
     * */
    class SaveImage extends AsyncTask<String, String, String> {

        private String uname;
        private String fname;
        private String iname;
        private String irelation;

        public SaveImage(String username, String filename, String imageName, String imageRelation)
        {
            uname = username;
            fname = filename;
            iname = imageName;
            irelation = imageRelation;
        }

        /**
         * Save information
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", uname));
            params.add(new BasicNameValuePair("filename", fname));
            params.add(new BasicNameValuePair("imagename", iname));
            params.add(new BasicNameValuePair("imagerelation", irelation));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_add_image,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                String msg = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    Log.d("Check Image Added", "Success");
                } else {
                    Log.d("Check Image Added", msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
