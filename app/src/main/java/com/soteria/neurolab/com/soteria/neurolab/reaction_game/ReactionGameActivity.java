package com.soteria.neurolab.com.soteria.neurolab.reaction_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.soteria.neurolab.R;
import com.soteria.neurolab.TempLauncherActivity;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Random;

public class ReactionGameActivity extends AppCompatActivity {
    private String patientID;
    private long startTime = -1;
    private int round = 0;
    private int[] results = new int[5];
    protected volatile boolean  isCancelled = false;

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
     * @param view The start button that is tapped to run the game.
     */
    public void runRound(View view) {
        view.setVisibility(View.INVISIBLE);
        isCancelled = false;
        new RandomDelay().execute(this);
        if (((Button) view).getText().equals(getResources().getString(R.string.start_game)))
            ((Button) view).setText(R.string.next_round);
    }

    /**
     * onClick handler to stop the timer and measure reaction time.
     * @param view The circle button that is tapped.
     */
    public void stopTimer(View view) {
        long endTime = System.currentTimeMillis();
        view.setVisibility(View.INVISIBLE);
        if (startTime == -1)
            throw new IllegalStateException(getString(R.string.reactionGame_exception_timer_not_started));

        int result = (int) (endTime - startTime);
        results[round] = result;
        round++;

        ((TextView)findViewById(R.id.information_txt)).setText(
                String.format(Locale.getDefault(),"Round %d\nReaction Time: %dms", round, result));

        if (round < 5) {
            startTime = -1;
            findViewById(R.id.start_button).setVisibility(View.VISIBLE);
        } else
            endGame();
    }

    private void endGame() {
        int avgResult = 0;
        for (int x : results)
            avgResult += x;
        avgResult /= results.length;

        //TODO: Save to database

        Intent launchGame = new Intent(this, TempLauncherActivity.class);
        launchGame.putExtra("PATIENT_ID", patientID);
        launchGame.putExtra("GAME_SCORE", avgResult);
        startActivity(launchGame);
        finish();
    }

    /**
     * Resets the round without counting the result.
     */
    public void stopTimerFoul() {
        startTime = -1;
        ((TextView)findViewById(R.id.information_txt)).setText(R.string.reactionGame_tap_to_early);
        findViewById(R.id.circle_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.start_button).setVisibility(View.VISIBLE);
        isCancelled = true;
    }

    /**
     * Used for detecting when the user taps too early or if they tap but don't tap the button
     * accurately enough.
     * @param view
     */
    public void screenTapped(View view) {
        View circleButton = findViewById(R.id.circle_button);
        if (circleButton.getVisibility() == View.VISIBLE)
            stopTimer(circleButton);
        else if (findViewById(R.id.circle_button).getVisibility() == View.INVISIBLE)
            stopTimerFoul();
    }

    /**
     * An inner class that pauses for a random delay on a separate thread then updates the
     * UI after the delay.
     */
    private class RandomDelay extends AsyncTask<ReactionGameActivity, Integer, Void> {
        private WeakReference<ReactionGameActivity> gameReference;

        @Override
        protected Void doInBackground(ReactionGameActivity... params) {
            gameReference = new WeakReference<>(params[0]); // Weak reference to prevent memory leaks.

            Random random = new Random();
            int delay = random.nextInt(6000) + 1000;
            try {
                Thread.sleep(delay);
                if (!isCancelled) {
                    startTime = System.currentTimeMillis();
                    publishProgress();
                }
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
            if (activity != null && !activity.isFinishing())
                findViewById(R.id.circle_button).setVisibility(View.VISIBLE);
        }
    }
}
