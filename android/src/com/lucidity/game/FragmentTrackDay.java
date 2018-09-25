package com.lucidity.game;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FragmentTrackDay extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {
    final String[] labelsDay = {"12 AM", "3 AM", "6 AM", "9 AM", "12 PM", "3 PM", "6 PM", "9 PM", "12 AM"};

    private LineChart chart;
    private long tsStart;

    private String username;
    private String date;

    //used to prevent a task from executing multiple times when a button is tapped multiple times
    private long prevClickTime = 0;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //JSONArray of results
    JSONArray scoresJSON = null;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    // url to get scores for one day
    private static String url_get_scores_day = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/get_scores_day.php";

    private ArrayList<String> timesRaw;
    private ArrayList<Integer> allScores;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_track_day, container, false);

        chart = rootView.findViewById(R.id.linechart);
        chart.setOnChartGestureListener(this);
        chart.setOnChartValueSelectedListener(this);

        Login login = new Login(getActivity());
        username = login.getUsername();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currDate = new Date();
        date = dateFormat.format(currDate);

        Button btnChangeDay = rootView.findViewById(R.id.change_day);
        btnChangeDay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Do nothing if button was recently pressed
                if (SystemClock.elapsedRealtime() - prevClickTime < 1000){
                    return;
                }
                prevClickTime = SystemClock.elapsedRealtime();

                int yearNum = Integer.valueOf(date.substring(0,4));
                int monthNum = Integer.valueOf(date.substring(5,7));
                int dayNum = Integer.valueOf(date.substring(8,10));
                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                        new DateSelector(), yearNum, monthNum - 1, dayNum);
                dialog.show();
            }
        });

        setData(date);

        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setExtraOffsets(5f, 0, 25f, 0);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(48);
        xAxis.setLabelCount(9, true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return labelsDay[(int) value/6];
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(5);
        yAxis.setLabelCount(6, true);

        return rootView;
    }

    @Override
    public void onChartGestureStart(MotionEvent me,
                                    ChartTouchListener.ChartGesture
                                            lastPerformedGesture) {

        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture
                                          lastPerformedGesture) {

        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            // or highlightTouch(null) for callback to onNothingSelected(...)
            chart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2,
                             float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: "
                + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

        CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout, tsStart);
        chart.setMarker(mv);

        Log.i("MIN MAX", "xmin: " + chart.getXChartMin()
                + ", xmax: " + chart.getXChartMax()
                + ", ymin: " + chart.getYChartMin()
                + ", ymax: " + chart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    class DateSelector implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String monthString = Integer.toString(monthOfYear + 1);
            if (monthString.length() == 1) {monthString = "0" + monthString;}
            String dayString = Integer.toString(dayOfMonth);
            if (dayString.length() == 1) {dayString = "0" + dayString;}
            date = year + "-" + monthString + "-" + dayString;
            setData(date);
        }
    }

    private void setData(String date){
        chart.clear();
        TextView tv = rootView.findViewById(R.id.day_label);
        tv.setText(date.substring(5) + "-" + date.substring(0,4));

        timesRaw = new ArrayList<>();
        allScores = new ArrayList<>();

        //Check for internet connection first
        ConnectivityChecker checker = ConnectivityChecker.getInstance(getActivity());
        if (checker.isConnected()){
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
        } else {
            checker.displayNoConnectionDialog();
        }

        ArrayList<Entry> entries = new ArrayList<>();
        long[] tsSeconds = new long[timesRaw.size()];
        int[] tsDiff = new int[timesRaw.size()];
        java.sql.Timestamp ts = java.sql.Timestamp.valueOf(date + " 00:00:00");
        tsStart = ts.getTime() / 1000;
        for (int i = 0; i < timesRaw.size(); i++) {
            ts = java.sql.Timestamp.valueOf(timesRaw.get(i));
            tsSeconds[i] = ts.getTime() / 1000;
            long tsDiffSecond = tsSeconds[i] - tsStart;
            tsDiff[i] = Math.round(tsDiffSecond / 1800f);
        }

        if (!timesRaw.isEmpty()) {
            int score = allScores.get(0);
            float count = 1;
            for (int j = 1; j < timesRaw.size(); j++){
                if(tsDiff[j] == tsDiff[j-1]) {
                    score += allScores.get(j);
                    count++;
                } else{
                    entries.add(new Entry(tsDiff[j-1],score / count));
                    score = allScores.get(j);
                    count = 1;
                }
            }
            entries.add(new Entry(tsDiff[tsDiff.length-1],score / count));
        }

        LineDataSet dataSet;

        //draw an empty chart if there are no entries, make a dummmy entry so chart does not crash
        if (entries.isEmpty()) {
            ArrayList<Entry> temp = new ArrayList<>();
            temp.add(new Entry(0,0));
            dataSet = new LineDataSet(temp, "scores");
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);
            dataSet.setHighlightEnabled(false);
        } else {
            dataSet = new LineDataSet(entries, "Scores");
            dataSet.setDrawValues(false);
            dataSet.setValueFormatter(new DataValueFormatter());
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorLightPurple));
            dataSet.setCircleColorHole(ContextCompat.getColor(getContext(), R.color.colorLightPurple));
            dataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorLightPurple));
        }

        LineData data = new LineData(dataSet);
        chart.setData(data);
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
                        url_get_scores_day, "GET", params);

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
