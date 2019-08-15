package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class tempLauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_launcher);

        Intent intent = getIntent();
        if (intent.hasExtra("gameScore")) {
            int score = intent.getIntExtra("gameScore", -1);
            if (score != -1) ((TextView)findViewById(R.id.score_text)).setText("Score: " + score);
        }
    }

    public void launchReactionGame(View view) {
        Intent launchGame = new Intent(view.getContext(), ReactionGameActivity.class);
        launchGame.putExtra("patientID", "YE37");
        startActivity(launchGame);
    }
}
