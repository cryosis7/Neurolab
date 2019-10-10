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

import java.util.List;

/**
 * This class is used to select the game that the patient will play. It requires an ID/Reference then
 * it will load up the games that that patient is assigned to in a list. A game can only be selected
 * if they still have attempts left for the day for that game. It will launch the correlated game that
 * was pressed with the Intents: PATIENT_ID, GAME_NAME and ATTEMPTS
 *
 * @author Scott Curtis
 */
public class SelectGameActivity extends AppCompatActivity {

    private int patientID;
    private List<String> dataSet;
    private DatabaseAccess db;

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
                    Intent intent = new Intent(view.getContext(), TutorialActivity.class);
                    intent.putExtra("PATIENT_ID", patientID);
                    intent.putExtra("ATTEMPTS", attemptsLeft);
                    intent.putExtra("GAME_NAME", gameName);
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
        db = new DatabaseAccess(this);
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
        if (storedHash == null) throw new RuntimeException("No password has been set");

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
                            Intent intent = new Intent(getApplicationContext(), ViewPatientDetails.class);
                            intent.putExtra("PATIENT_ID", patientID);
                            startActivity(intent);
                            finish();
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
     * Sets the back button on the top bar to process the validate function before returning.
     * @param item
     * @return
     */
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
