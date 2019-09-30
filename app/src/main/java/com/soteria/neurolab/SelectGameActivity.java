package com.soteria.neurolab;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.utilities.PasswordAuthentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectGameActivity extends AppCompatActivity {

    private int patientID;
    private Map<String, Class> gameClassMap;
    private List<String> dataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.select_game));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_select_game);

        // Handling intents 'PATIENT_ID' or 'PATIENT_REFERENCE'
        Intent intent = getIntent();
        patientID = intent.getIntExtra("PATIENT_ID", -1);
        String patientRef = intent.getStringExtra("PATIENT_REFERENCE");
        if (patientID == -1) {
            if (patientRef != null)
                patientID = new DatabaseAccess(this).getPatient(patientRef).getPatientID();
            else throw new IllegalArgumentException("Expected intent extra 'PATIENT_ID' or 'PATIENT_REFERENCE' - Received none");
        }

        gameClassMap = initMap();
        dataSet = new DatabaseAccess(this).getAssignmentNames(patientID);

        // Initialise Recycler View
        RecyclerView gameListView = findViewById(R.id.select_game_list);
        gameListView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        gameListView.setLayoutManager(layoutManager);
        SelectGameRecyclerAdapter mAdapter = new SelectGameRecyclerAdapter(dataSet);
        mAdapter.setClickListener(new SelectGameRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String gameName = dataSet.get(position);
                int attemptsLeft = getAttemptsLeft(gameName);
                if (attemptsLeft > 0) {
                    Intent intent = new Intent(view.getContext(), gameClassMap.get(gameName));
                    intent.putExtra("PATIENT_ID", patientID);
                    intent.putExtra("ATTEMPTS", attemptsLeft);
                    startActivity(intent);
                }
                else
                    Toast.makeText(SelectGameActivity.this, "There are no attempts remaining for this game.", Toast.LENGTH_LONG).show();
            }
        });
        gameListView.setAdapter(mAdapter);
    }

    /**
     * Calculates the amount of attempts the patient has left for a particular game.
     * @param gameName the name of the game to lookup
     * @return the number of attempts left the patient has left for a particular game.
     */
    private int getAttemptsLeft(String gameName) {
        DatabaseAccess db = new DatabaseAccess(this);
        int gameId = db.getGameId(gameName);
        int maxAttempts = db.getAssignment(patientID, gameId).getGameAttempts();
        int dailyAttempts = db.getTodaysSessions(patientID, gameId).size();
        return maxAttempts - dailyAttempts;
    }

    /**
     * Overriding the on back pressed to require a password prompt first.
     */
    @Override
    public void onBackPressed() {
        validate();
    }

    /**
     * Creates an alert dialog that prompts the user for the password.
     * Updates the validPassword to the result.
     */
    private void validate() {
        // Get stored password
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.apply();
        final String storedHash = pref.getString("passwordHash", null); //TODO: Store string as preference not literal
        if (storedHash == null) throw new RuntimeException("No password has been set"); //TODO: Uncomment with 016 pushed to master

        // Build alert dialog
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.requestFocus();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input.getText().toString();

                        PasswordAuthentication authenticator = new PasswordAuthentication();
                        if (authenticator.authenticate(password.toCharArray(), storedHash)) {
                            SelectGameActivity.super.onBackPressed(); // Triggers normal back button, skipping the rest of the function.
                        }
                        else
                            Toast.makeText(SelectGameActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    /**
     * Initiates the map that pairs game names to the names of their classes.
     *
     * @return A map of {string : Class} i.e. "Reaction Time" : ReactionGameActivity.class
     */
    private Map<String, Class> initMap() {
        Map map = new HashMap<>();
        map.put(getResources().getString(R.string.title_reaction_time), ReactionGameActivity.class);
        map.put(getResources().getString(R.string.title_visual_short_term_memory), VisualMemoryActivity.class);
//        map.put(getResources().getString(R.string.title_selective_attention), VisualAttentionActivity.class);
        return map;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                validate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
