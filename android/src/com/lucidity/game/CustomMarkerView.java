package com.lucidity.game;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    private long timeStart;
    private DateFormat dateFormat;
    private  DecimalFormat oneDecimalFormat;

    public CustomMarkerView (Context context, int layoutResource, long tsStart) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
        timeStart = tsStart;
        dateFormat = new SimpleDateFormat("h:mm");
        oneDecimalFormat = new DecimalFormat("##0.0");
    }


    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        long ts = timeStart + (long)e.getX()*1800;
        Date date = new Date();
        date.setTime(ts*1000);
        String datestr = dateFormat.format(date);

        tvContent.setText("Time:  " + datestr + "\nScore: " + oneDecimalFormat.format(e.getY()));

        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }
}