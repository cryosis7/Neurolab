package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.Game;

public class TutorialActivity extends AppCompatActivity {

    private static final String TEST_VIDEO_NAME = "short_term_visual_memory";
    private int patientID;
    private Game game;

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
//        String patientRef = intent.getStringExtra("PATIENT_REFERENCE");   //TODO: Uncomment
        String patientRef = "SC05";
        if (patientID == -1) {
            if (patientRef != null)
                patientID = new DatabaseAccess(this).getPatient(patientRef).getPatientID();
            else throw new IllegalArgumentException("Expected intent extra 'PATIENT_ID' or 'PATIENT_REFERENCE' - Received none");
        }
//        String gameID = intent.getStringExtra("GAME_ID");              // TODO: Uncomment
        String gameID = "1";
        if (gameID == null)
            throw new IllegalArgumentException("Expected String Extra 'GAME_ID' in Intent - Received none");
        else game = new DatabaseAccess(this).getGame(Integer.parseInt(gameID));
        if (game == null) throw new IllegalArgumentException("Could not find game with ID: " + gameID + " in database.");

        // Set the UI description and name of the game
        ((TextView) findViewById(R.id.tutorial_header)).setText(game.getGameName());
        ((TextView) findViewById(R.id.tutorial_description)).setText(game.getGameDesc());

        setVideo();
    }

    /**
     * Will set up and load the video
     */
    private void setVideo() {
        final VideoView video = findViewById(R.id.videoView);
        final MediaController mediaController = new MediaController(this);
        video.setVideoURI(getVideo(TEST_VIDEO_NAME));
        video.requestFocus();

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setVolume(0f, 0f);
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                        video.setMediaController(mediaController);
                        mediaController.setAnchorView(video);
                    }
                });

                mediaPlayer.start();
            }
        });
    }

    private Uri getVideo(String videoName) {
        return Uri.parse("android.resource://" + getPackageName() + "/raw/" + videoName);
    }

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
}
