package com.soteria.neurolab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;

import java.util.Calendar;
import java.util.Random;

public class TempLauncherActivity extends AppCompatActivity {

    private String patientID;
    public static final String TAG = "Scott_Debug_Launcher";

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

    public void createRandomGameData(View view) {
        Random random = new Random();

        DatabaseAccess db = new DatabaseAccess(getApplicationContext());
        db.deleteAllSessions("2", "1");

        for (int i = 0; i < 250; i++) {
            Calendar cal = Calendar.getInstance();
            cal.set(random.nextInt(2) + 2018, random.nextInt(12) + 1, random.nextInt(30) + 1);
            db.createSession(new GameSession(2, 1, random.nextInt(25) + 75, cal.getTime()));
        }
    }
}
