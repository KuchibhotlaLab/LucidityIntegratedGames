package com.lucidity.game;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddWearableActivity extends AppCompatActivity {

    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    private int buttonPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wearable);


        final FloatingActionButton btn_add = findViewById(R.id.addWearable);
        ArrayList<String> buttons = new ArrayList<String>();

        btn_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();


                LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.device_dialog, null);


                //TODO: edit text able to accept input
                final EditText device = popupView.findViewById(R.id.device_to_add);

                //inflate the add device dialog
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);




                Button btnDismiss = (Button)popupView.findViewById(R.id.cancel_device);
                btnDismiss.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }});

                Button btnSave = (Button)popupView.findViewById(R.id.add_device);
                btnSave.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        // TODO: save the device information

                        //TODO: get input string and put into cutomized button
                        //TODO: set absolute position of button/put in tableview
                        String deviceName = device.getText().toString();
                        //Toast.makeText(getBaseContext(), deviceName, Toast.LENGTH_LONG).show();


                        Button btn_wearable = new Button(getApplicationContext());
                        btn_wearable.setText(deviceName);
                        //btn_wearable.setBackgroundColor(0xff4081);

                        TableLayout tl = findViewById(R.id.tb_wearable);
                        TableLayout.LayoutParams tp =
                                new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        TableLayout.LayoutParams.WRAP_CONTENT);

                        //TODO: add buttons to table by row- index the buttons
                        TableRow tr = new TableRow(getBaseContext());
                        /*for (int row = 0; row < 4; row++) {
                            TableRow currentRow = new TableRow(context);
                            for (int button = 0; button < 4; button++) {
                                Button currentButton = new Button(context);
                                // you could initialize them here
                                currentButton.setOnClickListener(listener);
                                // you can store them
                                buttonArray[row][button] = currentButton;
                                // and you have to add them to the TableRow
                                currentRow.addView(currentButton);
                            }
                            // a new row has been constructed -> add to table
                            table.addView(currentRow);
                        }*/

                        //add margin between different buttons
                        //TODO: distinguish the first button
                        tp.setMargins(0, 100, 0, 0);
                        btn_wearable.setLayoutParams(tp);

                        //add button to table
                        tl.addView(btn_wearable, tp);


                        popupWindow.dismiss();
                    }
                });
            }});
    }
}