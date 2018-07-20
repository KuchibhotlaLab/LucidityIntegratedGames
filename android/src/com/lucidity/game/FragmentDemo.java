package com.lucidity.game;

/**
 * Java class for demographics tab
 */
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class FragmentDemo extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_demo, container, false);

        String date = calendarDateMask(rootView);
        int age = calculateAge(date);

        String[] educations = new String[]{"No schooling completed",
                "Nursery school to 8th grade",
                "Some high school, no diploma",
                "High school graduate or equivalent",
                "Some college credit, no degree",
                "Trade/technical/vocational training",
                "Associate degree",
                "Bachelor’s degree",
                "Master’s degree",
                "Professional degree",
                "Doctorate degree"};

        String[] ethnicities = new String[]{"White",
                "Hispanic or Latino",
                "Black or African American",
                "Native American or American Indian",
                "Asian / Pacific Islander",
                "Other",
                "More than one"};

        String[] marital = new String[]{"Single, never married",
                "Married or domestic partnership",
                "Widowed",
                "Divorced",
                "Separated"};

        maintainChecked(rootView);
        setSpinner(rootView, educations, R.id.education, "Select Your Education");
        setSpinner(rootView, ethnicities, R.id.ethnicity, "Select Your Ethnicity");
        setSpinner(rootView, marital, R.id.marital, "Select Your Marital Status");



        return rootView;
    }


    public void maintainChecked(View rootView){
        final RadioButton rb_male = (RadioButton) rootView.findViewById(R.id.sex_male);
        final RadioButton rb_female = (RadioButton) rootView.findViewById(R.id.sex_female);
        final RadioButton rb_other = (RadioButton) rootView.findViewById(R.id.sex_other);

        /*rb_male.isChecked();
        rb_male.setChecked(false);*/

        rb_male.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rb_female.setChecked(false);
                rb_other.setChecked(false);
            }
        });

        rb_female.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rb_male.setChecked(false);
                rb_other.setChecked(false);
            }
        });

        rb_other.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rb_male.setChecked(false);
                rb_female.setChecked(false);
            }
        });

    }

    public void setSpinner(View rootView, String[] content, int id, String prompt){
        final Spinner spinner = rootView.findViewById(id);

        //sets background color of the fragment page
        rootView.setBackgroundColor(Color.parseColor("#E9E9E9"));

        SpinnerAdapter adapter = new SpinnerAdapter(getActivity(), android.R.layout.simple_list_item_1);
        adapter.addAll(content);
        adapter.add(prompt);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    public String calendarDateMask(View rootView) {
        //displays the birthday of the patient in formate dd/mm/yyyy
        //based on code in page
        //https://stackoverflow.com/questions/16889502/how-to-mask-an-edittext-to-show-the-dd-mm-yyyy-date-format

        final EditText date;
        date = (EditText)rootView.findViewById(R.id.birth_date);

        TextWatcher tw = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1920) ? 1920 : (year > 2018) ? 2018 : year;
                        cal.set(Calendar.YEAR, year);
                        //first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    date.setText(current);
                    date.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };


        date.addTextChangedListener(tw);
        return date.toString();

    }

    int calculateAge(String birth_date){
        return 0;
    }


}
