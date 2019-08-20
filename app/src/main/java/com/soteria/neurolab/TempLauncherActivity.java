package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.soteria.neurolab.com.soteria.neurolab.reaction_game.ReactionGameActivity;

public class TempLauncherActivity extends AppCompatActivity {

    private static final String PATIENT_ID = "YE37";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_launcher);

        ((TextView)findViewById(R.id.patient_label)).setText("Patient: " + PATIENT_ID);

        Intent intent = getIntent();
        if (intent.hasExtra("GAME_SCORE")) {
            Integer score = intent.getIntExtra("GAME_SCORE", -1);
            if (score != -1) ((TextView)findViewById(R.id.score_text)).setText("Score: " + score);
        }
    }

    public void launchReactionGame(View view) {
        Intent launchGame = new Intent(view.getContext(), ReactionGameActivity.class);
        launchGame.putExtra("PATIENT_ID", PATIENT_ID);
        startActivity(launchGame);
    }
}
