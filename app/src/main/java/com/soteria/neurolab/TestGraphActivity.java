package com.soteria.neurolab;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.soteria.neurolab.utilities.ReportGraph;

public class TestGraphActivity extends AppCompatActivity {
    ReportGraph reportGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_graph);

        reportGraph = new ReportGraph(this);
        reportGraph.drawGraph("2", "1");
    }

    /**
     * Gets the session data for the last month from the database and updates the graph.
     * TODO: Update for dynamic patient and game ID
     * @param view
     */
    public void getLastMonthSessions(View view) {
        reportGraph.drawGraph("2", "1", ReportGraph.TIME_FRAME.MONTH);
    }

    /**
     * Gets the session data for the last year from the database and updates the graph.
     * TODO: Update for dynamic patient and game ID
     * @param view
     */
    public void getLastYearSessions(View view) {
        reportGraph.drawGraph("2", "1", ReportGraph.TIME_FRAME.YEAR);
    }
}
