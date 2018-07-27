package com.lucidity.game;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class DataValueFormatter implements IValueFormatter {

    private DecimalFormat oneDecimalFormat;

    public DataValueFormatter() {
        oneDecimalFormat = new DecimalFormat("##0.0");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        if (value != value) {
            return "";
        } else {
            return oneDecimalFormat.format(value);
        }
    }
}
