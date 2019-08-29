package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.soteria.neurolab.Models.GameAssignment;
import com.soteria.neurolab.Models.Patient;
import com.soteria.neurolab.database.DatabaseAccess;

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
        final List<Patient> allPatients = databaseAccess.getAllPatients();
        final List<GameAssignment> gameAssignments = databaseAccess.getAssignments(allPatients.get(0));
        databaseAccess.
    }
}
