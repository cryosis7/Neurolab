package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.soteria.neurolab.Models.Game;
import com.soteria.neurolab.Models.GameAssignment;
import com.soteria.neurolab.Models.Patient;
import com.soteria.neurolab.database.DatabaseAccess;

import java.util.ArrayList;
import java.util.List;

public class ViewReportActivity extends AppCompatActivity {

    private ListView gameListView;
    private Patient patient; //This will be the patient that is transferred to this page by intent
    private List<GameAssignment> listOfGameAssignments;
    private DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        gameListView = findViewById(R.id.view_report_games_list);

        patient = db.getPatient(1); //Hard coded atm - will use the patient trasnferred by intent
        listOfGameAssignments = db.getAssignments(patient);

        List<String> listOfGames = new ArrayList<>();
        for(GameAssignment ga: listOfGameAssignments){
            listOfGames.add(getGameName(ga.getGameID()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, )
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
