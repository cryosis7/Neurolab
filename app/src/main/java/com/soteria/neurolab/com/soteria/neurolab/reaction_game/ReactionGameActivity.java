package com.soteria.neurolab.com.soteria.neurolab.reaction_game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.soteria.neurolab.R;
import com.soteria.neurolab.tempLauncherActivity;

import java.lang.ref.WeakReference;
import java.util.Random;

public class ReactionGameActivity extends AppCompatActivity {
    private String patientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_game);

        Intent intent = getIntent();
        if (intent.hasExtra("PATIENT_ID"))
            patientID = intent.getStringExtra("PATIENT_ID");
        else throw new IllegalArgumentException("Expected Extra 'PATIENT_ID' in Intent - Received none");
    }

    /**
     * onClick handler for a button to start the game.
     * @param view The button that was clicked.
     */
    public void startGame(View view) {
        ((ViewGroup)view.getParent()).removeView(view);
        new GameInstance().execute(this);
    }

    private class GameInstance extends AsyncTask<ReactionGameActivity, Integer, Integer> {

        private WeakReference<ReactionGameActivity> gameReference;

        @Override
        protected Integer doInBackground(ReactionGameActivity... params) {
            gameReference = new WeakReference<>(params[0]);

            Random random = new Random();
            for (int round = 0; round < 5; round++) {
                int delay = random.nextInt(1000) + 2000;
                try {
                    Thread.sleep(delay);
                    publishProgress( round + 1, delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return 8008135;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            ReactionGameActivity activity = gameReference.get();

            if (activity == null || activity.isFinishing())
                return;
            else {
                if (findViewById(R.id.circle_button).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.circle_button).setVisibility(View.INVISIBLE);
                }
                else {
                    findViewById(R.id.circle_button).setVisibility(View.VISIBLE);
                }
            }
            Toast.makeText(activity, values[0]  + " - " + values[1] + "ms", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Integer score) {
            super.onPostExecute(score);

            ReactionGameActivity activity = gameReference.get();
            if (activity != null) {
                Intent launchGame = new Intent(activity, tempLauncherActivity.class);
                launchGame.putExtra("GAME_SCORE", score);
                activity.startActivity(launchGame);
                activity.finish();
            }

        }
    }
}
