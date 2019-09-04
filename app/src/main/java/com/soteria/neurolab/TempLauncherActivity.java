package com.soteria.neurolab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.soteria.neurolab.database.DatabaseAccess;
import java.util.Random;

public class TempLauncherActivity extends AppCompatActivity {

    private int patientID;
    private String patientReference = "SC05";
    public static final String TAG = "@TempLauncherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_launcher);

        Intent intent = getIntent();
        patientID = intent.getIntExtra("PATIENT_ID", 1);
        ((TextView) findViewById(R.id.patient_label)).setText("Patient: " + patientID);

        if (intent.hasExtra("GAME_SCORE")) {
            int score = intent.getIntExtra("GAME_SCORE", -1);
            if (score != -1)
                ((TextView) findViewById(R.id.score_text)).setText("Score: " + score);
        }
    }

    public void launchReactionGame(View view) {
        Intent gameIntent = new Intent(view.getContext(), ReactionGameActivity.class);
        gameIntent.putExtra("PATIENT_ID", patientID);
        startActivity(gameIntent);
    }

    public void launchMainScreen(View view) {
        startActivity(new Intent(view.getContext(), SearchCreateDeleteActivity.class));
    }

    public void launchViewReportPage(View view) {
        Intent launchViewReport = new Intent(view.getContext(), ViewReportActivity.class);
        launchViewReport.putExtra("PATIENT_REFERENCE", patientReference);
        startActivity(launchViewReport);
    }
    
    public void launchTestGraph(View view) {
        startActivity(new Intent(view.getContext(), TestGraphActivity.class));
    }

    public void createRandomGameData(View view) {
        Random random = new Random();

        DatabaseAccess db = new DatabaseAccess(getApplicationContext());
        db.deleteAllSessions("1", "1");

//        for (int i = 0; i < 75; i++) {
//            Calendar cal = Calendar.getInstance();
//            cal.set(2019, random.nextInt(12) + 1, random.nextInt(30) + 1);
//            db.createSession(new GameSession(1, 1, random.nextInt(200) + 300, cal.getTime()));
//        }
    }
}
