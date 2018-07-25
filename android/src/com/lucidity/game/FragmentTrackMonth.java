package com.lucidity.game;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private ArrayList<Integer> y;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_track_month, container, false);

        chart = rootView.findViewById(R.id.barchartMonth);

        Login login = new Login(getActivity());
        username = login.getUsername();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currDate = new Date();
        date = dateFormat.format(currDate);

        ArrayList<BarEntry> entries = new ArrayList<>();
        float x[] = {0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f,
                13f, 14f, 15f, 16f, 17f, 18f, 19f, 20f, 21f, 22f, 23f, 24f, 25f, 26f, 27f, 28f, 29f, 30f};
        float y[] = {5.4f, 7, 2.1f, 4.3f, 5, 3, 3.3f, 0, 0, 0.1f, 0, 5, 3, 3.3f,
                5.4f, 7, 2.1f, 4.3f, 7, 8, 1.4f, 5.4f, 0, 2.1f, 0, 5, 4.9f, 3.3f, 4, 5, 2};
        for (int i = 0; i < 31; i++) {
            entries.add(new BarEntry (x[i], y[i]));
        }
        BarDataSet dataSet = new BarDataSet(entries, "Scores");
        dataSet.setDrawValues(false);
        dataSet.setColor(ContextCompat.getColor(getContext(),R.color.colorLightPurple));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return Integer.toString((int) value + 1);
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(5);
        yAxis.setLabelCount(6, true);

        return rootView;
    }
}
