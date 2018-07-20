package com.lucidity.game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class TrackingActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

    final String[] labelsDay = {"12:00 AM", "", "6:00 AM", "",
            "12:00 PM", "", "6:00 PM", "", "12:00 AM"};

    private LineChart chart;
    private long tsStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // To make full screen layout
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tracking);

        chart = (LineChart) findViewById(R.id.linechart);
        chart.setOnChartGestureListener(this);
        chart.setOnChartValueSelectedListener(this);

        ArrayList<Entry> entries = new ArrayList<>();
        String[] timesRaw = {"2018-07-13 05:03:02", "2018-07-13 7:33:50",
                "2018-07-13 12:14:23", "2018-07-13 16:26:14", "2018-07-13 22:59:59"};
        long[] tsSeconds = new long[timesRaw.length];
        long[] x = new long[timesRaw.length];
        java.sql.Timestamp ts = java.sql.Timestamp.valueOf(timesRaw[0].substring(0, 10) + " 00:00:00");
        tsStart = ts.getTime() / 1000;
        for (int i = 0; i < timesRaw.length; i++) {
            ts = java.sql.Timestamp.valueOf(timesRaw[i]);
            tsSeconds[i] = ts.getTime() / 1000;
            x[i] = tsSeconds[i] - tsStart;
            System.out.println(x[i]);
        }
        int y[] = {2, 5, 10, 6, 11};
        for (int i = 0; i < 5; i++) {
            entries.add(new Entry(x[i], y[i]));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Scores");
        dataSet.setDrawValues(false);
        LineData data = new LineData(dataSet);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(86400);
        xAxis.setLabelCount(9, true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return labelsDay[(int) value/10800];
            }
        });

        chart.getLegend().setEnabled(false);
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

        CustomMarkerView mv = new CustomMarkerView(getApplicationContext(), R.layout.custom_marker_view_layout, tsStart);
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
}
