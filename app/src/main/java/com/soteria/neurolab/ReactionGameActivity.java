package com.soteria.neurolab;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * This class contains the functionality for the reaction time game.
 * The game will initiate with a very short instruction, telling the user to tap the button
 * as soon as they see it. When the 'start' button is pressed, the screen is cleared and
 * a yellow button will appear after a random duration.
 * Once the user taps the button, another 'ready' button appears and this cycle will repeat for 5
 * rounds. When the 5 rounds are up, the average reaction speed is displayed to the user and stored
 * in the database.
 *
 * @author Scott Curtis
 */
public class ReactionGameActivity extends AppCompatActivity {
    private int patientID;
    private ReactionTimer reactionTimer;
    private static long startTime = -1;
    private int round = 0;
    private List<Integer> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.title_reaction_time);
        getSupportActionBar().setHomeButtonEnabled(true); //TODO: Shouldn't this be false?
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_reaction_game);

        Intent intent = getIntent();
        patientID = intent.getIntExtra("PATIENT_ID", -1);
        if (patientID == -1)
            throw new IllegalArgumentException("Expected Int Extra 'PATIENT_ID' in Intent - Received none");

        findViewById(R.id.reaction_game_exit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /**
     * onClick handler for a button to run a round of the game.
     * The start game should disappear, the information text will be cleared and the asyncTask that
     * starts the reaction timer will be started.
     *
     * @param startButton The start button that is tapped to run the game.
     */
    public void runRound(View startButton) {
        if (!((Button) startButton).getText().equals(getResources().getString(R.string.play_again))) {
            startButton.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.reaction_game_information_txt)).setText(getString(R.string.reactionGame_information));
            reactionTimer = new ReactionTimer();
            reactionTimer.execute(this);

            if (((Button) startButton).getText().equals(getResources().getString(R.string.start_game)))
                ((Button) startButton).setText(R.string.next_round);
        }
        else {
            ((Button) startButton).setText(R.string.start_game);
            ((TextView) findViewById(R.id.reaction_game_information_txt)).setText(getString(R.string.reactionGame_information));
            findViewById(R.id.reaction_game_exit_btn).setVisibility(View.INVISIBLE);

            round = 0;
            results.clear();
        }
    }

    /**
     * onClick handler to stop the timer and measure reaction time.
     * If it's the last round, it will end the game - endGame()
     *
     * @param circleButton The circle button that is tapped.
     */
    public void stopTimer(View circleButton) {
        long endTime = System.currentTimeMillis();
        circleButton.setVisibility(View.INVISIBLE);
        if (startTime == -1)
            throw new IllegalStateException(getString(R.string.reactionGame_exception_timer_not_started));

        int result = (int) (endTime - startTime);
        if (result < 225) {
            stopTimerFoul();
            return;
        }

        results.add(result);

        ((TextView) findViewById(R.id.reaction_game_information_txt)).setText(
                (round < 4)
                        ? String.format(Locale.getDefault(), "Round %d", round + 1)
                        : String.format(Locale.getDefault(), "GAME OVER\nReaction Time: %dms", getAverageResult()));

        round++;
        if (round < 5) {
            startTime = -1;
            findViewById(R.id.reaction_game_start_button).setVisibility(View.VISIBLE);
        } else
            endGame();
    }

    /**
     * Averages the results and will save it to the database.
     * It then displays a play again button and an exit game button.
     */
    private void endGame() {
        DatabaseAccess db = new DatabaseAccess(this);
        String gameName = getResources().getString(R.string.title_reaction_time);
        int gameID = db.getGameId(gameName);
        if (gameID == -1)
            throw new IllegalArgumentException("Invalid Game Name: " + gameName);
        GameSession gameSession = new GameSession(patientID, gameID, getAverageResult(), new Date());
        db.createSession(gameSession);

        int maxAttempts = db.getAssignment(patientID, gameID).getGameAttempts();
        int dailyAttempts = db.getTodaysSessions(patientID, gameID).size();
        int attemptsRemaining = maxAttempts - dailyAttempts;

        ((TextView) findViewById(R.id.reaction_game_information_txt)).append(
                String.format(Locale.ENGLISH,
                        "\nYou have %d attempt%s remaining today.",
                        attemptsRemaining, attemptsRemaining == 1 ? "" : "s"));
        ((Button) findViewById(R.id.reaction_game_start_button)).setText(getString(R.string.play_again));

        if (attemptsRemaining > 0)
            findViewById(R.id.reaction_game_start_button).setVisibility(View.VISIBLE);
        findViewById(R.id.reaction_game_exit_btn).setVisibility(View.VISIBLE);
    }

    /**
     * Will return the average result for the amount of rounds that have been played so far.
     * @return The average reaction time in milliseconds.
     */
    private int getAverageResult() {
        int avgResult = 0;
        for (int x : results)
            avgResult += x;
        avgResult /= results.size();
        return avgResult;
    }

    /**
     * Sets the back button on the home bar to the onBackPressed() function and finishes the activity.
     * @param menuItem
     * @return True if back button is pressed. False should not be returned.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                finish();
                return false;
        }
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
        if (findViewById(R.id.reaction_game_exit_btn).getVisibility() == View.INVISIBLE){
            View circleButton = findViewById(R.id.reaction_game_circle_button);
            View startButton = findViewById(R.id.reaction_game_start_button);
            if (circleButton.getVisibility() == View.VISIBLE)
                stopTimer(circleButton);
            else if (startButton.getVisibility() == View.INVISIBLE)
                stopTimerFoul();
            else if (startButton.getVisibility() == View.VISIBLE)
                runRound(startButton);
        }
    }

    /**
     * AsyncTask to run a separate thread that will wait for a period of time without freezing up
     * the application.
     */
    private static class ReactionTimer extends AsyncTask<ReactionGameActivity, Void, Void> {
        private WeakReference<ReactionGameActivity> gameReference;

        /**
         * Pauses for a random amount of time between 1 and 7 seconds.
         * After that time is up it will make a request to update the UI and display the circle button.
         * @param params
         * @return
         */
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

        /**
         * Called to update the UI and make the circle button visible after the async task has paused
         * for a random amount of time.
         *
         * @param progress
         */
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
