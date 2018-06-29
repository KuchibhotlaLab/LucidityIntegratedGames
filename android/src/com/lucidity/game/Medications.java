package com.lucidity.game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Medications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medications);


        final EditText editText = (EditText) findViewById(R.id.med_names);
        final Button addButton = (Button) findViewById(R.id.add_med);
        final ListView listView = (ListView) findViewById(R.id.listView);

        //TODO: stores the list of medications should be list loaded from database
        //TODO: listItems is the arraylist you want to store
        final ArrayList listItems = new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);
        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                listItems.add(editText.getText().toString());
                editText.setText("");

                adapter.notifyDataSetChanged();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {

                //TODO: display the medications to delete
                final String itemValue = (String) listView.getItemAtPosition(position);
                final String value=((TextView)v).getText().toString();


                LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.delete_dialog, null);


                //gets the string/medication that user clicked on
                //TODO: display the string in the popupview
                final TextView textViewToChange = (TextView) popupView.findViewById(R.id.med_to_delete);
                textViewToChange.setText(value);

                //inflate the delte dialoge
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                //when the cancel button is pressed, do nothing
                Button btnDismiss = (Button)popupView.findViewById(R.id.cancel);
                btnDismiss.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }});


                //when the delete button is pressed, remove medication
                Button btnSave = (Button)popupView.findViewById(R.id.delete);
                btnSave.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        // TODO: save the MH information
                        adapter.remove(itemValue);
                        popupWindow.dismiss();
                    }});
            }
        });
    }

}
