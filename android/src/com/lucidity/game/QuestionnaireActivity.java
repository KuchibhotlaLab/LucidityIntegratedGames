package com.lucidity.game;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.badlogic.gdx.scenes.scene2d.ui.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuestionnaireActivity extends AppCompatActivity {
    //Stores the username of the user
    private String username;
    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;

    // url to add and delete location
    private static String url_add_location = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_location.php";
    private static String url_delete_location = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/delete_location.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private ArrayList<String> mListLocations;
    //private ArrayAdapter<String> adapter;
    private ListItemAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        Login login = new Login(getApplicationContext());
        username = login.getUsername();
        TextView locationPrompt = findViewById(R.id.location_prompt);
        locationPrompt.setText("Please list some of the locations \nthat " + username + " has lived in");

        adapter = new ListItemAdapter(this);
        ListView listView = findViewById(R.id.list_location);
        listView.setAdapter(adapter);


        final EditText editText = findViewById(R.id.addLoc);
        Button addLocBtn = findViewById(R.id.addLocBtn);
        addLocBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                adapter.addItem(editText.getText().toString());
                editText.getText().clear();
            }
        });
        //stackoverflow.com/questions/22144891/
    }

    public class ListItemAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> mListLocations = new ArrayList<>();
        private Context context;

        ListItemAdapter(Context context){
            this.mListLocations = new ArrayList<>();
            this.context = context;

            new Thread(new Runnable() {
                public void run() {
                    //Load previously added locations
                    LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Locations")
                            .build();
                    LocationDAO locationDAO = database.getLocationDAO();

                    java.util.List<Location> prevLocations = locationDAO.getUserLocations(username);
                    for(Location prevLocation : prevLocations) {
                        mListLocations.add(prevLocation.getLocation());
                    }
                    database.close();
                    notifyDataSetChanged();
                }
            }).start();
        }

        @Override
        public int getCount() {
            return mListLocations.size();
        }

        @Override
        public Object getItem(int pos) {
            return mListLocations.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return mListLocations.indexOf(mListLocations.get(pos));
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.locations_listitem, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.location_names);
            listItemText.setText(mListLocations.get(position));

            //Handle buttons and add onClickListeners
            ImageButton deleteBtn = view.findViewById(R.id.delete_loc);
            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //TODO: call dialoge
                    DeleteItem deleteTask = new DeleteItem(mListLocations.get(position));
                    deleteTask.execute();
                    mListLocations.remove(position); //or some other task
                    notifyDataSetChanged();
                }
            });

            return view;
        }

        public void addItem(String item){
            mListLocations.add(item);
            SaveItem saveTask = new SaveItem(item);
            saveTask.execute();
            notifyDataSetChanged();
        }
    }

    /**
     * Background Async Task to save added location to MySQL
     * */
    class SaveItem extends AsyncTask<String, String, String> {
        private String loc;

        public SaveItem(String location)
        {
            loc = location;
        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(QuestionnaireActivity.this);
            pDialog.setMessage("Uploading location...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Save information
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            java.util.List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("location", loc));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_add_location,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                String msg = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    Log.d("Check Location Added", "Success");

                    //Save location locally
                    LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Locations")
                            .build();
                    LocationDAO locationDAO = database.getLocationDAO();

                    Location location = new Location();
                    location.setUsername(username);
                    location.setLocation(loc);

                    locationDAO.insert(location);
                    database.close();
                } else {
                    Log.d("Check Location Added", msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }

    /**
     * Background Async Task to delete a location from MySQL
     * */
    class DeleteItem extends AsyncTask<String, String, String> {
        private String loc;

        public DeleteItem(String location)
        {
            loc = location;
        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(QuestionnaireActivity.this);
            pDialog.setMessage("Deleting location...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Delete information
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            java.util.List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("location", loc));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_delete_location,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                String msg = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    Log.d("Check Location Deleted", "Success");

                    //Delete location locally
                    LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Locations")
                            .build();
                    LocationDAO locationDAO = database.getLocationDAO();

                    Location location = locationDAO.getLocation(username, loc);
                    locationDAO.delete(location);
                    database.close();
                } else {
                    Log.d("Check Location Deleted", msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }
}
