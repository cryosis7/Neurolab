package com.soteria.neurolab;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.Game;
import com.soteria.neurolab.models.GameAssignment;
import com.soteria.neurolab.models.GameSession;
import com.soteria.neurolab.models.Patient;
import com.soteria.neurolab.utilities.DisclaimerAlertDialog;
import com.soteria.neurolab.utilities.ReportGraph;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ViewReportActivity extends AppCompatActivity {
    private final String TAG = "@ViewReportActivity";

    Spinner gameListSpinner;

    private Patient patient; //This will be the patient that is transferred to this page by intent
    private Game game;
    private DatabaseAccess db;
    private ReportGraph reportGraph;
    private List<GameSession> gameSessions;

    private TextView averageScoreTextView;
    private TextView highestScoreTextView;

    private ReportGraph.TIME_FRAME timeFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.view_report_title));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_view_report);

        db = DatabaseAccess.getInstance(getApplicationContext());
        reportGraph = new ReportGraph(this);

        Intent intent = getIntent();
        if (intent.hasExtra("PATIENT_REFERENCE")) {
            patient = db.getPatient(intent.getStringExtra("PATIENT_REFERENCE"));
            if (patient == null)
                throw new IllegalArgumentException("Patient reference does not exist in database: " + intent.getStringExtra("PATIENT_REFERENCE"));
        }
        else
            throw new IllegalArgumentException("Expected Extra 'PATIENT_REFERENCE' in Intent - Received none");

        initializeUIElements();
        populateGameList();
        findViewById(R.id.view_report_all_button).performClick();
    }

    /** Sets the actions bar to include an overflow menu with the disclaimer and log out options.
     *
     * @param menu - Instance of the top bar
     * @return true if the top bar was able to be updated and false if an error occurred.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        /*Grabs the information from the overflow_menu resource in the menu folder and sets them
          to the action bar*/
        try {
            getMenuInflater().inflate(R.menu.overflow_menu, menu);
            return true;
        } catch ( Exception e ) {
            Toast.makeText(getApplicationContext(),"EXCEPTION " + e + " occurred!",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            //If the settings button is pressed, direct the user to the settings page
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_disclaimer:
                DisclaimerAlertDialog dad = new DisclaimerAlertDialog();
                dad.showDisclaimer(this, getResources());
                return true;
            //If the log out button is pressed, send the user to the log in screen
            case R.id.action_logout:
                //TODO link to logout, remove toast afterwards
                Toast.makeText(getApplicationContext(), "LOGOUT PRESSED - Going to log in screen", Toast.LENGTH_SHORT).show();
                     /*startActivity(new Intent(this, //TODO add link to log in here));
                       finish();
                     */
                return true;
            //If an unknown option is selected, display an error to the user
            default:
                Toast.makeText(getApplicationContext(), "INVALID - Option not valid or completed", Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    /**
     * Initializes UI elements/Sets listeners
     */
    public void initializeUIElements(){
        TextView patientIDTextView = findViewById(R.id.view_report_text_patient_id);
        patientIDTextView.setText(getResources().getString(R.string.view_patient_details_patient_identifier, patient.getPatientReference()));
        averageScoreTextView = findViewById(R.id.view_report_text_average);
        highestScoreTextView = findViewById(R.id.view_report_text_highest);

        final Button monthButton = findViewById(R.id.view_report_month_button);
        final Button weekButton = findViewById(R.id.view_report_week_button);
        final Button allButton = findViewById(R.id.view_report_all_button);

        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeFrame = ReportGraph.TIME_FRAME.MONTH;
                monthButton.setBackground(getResources().getDrawable(R.drawable.button_selector_pressed));
                weekButton.setBackground(getResources().getDrawable(R.drawable.button_selector_normal));
                allButton.setBackground(getResources().getDrawable(R.drawable.button_selector_normal));
                drawGraphAndText();
            }
        });

        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeFrame = ReportGraph.TIME_FRAME.WEEK;
                weekButton.setBackground(getResources().getDrawable(R.drawable.button_selector_pressed));
                monthButton.setBackground(getResources().getDrawable(R.drawable.button_selector_normal));
                allButton.setBackground(getResources().getDrawable(R.drawable.button_selector_normal));
                drawGraphAndText();
            }
        });

        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeFrame = ReportGraph.TIME_FRAME.ALL;
                allButton.setBackground(getResources().getDrawable(R.drawable.button_selector_pressed));
                weekButton.setBackground(getResources().getDrawable(R.drawable.button_selector_normal));
                monthButton.setBackground(getResources().getDrawable(R.drawable.button_selector_normal));
                drawGraphAndText();
            }
        });

        gameListSpinner = findViewById(R.id.view_report_game_list_spinner);
    }

    /**
     * Press the home button to go back home
     * @param view
     */
    public void launchMainScreen(View view) {
        startActivity(new Intent(view.getContext(), SearchCreateDeleteActivity.class));
    }

    /**
     * Populates the game list spinner from the database
     */
    private void populateGameList(){
        List<GameAssignment> listOfGameAssignments = db.getAssignments(patient);

        ArrayList<String> listOfGames = new ArrayList<>();
        for(GameAssignment ga: listOfGameAssignments){
            String gameName = getGameName(ga.getGameID());
            if (gameName != null)
                listOfGames.add(gameName);
            else
                throw new InvalidParameterException("GameID: " + ga.getGameID() + " was not found in the database.");
        }

        ArrayAdapter<String> gameListAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, listOfGames);
        gameListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        gameListSpinner.setAdapter(gameListAdapter);
        gameListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                game = db.getGameByName((String) gameListSpinner.getSelectedItem());
                if (patient != null && game != null) {
                    drawGraphAndText();
                }
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
        DatabaseAccess db = new DatabaseAccess(this);
        Game game = db.getGame(gameID);
        return game.getGameName();
    }

    /**
     * Calculates the average score for that game and time period and sets the text
     */
    private void getAverageScore(){
        int scoreTotal = 0;
        for(GameSession gs: gameSessions)
            scoreTotal += gs.getMetrics();
        if(gameSessions.size() == 0){
            averageScoreTextView.setText(getResources().getString(R.string.view_report_average_blank));
        } else {
            averageScoreTextView.setText(getResources().getString(R.string.view_report_average, String.valueOf(scoreTotal / gameSessions.size())));
        }
    }

    /**
     * Calculates the highest score for that game and time period and sets the text
     */
    private void getBestScore(){
        double scoreBest = 0;
        if(gameListSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.title_reaction_time))) {
            for (GameSession gs : gameSessions)
                scoreBest = gs.getMetrics() < scoreBest || scoreBest == 0 ? gs.getMetrics() : scoreBest;
        } else {
            for (GameSession gs : gameSessions)
                scoreBest = gs.getMetrics() > scoreBest ? gs.getMetrics() : scoreBest;
        }
            if (scoreBest == 0) {
                highestScoreTextView.setText(getResources().getString(R.string.view_report_best_blank));
            } else {
                highestScoreTextView.setText(getResources().getString(R.string.view_report_best, String.valueOf(scoreBest)));
            }
    }

    /**
     * calls the draw graph method and then sets the gameSessions list depending on the selected time frame
     * then calls the average and highest methods
     */
    private void drawGraphAndText(){
        if (patient != null && game != null && reportGraph != null) {
            reportGraph.drawGraph(String.valueOf(patient.getPatientID()), String.valueOf(game.getGameID()), timeFrame);
            switch(timeFrame){
                case ALL:
                    gameSessions = db.getAllSessions(String.valueOf(patient.getPatientID()), String.valueOf(game.getGameID()));
                    break;

                case WEEK:
                    gameSessions = db.getLastWeekSessions(String.valueOf(patient.getPatientID()), String.valueOf(game.getGameID()));
                    break;

                case MONTH:
                    gameSessions = db.getLastMonthSessions(String.valueOf(patient.getPatientID()), String.valueOf(game.getGameID()));
                    break;
            }
            getAverageScore();
            getBestScore();
        }
    }

}
