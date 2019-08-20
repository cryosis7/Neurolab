package com.soteria.neurolab.com.soteria.neurolab.reaction_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.soteria.neurolab.R;
import com.soteria.neurolab.TempLauncherActivity;

import java.lang.ref.WeakReference;
import java.util.Random;

public class ReactionGameActivity extends AppCompatActivity {
    private String patientID;
    private long startTime = -1;
    private int round = 0;
    private int[] results = new int[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_game);

        Intent intent = getIntent();
        if (intent.hasExtra("PATIENT_ID"))
            patientID = intent.getStringExtra("PATIENT_ID");
        else
            throw new IllegalArgumentException("Expected Extra 'PATIENT_ID' in Intent - Received none");
    }

    /**
     * onClick handler for a button to run a round of the game.
     *
     * @param view The start button that is tapped to run the game.
     */
    public void runRound(View view) {
        view.setVisibility(View.INVISIBLE);
        new RandomDelay().execute(this);
        if (((Button) view).getText().equals(getResources().getString(R.string.start_game)))
            ((Button) view).setText(R.string.next_round);
    }

    /**
     * onClick handler to stop the timer and measure reaction time.
     *
     * @param view The circle button that is tapped.
     */
    public void stopTimer(View view) {
        long endTime = System.currentTimeMillis();
        if (startTime == -1)
            throw new IllegalStateException("Attempted to measure elapsed time but start time has not been set.");

        int result = (int) (endTime - startTime);
        Toast.makeText(view.getContext(), result + "ms", Toast.LENGTH_SHORT).show();
        results[round] = result;
        round++;

        if (round < 5) {
            view.setVisibility(View.INVISIBLE);
            startTime = -1;
            findViewById(R.id.activity_reaction_game_start_btn).setVisibility(View.VISIBLE);
        } else {
            int avgResult = 0;
            for (int x : results)
                avgResult += x;
            avgResult /= results.length;

            Intent launchGame = new Intent(this, TempLauncherActivity.class);
            launchGame.putExtra("PATIENT_ID", patientID);
            launchGame.putExtra("GAME_SCORE", avgResult);
            startActivity(launchGame);
            finish();
        }
    }

    /**
     * After a random delay, displays the button.
     */
    private class RandomDelay extends AsyncTask<ReactionGameActivity, Integer, Void> {

        private WeakReference<ReactionGameActivity> gameReference;

        @Override
        protected Void doInBackground(ReactionGameActivity... params) {
            gameReference = new WeakReference<>(params[0]); // Weak reference to prevent memory leaks.

            Random random = new Random();
            int delay = random.nextInt(1000) + 2000;
            try {
                Thread.sleep(delay);
                startTime = System.currentTimeMillis();
                publishProgress();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate();
            ReactionGameActivity activity = gameReference.get();

            // Due to weak reference, there is a chance the activity does not exist anymore or is closing down.
            if (activity != null && !activity.isFinishing()) {
                if (findViewById(R.id.circle_button).getVisibility() == View.INVISIBLE) {
                    findViewById(R.id.circle_button).setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
