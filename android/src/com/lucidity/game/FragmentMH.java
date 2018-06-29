package com.lucidity.game;

/**
 * Java class for medical history tab
 */

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FragmentMH extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_mh, container, false);
        rootView.setBackgroundColor(Color.parseColor("#E9E9E9"));

        Button past_mh = rootView.findViewById(R.id.past_mh);
        past_mh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                LayoutInflater layoutInflater
                        = (LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.past_med_dialog, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);

                popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

                Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
                btnDismiss.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }});

                Button btnSave = (Button)popupView.findViewById(R.id.save);
                btnSave.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        // TODO: save the MH information
                        popupWindow.dismiss();
                    }});
            }
        });

        Button current_med = rootView.findViewById(R.id.current_med);
        current_med.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Medications.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
