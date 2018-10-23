package com.lucidity.game;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FragmentTrackWeek extends Fragment implements AdapterView.OnItemSelectedListener {

    final int NUMDAYS = 7;
    private BarChart chart;

    private String username;

    //gets the current games to display data from
    Spinner spinner;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //JSONArray of results
    JSONArray scoresJSON = null;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    // url to get scores for one week
    private static String url_get_scores_week = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/get_scores_week.php";

    private ArrayList<String> timesRaw;
    private ArrayList<Integer> allScores;

    private String[] dates;

    View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_track_week, container, false);

        chart = rootView.findViewById(R.id.barchartWeek);

        Login login = new Login(getActivity());
        username = login.getUsername();
        dates = new String[7];
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            dates[6-i] = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, -1);
        }

        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_item_tracking, Arrays.asList(getResources().getStringArray(R.array.tracking_tests))) {

            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);

                return view;
            }


            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                Typeface font = Typeface.createFromAsset(getContext().getAssets(),"data/Kayak-Sans-Regular.ttf");
                view.setTypeface(font);
                view.setTextSize(18);
                view.setBackgroundResource(R.color.colorLightPurple);

                return view;
            }
        };

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        setData(dates, spinner.getSelectedItemPosition());

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(6.5f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value >= 0 && value <= 6) {
                    return dates[(int) value].substring(5);
                } else {
                    return "";
                }
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(5);
        yAxis.setLabelCount(6, true);

        return rootView;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        setData(dates, pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void setData(String[] d, int pos){
        chart.clear();

        timesRaw = new ArrayList<>();
        allScores = new ArrayList<>();

        //Check for internet connection first
        ConnectivityChecker checker = ConnectivityChecker.getInstance(getActivity());
        if (checker.isConnected()){
            GetScores task = new GetScores(d, pos);
            task.execute();
        } else {
            checker.displayNoConnectionDialog();
        }
    }

    private void updateChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        float x[] = new float[NUMDAYS];
        float y[] = new float[NUMDAYS];
        int j = 0;
        String dateCounter = "";
        if (!timesRaw.isEmpty()) {
            dateCounter = timesRaw.get(j).substring(0,10);
        }
        for (int i = 0; i < NUMDAYS; i++) {
            x[i] = (float) i;
            float count = 0f;
            float totalScore = 0f;
            while (dateCounter.equals(dates[i])) {
                totalScore += allScores.get(j);
                count += 1;
                j++;
                if (j < timesRaw.size()) {
                    dateCounter = timesRaw.get(j).substring(0,10);
                } else {
                    dateCounter = "";
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
        chart.invalidate();
    }

    /**
     * Background Async Task to get the scores for the last 7 days
     * */
    class GetScores extends AsyncTask<String, String, String> {

        private String[] dates;
        private int pos;
        private ProgressDialog pDialog;

        public GetScores(String[] d, int p)
        {
            dates = d;
            pos = p;
        }

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Getting scores data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
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
                for (int i = 0; i < 7; i++) {
                    params.add(new BasicNameValuePair("date" + (i+1), dates[i]));
                }
                params.add(new BasicNameValuePair("pos", String.valueOf(pos)));

                // getting pictures and tags from web
                JSONObject json = jsonParser.makeHttpRequest(
                        url_get_scores_week, "GET", params);

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

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            updateChart();
        }
    }
}
