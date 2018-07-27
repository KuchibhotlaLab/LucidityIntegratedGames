package com.lucidity.game;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FragmentTrackMonth extends Fragment {

    private BarChart chart;

    private String username;
    private String date;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //JSONArray of results
    JSONArray scoresJSON = null;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    // url to get scores for one month
    private static String url_get_scores_month = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/get_scores_month.php";

    private ArrayList<String> timesRaw;
    private ArrayList<Integer> allScores;

    private int numDays;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_track_month, container, false);

        chart = rootView.findViewById(R.id.barchartMonth);

        Login login = new Login(getActivity());
        username = login.getUsername();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date currDate = new Date();
        date = dateFormat.format(currDate);

        Button btnChangeMonth = rootView.findViewById(R.id.change_month);
        btnChangeMonth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pickMonth(date);
            }
        });

        setData(date);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(5);
        yAxis.setLabelCount(6, true);

        return rootView;
    }

    private void pickMonth(String date) {

        final LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.pick_month_dialog, null);
        final NumberPicker monthpicker = linearLayout.findViewById(R.id.pick_month);
        final NumberPicker yearpicker = linearLayout.findViewById(R.id.pick_year);

        int yearVal = Integer.valueOf((date.substring(0,4)));

        monthpicker.setMinValue(1);
        monthpicker.setMaxValue(12);
        monthpicker.setValue(Integer.valueOf(date.substring(5,7)));
        yearpicker.setMinValue(yearVal - 50);
        yearpicker.setMaxValue(yearVal + 50);
        yearpicker.setValue(yearVal);

        final AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .setView(linearLayout)
                .setCancelable(true)
                .create();
        builder.show();
        //Setting up OnClickListener on positive button of AlertDialog
        builder.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chosenDate;
                if (monthpicker.getValue() < 10) {
                    chosenDate = yearpicker.getValue() + "-0" + monthpicker.getValue();
                } else {
                    chosenDate = yearpicker.getValue() + "-" + monthpicker.getValue();
                }
                setData(chosenDate);
                builder.dismiss();
            }
        });
    }

    private void setData(String d){
        date = d;
        chart.clear();
        TextView tv = rootView.findViewById(R.id.month_label);
        int monthVal = Integer.valueOf(date.substring(5,7)) - 1;
        int yearVal = Integer.valueOf((date.substring(0,4)));
        tv.setText(new DateFormatSymbols().getMonths()[monthVal] + " " + yearVal);

        timesRaw = new ArrayList<>();
        allScores = new ArrayList<>();

        GetScores task = new GetScores(date);
        task.execute();
        //wait for task to finish
        try {
            task.get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Calendar calendar = new GregorianCalendar(yearVal, monthVal, 1);
        numDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        ArrayList<BarEntry> entries = new ArrayList<>();
        float x[] = new float[numDays];
        float y[] = new float[numDays];
        int j = 0;
        int dayVal = -1;
        if (!timesRaw.isEmpty()) {
            dayVal = Integer.valueOf(timesRaw.get(j).substring(8,10));
        }
        for (int i = 0; i < numDays; i++) {
            x[i] = (float) i;
            float count = 0f;
            float totalScore = 0f;
            while (dayVal == i + 1) {
                totalScore += allScores.get(j);
                count += 1;
                j++;
                if (j < timesRaw.size()) {
                    dayVal = Integer.valueOf(timesRaw.get(j).substring(8, 10));
                } else {
                    dayVal = -1;
                }
            }
            y[i] = totalScore/count;
            entries.add(new BarEntry (x[i], y[i]));
        }
        BarDataSet dataSet = new BarDataSet(entries, "Scores");
        dataSet.setValueFormatter(new DataValueFormatter());
        dataSet.setColor(ContextCompat.getColor(getContext(),R.color.colorLightPurple));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(numDays - 0.5f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                if (value >= 0 && value <= numDays - 1) {
                    return Integer.toString((int) value + 1);
                } else {
                    return "";
                }
            }
        });
    }

    /**
     * Background Async Task to get the scores for a particular day
     * */
    class GetScores extends AsyncTask<String, String, String> {

        private String date;

        public GetScores(String d)
        {
            date = d;
        }

        /**
         * Getting everything from MySQL
         * */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("date", date));

                // getting pictures and tags from web
                JSONObject json = jsonParser.makeHttpRequest(
                        url_get_scores_month, "GET", params);

                // check your log for json response
                Log.d("get scores", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    // Get array of scores
                    scoresJSON = json.getJSONArray("scores");

                    // loop through pictures found
                    for (int i = 0; i < scoresJSON.length(); i++) {
                        JSONObject scoreObject = scoresJSON.getJSONObject(i);

                        // add pictures and tags to Arraylists in order
                        timesRaw.add(scoreObject.getString("datetime"));
                        allScores.add(scoreObject.getInt("score"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "complete";
        }
    }
}
