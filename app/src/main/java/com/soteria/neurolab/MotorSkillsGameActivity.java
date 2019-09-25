package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author Jason Krieg
 */
public class MotorSkillsGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the top bar title, and back button to reflect that the user is on the visual
        // memory game.
        if(getSupportActionBar() != null ) {
            getSupportActionBar().setTitle(R.string.title_motor_skills);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setContentView(R.layout.activity_motor_skills_game);
        }

        // Grabs information from the select games pages intent. This will be used for determining
        // the number of times the patient can play the game and for using the users ID to
        // update the game session table in the database.
        try {
            Bundle visualBundle = getIntent().getExtras();
            int patientID = visualBundle.getInt("PATIENT_ID");
            int attemptsLeft = visualBundle.getInt("ATTEMPTS");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"ERROR - An error occurred during page transition : " + e,Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ViewPatientDetails.class)); //TODO change ViewPatientDetails to SelectGameActivity once added
            finish();
        }


    }
}
