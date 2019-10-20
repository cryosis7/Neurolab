package com.soteria.neurolab;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.Game;

import java.util.HashMap;
import java.util.Map;

/**
 * This class will load a tutorial for the game that is about to be played.
 * It requires 3 Intents.
 * 1) PATIENT_ID | PATIENT_REFERENCE - The patient ID or reference to store the game results in.
 *                                     This isn't used in this class but passed onto the game class.
 * 2) GAME_NAME - The name of the game to load the tutorial for. This should be identical to the
 *                name that is used in the database
 * 3) ATTEMPTS - The number of attempts that the patient has left for this game for the day.
 *               This isn't used in this class but passed onto the game class.
 *
 * @author Scott Curtis
 */
public class TutorialActivity extends AppCompatActivity {

    private int patientID;
    private Game game;
    private int attemptsLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.game_instructions));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_tutorial);

        // Handling intents
        Intent intent = getIntent();
        patientID = intent.getIntExtra("PATIENT_ID", -1);
        String patientRef = intent.getStringExtra("PATIENT_REFERENCE");
        if (patientID == -1) {
            if (patientRef != null)
                patientID = new DatabaseAccess(this).getPatient(patientRef).getPatientID();
            else
                throw new IllegalArgumentException("Expected intent extra 'PATIENT_ID' or 'PATIENT_REFERENCE' - Received none");
        }
        String gameName = intent.getStringExtra("GAME_NAME");
        if (gameName == null)
            throw new IllegalArgumentException("Expected String Extra 'GAME_NAME' in Intent - Received none");
        else game = new DatabaseAccess(this).getGameByName(gameName);
        if (game == null)
            throw new IllegalArgumentException("Could not find game with name: " + gameName + " in database.");
        attemptsLeft = intent.getIntExtra("ATTEMPTS", -1);

        // Set the UI description and name of the game
        ((TextView) findViewById(R.id.tutorial_header)).setText(game.getGameName());
        ((TextView) findViewById(R.id.tutorial_description)).setText(game.getGameDesc());
        setVideo(game.getGameName());
    }

    /**
     * Will set up and load the video
     * The video can be started/paused from tapping on it.
     * A media control bar will also appear after tapping it that allows for
     * play/pause, scrubbing and skipping time.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setVideo(String gameName) {
        final VideoView video = findViewById(R.id.video_view);
        final MediaController mediaController = new MediaController(this);

        // Handles starting and pausing the video on touches.
        video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!video.isPlaying())
                        video.start();
                    else video.pause();
                }
                return false;
            }
        });

        video.setVideoURI(getVideo(gameName));
        video.requestFocus();

        // Loads the video, mutes it and sets it to the first frame when ready.
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                mediaPlayer.setVolume(0f, 0f);
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                        video.setMediaController(mediaController);
                        mediaController.setAnchorView(video);
                    }
                });

                video.setMediaController(mediaController);
                mediaController.setAnchorView(video);
                video.seekTo(1);
            }
        });

        // Sets video to first frame when it reaches the end.
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                video.seekTo(1);
            }
        });
    }

    /**
     * Takes the name of a game as represented in the database, scrubs the name by converting it to
     * lowercase and converting whitespace and dashes to underscores; then retrieves the corresponding
     * mp4 file associated with that name located in the res/raw folder.
     * @param gameName A game name as represented in the database i.e. "Short-term Visual Memory"
     * @return The MP4 URI for the corresponding game.
     */
    private Uri getVideo(String gameName) {
        String scrubbedName = gameName.toLowerCase().replaceAll("[ -]", "_");
        return Uri.parse("android.resource://" + getPackageName() + "/raw/" + scrubbedName);
    }

    /**
     * Sets the nav bar back arrow to the onBackPressed() function.
     * @param item
     * @return True on success
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Initiates the map that pairs game names to the names of their classes.
     *
     * @return A map of {string : Class} i.e. "Reaction Time" : ReactionGameActivity.class
     */
    private Map<String, Class> getClassMap() {
        Map<String, Class> map = new HashMap<>();
        map.put(getResources().getString(R.string.title_reaction_time), ReactionGameActivity.class);
        map.put(getResources().getString(R.string.title_visual_short_term_memory), VisualMemoryActivity.class);
        map.put(getResources().getString(R.string.title_visual_attention), VisualAttentionGame.class);
        map.put(getResources().getString(R.string.title_motor_skills), MotorSkillsGameActivity.class);
        return map;
    }

    /**
     * Starts the game that the tutorial is loaded for.
     * @param view The view that this function is called on.
     */
    public void startGame(View view) {
        Intent intent = new Intent(view.getContext(), getClassMap().get(game.getGameName()));
        intent.putExtra("PATIENT_ID", patientID);
        intent.putExtra("ATTEMPTS", attemptsLeft);
        startActivity(intent);
        finish();
    }
}
