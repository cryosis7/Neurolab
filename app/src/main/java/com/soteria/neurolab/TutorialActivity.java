package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.Game;
import com.soteria.neurolab.models.Patient;

public class TutorialActivity extends AppCompatActivity {

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
