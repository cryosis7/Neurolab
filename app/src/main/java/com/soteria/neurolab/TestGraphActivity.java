package com.soteria.neurolab;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;
import com.soteria.neurolab.utilities.DateManager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
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

    private void graphStuff() {
        DatabaseAccess databaseAccess = new DatabaseAccess(getApplicationContext());
        final List<GameSession> gameSessions = databaseAccess.getSessions("1", "1");
        LineChart chart = findViewById(R.id.reportChart);

        List<Entry> entries = new ArrayList<>();
        try {
            for (GameSession session : gameSessions) {
                Date date = DateManager.convertToDate(session.getDate());
                entries.add(new Entry(date.getTime(), (float) session.getMetrics()));
            }
        }
        catch (ParseException ex) {
           Log.e(TAG, "ParseException was thrown when adding converting dates from String into Date\n" + ex.toString());
        }

        LineDataSet dataSet = new LineDataSet(entries, "Reaction Game Metrics");
        dataSet.setColor(R.color.colorBlack);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        formatChart(chart);
        chart.invalidate();
    }

    private void formatChart(LineChart chart) {
        chart.setDrawBorders(true);
        chart.setBorderColor(getResources().getColor(R.color.colorPrimaryDark));
        chart.setBorderWidth(2);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(false);
//        xAxis.setdr
    }
}
