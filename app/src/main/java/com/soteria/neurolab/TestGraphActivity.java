package com.soteria.neurolab;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;
import com.soteria.neurolab.utilities.DateManager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestGraphActivity extends AppCompatActivity {

    private static final String TAG = "Scott_Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_graph);
        graphStuff();
    }

    /**
     * https://weeklycoding.com/mpandroidchart-documentation/
     */
    private void graphStuff() {
        DatabaseAccess databaseAccess = new DatabaseAccess(getApplicationContext());
        final List<GameSession> gameSessions = databaseAccess.getSessions("1", "1");
        LineChart chart = findViewById(R.id.reportChart);

        List<Entry> entries = new ArrayList<>();
        try {
            for (GameSession session : gameSessions) {
//                Date date = DateManager.convertToCalendar(session.getDate());
                if (DateManager.convertToCalendar(session.getDate()).get(Calendar.DAY_OF_MONTH) == 20)
                    Log.i(TAG, "Storing Date: " + DateManager.convertToMillis(session.getDate()));
                long x = DateManager.convertToMillis(session.getDate());
                float y = (float) session.getMetrics();
                entries.add(new Entry(x, y));
            }
        }
        catch (ParseException ex) {
           Log.e(TAG, "ParseException was thrown when adding converting dates from String into Date\n" + ex.toString());
        }

        LineDataSet dataSet = new LineDataSet(entries, "Reaction Game Metrics");
        chart.setData(new LineData(dataSet));
        formatChart(chart);
        chart.invalidate();
    }

    private void formatChart(LineChart chart) {
        chart.setDrawBorders(true);
        chart.setBorderColor(getResources().getColor(R.color.colorDivider));
        chart.setBorderWidth(2);
//        chart.getLineData().setValueTextSize(getResources().getDimension(R.dimen.text_body));
        chart.setDoubleTapToZoomEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateX(500);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DateManager.getShortDateString(new Date((long) value));
            }
        });
        xAxis.setGranularity(86400000); // # of milliseconds in a day

        chart.getAxisLeft().setGranularity(1);
        chart.getAxisRight().setGranularity(1);

        LineDataSet dataSet = (LineDataSet) chart.getLineData().getDataSetByIndex(0);
        dataSet.setColors(getResources().getColor(R.color.colorPrimary));
        dataSet.setCircleColor(getResources().getColor(R.color.colorPrimaryDark));
        dataSet.setHighLightColor(getResources().getColor(R.color.colorPrimaryDark));
        dataSet.setLineWidth(3);
        dataSet.setCircleRadius(6);
        dataSet.setCircleHoleRadius(4);
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf(Math.round(value));
            }
        });
    }
}
