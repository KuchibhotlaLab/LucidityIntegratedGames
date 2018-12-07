package com.lucidity.game;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    //Stores the username of the user
    private String username;
    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

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

            final LinearLayout listItemInnerList = view.findViewById(R.id.location_event_list);
            final Button addEventButton = view.findViewById(R.id.addEventBtn);

            ArrayList<History> currLocHistories = mListEvents.get(position);
            for (History e : currLocHistories) {
                View eventView = inflater.inflate(R.layout.location_event_listitem, null);
                TextView eventText = eventView.findViewById(R.id.event_details);
                eventText.setText(e.getEvent() + "         " + e.getYear());
                ImageButton eventDel = eventView.findViewById(R.id.delete_event);
                eventDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("delete event");
                    }
                });
                listItemInnerList.addView(eventView);
            }
            listItemInnerList.setVisibility(View.GONE);

            addEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("add event");
                }
            });
            addEventButton.setVisibility(View.GONE);


            listItemText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (!listItemInnerList.isShown()) {
                        listItemInnerList.setVisibility(View.VISIBLE);
                        addEventButton.setVisibility(View.VISIBLE);
                    } else {
                        listItemInnerList.setVisibility(View.GONE);
                        addEventButton.setVisibility(View.GONE);
                    }
                }
            });

            //Handle buttons and add onClickListeners
            ImageButton deleteBtn = view.findViewById(R.id.delete_loc);
            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    System.out.println("delete event");
                }
            });

            return view;
        }
    }
}
