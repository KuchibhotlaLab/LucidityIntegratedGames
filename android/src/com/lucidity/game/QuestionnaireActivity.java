package com.lucidity.game;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        locationPrompt.setText("Please list some of the locations that " + username + " have lived");

        //mListLocations = new ArrayList<>();
        //mListLocations.add("");


        //adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, mListLocations);

        adapter = new ListItemAdapter(this);
        ListView listView = findViewById(R.id.list_location);
        listView.setAdapter(adapter);


        final EditText editText = findViewById(R.id.addLoc);
        Button addLocBtn = findViewById(R.id.addLocBtn);
        addLocBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //mListLocations.remove("");
                //mListLocations.add(editText.getText().toString());
                adapter.addItem(editText.getText().toString());
                editText.getText().clear();
                //adapter.notifyDataSetChanged();
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
                    mListLocations.remove(position); //or some other task
                    notifyDataSetChanged();
                }
            });

            return view;
        }

        public void addItem(String item){
            mListLocations.add(item);
            notifyDataSetChanged();
        }

    }
}
