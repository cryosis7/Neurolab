package com.soteria.neurolab;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;

import java.util.ArrayList;
import java.util.List;

public class TestGraphActivity extends AppCompatActivity {

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
        for (GameSession session : gameSessions) {
            final String date = session.getDate();

//            entries.add(new Entry(session.getMetrics(), ))
        }
    }
}
