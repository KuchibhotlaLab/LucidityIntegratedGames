package com.lucidity.game;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    //Stores the username of the user
    private String username;
    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;

    // urls
    private static String url_add_event = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_history.php";
    private static String url_delete_event = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/delete_history.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private ArrayList<String> mListLocations;
    private ArrayList<ArrayList<History>> mListEvents;
    //private ArrayAdapter<String> adapter;
    private ListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Login login = new Login(getApplicationContext());
        username = login.getUsername();

        adapter = new ListItemAdapter(this);
        ListView listView = findViewById(R.id.list_location);
        listView.setAdapter(adapter);

        Button btnAddEvent = findViewById(R.id.addEventBtn);
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                showDialog();
            }
        });
    }

    public class ListItemAdapter extends BaseAdapter implements ListAdapter {
        private Context context;

        ListItemAdapter(Context context){
            mListLocations = new ArrayList<>();
            mListEvents = new ArrayList<>();
            this.context = context;

            new Thread(new Runnable() {
                public void run() {
                    //Load previously added locations
                    LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Histories")
                            .build();
                    HistoryDAO historyDAO = database.getHistoryDAO();

                    java.util.List<History> prevHistories = historyDAO.getUserHistories(username);
                    for(History prevHistory : prevHistories) {
                        if (!mListLocations.contains(prevHistory.getLocation())) {
                            mListLocations.add(prevHistory.getLocation());
                            ArrayList<History> locEvents = new ArrayList<>();
                            locEvents.add(prevHistory);
                            mListEvents.add(locEvents);
                        } else {
                            mListEvents.get(mListLocations.indexOf(prevHistory.getLocation())).add(prevHistory);
                        }
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (view == null) {
                view = inflater.inflate(R.layout.locations_listitem, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.location_names);
            listItemText.setText(mListLocations.get(position));
            final TextView expandText = (TextView)view.findViewById(R.id.expand_text);

            final LinearLayout listItemInnerList = view.findViewById(R.id.location_event_list);

            final ArrayList<History> currLocHistories = mListEvents.get(position);
            for (final History e : currLocHistories) {
                View eventView = inflater.inflate(R.layout.location_event_listitem, null);
                TextView eventText = eventView.findViewById(R.id.event_details);
                eventText.setText("\n"+ e.getEvent() + "\n" + e.getYear());
                ImageButton eventDel = eventView.findViewById(R.id.delete_event);
                eventDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Do nothing if button was recently pressed
                        if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                            return;
                        }
                        prevClickTime = SystemClock.elapsedRealtime();

                        //Check for internet connection first
                        ConnectivityChecker checker = ConnectivityChecker.getInstance(HistoryActivity.this);
                        if (checker.isConnected()){
                            DeleteEvent deleteTask = new DeleteEvent(e, true);
                            deleteTask.execute();
                        } else {
                            checker.displayNoConnectionDialog();
                        }
                    }
                });
                listItemInnerList.addView(eventView);
            }
            listItemInnerList.setVisibility(View.GONE);

            expandText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (!listItemInnerList.isShown()) {
                        listItemInnerList.setVisibility(View.VISIBLE);
                        expandText.setText("(Collapse)");
                    } else {
                        listItemInnerList.setVisibility(View.GONE);
                        expandText.setText("(Expand)");
                    }
                }
            });

            //Handle buttons and add onClickListeners
            ImageButton deleteBtn = view.findViewById(R.id.delete_loc);
            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //Do nothing if button was recently pressed
                    if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                        return;
                    }
                    prevClickTime = SystemClock.elapsedRealtime();

                    for (int i = 0; i < currLocHistories.size(); i++) {
                        //Check for internet connection first
                        ConnectivityChecker checker = ConnectivityChecker.getInstance(HistoryActivity.this);
                        if (checker.isConnected()){
                            boolean finish = i == currLocHistories.size() - 1;
                            DeleteEvent deleteTask = new DeleteEvent(currLocHistories.get(i), finish);
                            deleteTask.execute();
                        } else {
                            checker.displayNoConnectionDialog();
                        }
                    }
                }
            });

            return view;
        }
    }

    public void showDialog()
    {
        LayoutInflater li = LayoutInflater.from(HistoryActivity.this);
        View promptsView = li.inflate(R.layout.add_event_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HistoryActivity.this);
        alertDialogBuilder.setView(promptsView);

        final EditText eventInput = (EditText) promptsView
                .findViewById(R.id.event_to_add);
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.location_to_add);
        final EditText yearsInput = (EditText) promptsView
                .findViewById(R.id.years_to_add);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                .build();
        Typeface font = Typeface.createFromAsset(getAssets(),"data/Kayak-Sans-Regular.ttf");
        int hintColor = eventInput.getCurrentHintTextColor();

        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setHint("Name of Location");
        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        final EditText locationText = autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input);
        locationText.setTypeface(font);
        locationText.setTextSize(18);
        locationText.setGravity(Gravity.CENTER);
        locationText.setHintTextColor(hintColor);
        locationText.setPadding(4,0,0,0);
        locationText.setBackgroundResource(android.R.drawable.edit_text);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("success", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.i("failure", "An error occurred: " + status);
            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Add Event",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.dismiss();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.remove(autocompleteFragment);
                                fragmentTransaction.commit();
                            }
                        }
                );

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                String eventText = (eventInput.getText()).toString();
                String locText = (locationText.getText()).toString();
                String yearsText = (yearsInput.getText()).toString();
                final int[] succeed = new int[1];
                boolean filled = true;
                if (eventText.trim().length() == 0)
                {
                    eventInput.setError("Please enter an event name");
                    filled = false;
                }
                if (locText.trim().length() == 0)
                {
                    locationText.setError("Please enter a location");
                    filled = false;
                }
                if (yearsText.trim().length() == 0)
                {
                    yearsInput.setError("Please enter an event name");
                    filled = false;
                }
                if (filled){
                    AddEvent addTask = new AddEvent(eventText, locText, yearsText, new AsyncResponse() {
                        @Override
                        public void processFinish(int output) {
                            succeed[0] = output;
                        }
                    });
                    //Check for internet connection first
                    ConnectivityChecker checker = ConnectivityChecker.getInstance(HistoryActivity.this);
                    if (checker.isConnected()){
                        addTask.execute();
                    } else {
                        checker.displayNoConnectionDialog();
                    }
                }

                if(succeed[0] == 1)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
    }

    /**
     * Background Async Task to Add new Event
     * */
    class AddEvent extends AsyncTask<String, String, String> {

        private String event;
        private String location;
        private String years;
        private int success;

        public AsyncResponse delegate = null;

        public AddEvent(String eve, String loc, String y, AsyncResponse delegate)
        {
            event = eve;
            location = loc;
            years = y;
            this.delegate = delegate;
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HistoryActivity.this);
            pDialog.setMessage("Adding Event");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Adding Event
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params;

            params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("location", location));
            params.add(new BasicNameValuePair("event", event));
            params.add(new BasicNameValuePair("year", years));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_add_event,
                        "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                success = json.getInt(TAG_SUCCESS);
                String msg = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    Log.d("Check Event Added", "Success");
                    //save local
                    LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Histories")
                            .build();
                    HistoryDAO historyDAO = database.getHistoryDAO();

                    History history = new History();
                    history.setUsername(username);
                    history.setLocation(location);
                    history.setEvent(event);
                    history.setYear(years);

                    historyDAO.insert(history);
                    database.close();
                } else {
                    Log.d("Check Event Added", msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            delegate.processFinish(success);
            finish();
            startActivity(getIntent());
        }
    }

    /**
     * Background Async Task to delete an event from MySQL
     * */
    class DeleteEvent extends AsyncTask<String, String, String> {
        private History event;
        private boolean finish;

        public DeleteEvent(History eve, boolean end)
        {
            event = eve;
            finish = end;
        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HistoryActivity.this);
            pDialog.setMessage("Deleting event...");
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
            params.add(new BasicNameValuePair("location", event.getLocation()));
            params.add(new BasicNameValuePair("event", event.getEvent()));
            params.add(new BasicNameValuePair("years", event.getYear()));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_delete_event,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                String msg = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    Log.d("Check event Deleted", "Success");

                    //Delete location locally
                    LucidityDatabase database = Room.databaseBuilder(getApplicationContext(), LucidityDatabase.class, "db-Histories")
                            .build();
                    HistoryDAO historyDAO = database.getHistoryDAO();

                    History location = historyDAO.getEvent(username, event.getLocation(), event.getEvent(), event.getYear());
                    historyDAO.delete(location);
                    database.close();
                } else {
                    Log.d("Check event Deleted", msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if(finish) {
                finish();
                startActivity(getIntent());
            }
        }
    }
}
