package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;

import java.util.List;

public class TempLauncherActivity extends AppCompatActivity {

    private String patientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_launcher);

        Intent intent = getIntent();
        if (intent.hasExtra("PATIENT_ID")) {
            patientID = intent.getStringExtra("PATIENT_ID");
            ((TextView) findViewById(R.id.patient_label)).setText("Patient: " + patientID);

            if (intent.hasExtra("GAME_SCORE")) {
                int score = intent.getIntExtra("GAME_SCORE", -1);
                if (score != -1)
                    ((TextView) findViewById(R.id.score_text)).setText("Score: " + score);
            }
        }
    }

    public void launchReactionGame(View view) {
        Intent launchGame = new Intent(view.getContext(), ReactionGameActivity.class);
        launchGame.putExtra("PATIENT_ID", "1234");
        startActivity(launchGame);
    }

    public void launchMainScreen(View view) {
        startActivity(new Intent(view.getContext(), SearchCreateDeleteActivity.class));
    }

    public void launchViewReportPage(View view) {
        Intent launchViewReport = new Intent(view.getContext(), ViewReportActivity.class);
        launchViewReport.putExtra("PATIENT_REFERENCE", "SC05");
        startActivity(launchViewReport);
    }
    
    public void launchTestGraph(View view) {
        startActivity(new Intent(view.getContext(), TestGraphActivity.class));
    }
}
