package com.soteria.neurolab.utilities;

import android.app.Activity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.soteria.neurolab.R;
import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Class used for drawing a graph.
 * Documentation for library: https://weeklycoding.com/mpandroidchart-documentation/
 */
public class ReportGraph {
    private final String TAG = "@ReportGraph";
    private Activity activity;
    private LineChart chart;

    public enum TIME_FRAME {ALL, MONTH, WEEK}

    public ReportGraph(Activity activity) {
        this.activity = activity;
        chart = activity.findViewById(R.id.view_report_report_chart);
        formatChart();
    }

    /**
     * Formats the appearance and interactivity of the chart EXCEPT for the data points;
     * those are handled separately in formatLineData().
     * chart.invalidate() will need to be called to update the graph.
     */
    private void formatChart() {
        chart.setDrawBorders(true);
        chart.setBorderColor(activity.getResources().getColor(R.color.colorDivider));
        chart.setBorderWidth(2);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setScaleYEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateX(500);
        chart.setNoDataText("There is no data available for this time period");
        chart.setNoDataTextColor(activity.getResources().getColor(R.color.colorPrimary));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                float chartRange = chart.getHighestVisibleX() - chart.getLowestVisibleX();
                float monthInMillis = 2592000000f;
                DateFormat df = new SimpleDateFormat((chartRange < monthInMillis) ? "dd-MMM" : " MMM-yyyy ", Locale.ENGLISH);
                return df.format(new Date((long) value));
            }
        });
        xAxis.setGranularity(86400000); // # of milliseconds in a day

        chart.getAxisLeft().setGranularity(1);
        chart.getAxisRight().setGranularity(1);
    }

    /**
     * Will draw a graph of ALL game-session data that matches the patient reference and game id.
     * @param patientID The patients ID
     * @param gameID the Game ID.
     */
    public void drawGraph(String patientID, String gameID) {
        drawGraph(patientID, gameID, TIME_FRAME.ALL);
    }

    /**
     * Will draw a graph of the game-session data that matches the patient reference and game id
     * for the selected time frame.
     * @param patientID The patients ID
     * @param gameID the Game ID.
     * @param timeFrame An enum representing the time frame to display.
     */
    public void drawGraph(String patientID, String gameID, TIME_FRAME timeFrame) {
        DatabaseAccess databaseAccess = new DatabaseAccess(activity.getApplicationContext());
        List<GameSession> gameSessions;
        switch (timeFrame) {
            case ALL:
                gameSessions = databaseAccess.getAllSessions(patientID, gameID);
                break;
            case MONTH:
                gameSessions = databaseAccess.getLastMonthSessions(patientID, gameID);
                break;
            case WEEK:
                gameSessions = databaseAccess.getLastWeekSessions(patientID, gameID);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + timeFrame);
        }
        updateChartData(gameSessions);

        chart.fitScreen();
        chart.invalidate();
    }

    /**
     * Updates the chart with the given data.
     * @param gameSessions
     */
    private void updateChartData(List<GameSession> gameSessions) {
        if (chart == null) {
            chart = activity.findViewById(R.id.view_report_report_chart);
            formatChart();
        }

        List<Entry> entries = new ArrayList<>();
        try {
            for (GameSession session : gameSessions) {
                long x = DateManager.convertToMillis(session.getDate());
                float y = (float) session.getMetrics();
                entries.add(new Entry(x, y));
            }
        }
        catch (ParseException ex) {
            Log.e(TAG, "ParseException was thrown when adding converting dates from String into Date\n" + ex.toString());
        }
        Collections.sort(entries, new EntryXComparator());

        if (entries.size() > 0) {
            chart.setData(new LineData(new LineDataSet(entries, "Game Metrics"))); //TODO: Change Label
            formatLineData();
        }
        else
            chart.clear();
    }

    /**
     * Visually formats the data of the graph for when the data is changed.
     * chart.invalidate() will need to be called to update the graph.
     */
    private void formatLineData() {
        LineDataSet dataSet = (LineDataSet) chart.getLineData().getDataSetByIndex(0);
        dataSet.setColors(activity.getResources().getColor(R.color.colorPrimary));
        dataSet.setCircleColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        dataSet.setHighLightColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        dataSet.setHighlightLineWidth(2);
        dataSet.setLineWidth(3);
        dataSet.setCircleRadius(6);
        dataSet.setCircleHoleRadius(4);
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf(Math.round(value));
            }
        });
        dataSet.setValueTextSize(activity.getResources().getDimension(R.dimen.text_very_small));
    }
}
