package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ReactionGameActivity extends AppCompatActivity {
    private int patientID;
    private ReactionTimer reactionTimer;
    private static long startTime = -1;
    private int round = 0;
    private int[] results = new int[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.reactionGame_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_reaction_game);


        Intent intent = getIntent();
        patientID = intent.getIntExtra("PATIENT_ID", -1);
        if (patientID == -1)
            throw new IllegalArgumentException("Expected Extra 'PATIENT_ID' in Intent - Received none");
    }

    /**
     * onClick handler for a button to run a round of the game.
     * The start game should disappear, the information text will be cleared and the asyncTask that
     * starts the reaction timer will be started.
     *
     * @param startButton The start button that is tapped to run the game.
     */
    public void runRound(View startButton) {
        startButton.setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.reaction_game_information_txt)).setText("");
        reactionTimer = new ReactionTimer();
        reactionTimer.execute(this);

        if (((Button) startButton).getText().equals(getResources().getString(R.string.start_game)))
            ((Button) startButton).setText(R.string.next_round);
    }

    /**
     * onClick handler to stop the timer and measure reaction time.
     * If it's the last round, it will end the game - endGame()
     * @param circleButton The circle button that is tapped.
     */
    public void stopTimer(View circleButton) {
        long endTime = System.currentTimeMillis();
        circleButton.setVisibility(View.INVISIBLE);
        if (startTime == -1)
            throw new IllegalStateException(getString(R.string.reactionGame_exception_timer_not_started));

        int result = (int) (endTime - startTime);
        results[round] = result;
        if (result < 250) {
            stopTimerFoul();
            return;
        }

        ((TextView) findViewById(R.id.reaction_game_information_txt)).setText(
                String.format(Locale.getDefault(), "Round %d\nReaction Time: %dms", round + 1, result));

        round++;
        if (round < 5) {
            startTime = -1;
            findViewById(R.id.reaction_game_start_button).setVisibility(View.VISIBLE);
        } else
            endGame();
    }

    /**
     * Averages the results and will save it to the database.
     * It then launches the next activity. bundles two extras into the intent:
     * PATIENT_ID & GAME_SCORE (An int value representing the gameSession metric)
     */
    private void endGame() {
        int avgResult = 0;
        for (int x : results)
            avgResult += x;
        avgResult /= results.length;

        int gameID = 1; //TODO: Change to actual game ID
        GameSession gameSession = new GameSession(patientID, gameID, avgResult, new Date());
        DatabaseAccess db = new DatabaseAccess(this);
        db.createSession(gameSession);

        //TODO change view patient details to the score screen once implemented
        /*Intent launchGame = new Intent(this, ViewPatientDetails.class);
        launchGame.putExtra("PATIENT_ID", patientID);
        launchGame.putExtra("GAME_SCORE", avgResult);
        startActivity(launchGame);
        */
        onBackPressed();
        finish();
    }

    /**
     * Resets the round without counting the result.
     */
    public void stopTimerFoul() {
        startTime = -1;
        ((TextView) findViewById(R.id.reaction_game_information_txt)).setText(R.string.reactionGame_tap_to_early);
        findViewById(R.id.reaction_game_circle_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.reaction_game_start_button).setVisibility(View.VISIBLE);
        reactionTimer.cancel(true);
    }

    /**
     * Used for detecting when the user taps too early or if they do tap but don't tap exactly
     * on the button.
     * Handles three cases: Foul (Tap too early); inaccurate tap on start button and inaccurate tap
     * on circle button.
     *
     * @param view
     */
    public void screenTapped(View view) {
        View circleButton = findViewById(R.id.reaction_game_circle_button);
        View startButton = findViewById(R.id.reaction_game_start_button);
        if (circleButton.getVisibility() == View.VISIBLE)
            stopTimer(circleButton);
        else if (startButton.getVisibility() == View.INVISIBLE)
            stopTimerFoul();
        else if (startButton.getVisibility() == View.VISIBLE)
            runRound(startButton);
    }

    /**
     * An inner class that pauses for a random delay on a separate thread then updates the
     * UI after the delay.
     */
    private static class ReactionTimer extends AsyncTask<ReactionGameActivity, Void, Void> {
        private WeakReference<ReactionGameActivity> gameReference;

        @Override
        protected Void doInBackground(ReactionGameActivity... params) {
            gameReference = new WeakReference<>(params[0]); // Weak reference to prevent memory leaks.

            Random random = new Random();
            int delay = random.nextInt(6000) + 1000;
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
        protected void onProgressUpdate(Void... progress) {
            super.onProgressUpdate();
            ReactionGameActivity activity = gameReference.get();

            // Due to weak reference, there is a chance the activity does not exist anymore or is closing down.
            if (activity != null && !activity.isFinishing())
                activity.findViewById(R.id.reaction_game_circle_button).setVisibility(View.VISIBLE);
        }
    }
}
