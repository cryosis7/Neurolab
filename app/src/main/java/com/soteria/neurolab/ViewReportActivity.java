package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.soteria.neurolab.Models.Game;
import com.soteria.neurolab.Models.GameAssignment;
import com.soteria.neurolab.Models.Patient;
import com.soteria.neurolab.database.DatabaseAccess;

import java.util.ArrayList;
import java.util.List;

public class ViewReportActivity extends AppCompatActivity {

    private Spinner gameListSpinner;
    private Patient patient; //This will be the patient that is transferred to this page by intent
    private List<GameAssignment> listOfGameAssignments;
    private DatabaseAccess db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        db = DatabaseAccess.getInstance(getApplicationContext());

        Intent intent = getIntent();
        if (intent.hasExtra("PATIENT_REFERENCE")) {
            patient = db.getPatient(intent.getStringExtra("PATIENT_REFERENCE"));
        }
        else
            throw new IllegalArgumentException("Expected Extra 'PATIENT_REFERENCE' in Intent - Received none");

        gameListSpinner = findViewById(R.id.view_report_game_list_spinner);

        listOfGameAssignments = db.getAssignments(patient);

        ArrayList<String> listOfGames = new ArrayList<>();
        for(GameAssignment ga: listOfGameAssignments){
            listOfGames.add(getGameName(ga.getGameID()));
        }

        ArrayAdapter<String> gameListAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, listOfGames);
        gameListSpinner.setAdapter(gameListAdapter);
        gameListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    /**
     * Gets the name of a specific game with the given gameID
     * @param gameID - the ID of the game
     * @return - String game name or null if no game found by given ID
     */
    private String getGameName(int gameID){
        List<Game> fullGameList = db.getGames();
        for(Game game : fullGameList){
            if(game.getGameID() == gameID){
                return game.getGameName();
            }
        }
        return null;
    }
}
