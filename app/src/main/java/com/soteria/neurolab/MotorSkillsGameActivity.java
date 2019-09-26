package com.soteria.neurolab;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameAssignment;
import com.soteria.neurolab.models.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Jason Krieg
 */
public class MotorSkillsGameActivity extends AppCompatActivity {

    //UI elements
    private TextView gameTitleTextView;
    private TextView gameAttemptsTextView;
    private Button startGameButton;
    private Button playAgainButton;
    private Button exitGameButton;
    private ConstraintLayout gameLayout;

    //Models
    private Patient patient;
    private GameAssignment gameAssignment;

    private DatabaseAccess db;

    //Button values
    private String[][] numberAlphabet = {{"1", "A"}, {"2", "B"}, {"3", "C"}, {"4", "D"}, {"5", "E"},
            {"6", "F"}, {"7", "G"}, {"8", "H"}, {"9", "I"}, {"10", "J"}, {"11", "K"}, {"12", "L"},
            {"13", "M"}, {"14", "N"}, {"15", "O"}, {"16", "P"}, {"17", "Q"}, {"18", "R"}, {"19", "S"},
            {"20", "T"}, {"21", "U"}, {"22", "V"}, {"23", "W"}, {"24", "X"}, {"25", "Y"}, {"26", "Z"}};

    //An array of an ArrayList - This is because you can't loop through a two dimensional array if
    //  any of the values are not initialized, however if there is one item in the first dimension
    //  of this array then it is assured that there will be one in the second. - See setupGameRound()
    private List<Button> gameButtonArray[] = new ArrayList[2];

    private int round;

    private ShapeDrawable buttonDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing some global variables
        db = DatabaseAccess.getInstance(getApplicationContext());
        round = 1;

        // Sets the top bar title, and back button to reflect that the user is on the visual
        // memory game.
        if(getSupportActionBar() != null ) {
            getSupportActionBar().setTitle(R.string.title_motor_skills);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setContentView(R.layout.activity_motor_skills_game);
        }

        // Creating test patient and game assignment TODO: Delete this, will be handled by select game screen
        int patientID = 1;
        GameAssignment testGA = new GameAssignment(DatabaseAccess.GAME_ENUM.MOTOR.getGameID(), patientID, 1);
        db.createAssignment(testGA);
        // Grabs information from the select games pages intent. This will be used for determining
        // the number of times the patient can play the game and for using the users ID to
        // update the game session table in the database.
        try {
            Bundle visualBundle = getIntent().getExtras();
            patientID = visualBundle.getInt("PATIENT_ID");
            int attemptsLeft = visualBundle.getInt("ATTEMPTS");
        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(),"ERROR - An error occurred during page transition : " + e,Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, ViewPatientDetails.class)); //TODO change ViewPatientDetails to SelectGameActivity once added
//            finish();
        }

        patient = db.getPatient(patientID);
        gameAssignment = db.getAssignment(patient.getPatientID(), DatabaseAccess.GAME_ENUM.MOTOR);
        setupUI();
    }

    /**
     * Initialize UI elements on startup
     */
    private void setupUI(){
        gameTitleTextView = findViewById(R.id.motor_skills_game_title);
        gameAttemptsTextView = findViewById(R.id.motor_skills_game_incorrect_info);
        startGameButton = findViewById(R.id.motor_skills_game_start_game);
        playAgainButton = findViewById(R.id.motor_skills_game_play_again);
        exitGameButton = findViewById(R.id.motor_skills_game_exit_game);
        gameLayout = findViewById(R.id.motor_skills_game_constraint_layout);
        playAgainButton.setEnabled(false);
        playAgainButton.setVisibility(View.INVISIBLE);
        exitGameButton.setEnabled(false);
        exitGameButton.setVisibility(View.INVISIBLE);
        gameAttemptsTextView.setText(getResources().getString(R.string.visual_memory_textview_attempts_plural, String.valueOf(gameAssignment.getGameAttempts())));

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupGameRound();
            }
        });
    }

    /**
     * Uses the game round to determine the buttons to display, their sizes, and renders them then
     * starts the round
     */
    private void setupGameRound(){
        startGameButton.setEnabled(false);
        startGameButton.setVisibility(View.INVISIBLE);

        gameButtonArray[0] = new ArrayList<>();
        gameButtonArray[1] = new ArrayList<>();
        setGameButtons(0);
        setGameButtons(1);
    }

    /**
     * Will generate random x and y coordinates, if there would be no overlap of buttons by placing
     *  a button at x/y then it will place the button, set the text and the onClickListener
     * @param arrayListIndex - The index of the gameButtonArrayListArray
     */
    private void setGameButtons(int arrayListIndex){
        buttonDrawable = (ShapeDrawable) getResources().getDrawable(R.drawable.motor_skills_game_button);
        buttonDrawable.getShape().resize(120 % round, 120 % round);
        Random rand = new Random();
        int x = rand.nextInt(721);
        int y = rand.nextInt(785);

        for(int i = 0; i < round; i++){
            gameButtonArray[arrayListIndex].add(new Button(this));
            gameLayout.addView(gameButtonArray[arrayListIndex].get(i));
            gameButtonArray[arrayListIndex].get(i).setCompoundDrawables(buttonDrawable, null, null, null);
            gameButtonArray[arrayListIndex].get(i).setText(numberAlphabet[i][arrayListIndex]);
            gameButtonArray[arrayListIndex].get(i).setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));

            //Set random x/y coordinates for the button, if the new coordinates would put the button
            //  behind another button then set x/y coordinates again
            boolean shorterDistance = true;
            while(shorterDistance){
                for (Button tempButton : gameButtonArray[0]){
                    float largerX = x > tempButton.getX() ? x : tempButton.getX();
                    float smallerX = x < tempButton.getX() ? x : tempButton.getX();
                    float largerY = y > tempButton.getY() ? y : tempButton.getY();
                    float smallerY = y < tempButton.getY() ? y : tempButton.getY();
                    double distance = Math.sqrt(Math.pow(largerX - smallerX, 2) + Math.pow(largerY - smallerY, 2) * 1.0);
                    shorterDistance = distance < 30 % round ? true : false;
                    if(shorterDistance)
                        break;
                }
                if(shorterDistance){
                    for (Button tempButton : gameButtonArray[1]){
                        float largerX = x > tempButton.getX() ? x : tempButton.getX();
                        float smallerX = x < tempButton.getX() ? x : tempButton.getX();
                        float largerY = y > tempButton.getY() ? y : tempButton.getY();
                        float smallerY = y < tempButton.getY() ? y : tempButton.getY();
                        double distance = Math.sqrt(Math.pow(largerX - smallerX, 2) + Math.pow(largerY - smallerY, 2) * 1.0);
                        shorterDistance = distance < 30 % round ? false : true;
                        if(!shorterDistance)
                            break;
                    }
                }
                if(shorterDistance) {
                    x = rand.nextInt(721);
                    y = rand.nextInt(785);
                }
            }
            gameButtonArray[arrayListIndex].get(i).setX(x);
            gameButtonArray[arrayListIndex].get(i).setX(y);

        }
    }


    /**
     * Class to dynamically create a drawable
     */
    public class buttonDrawable extends Drawable{

        public buttonDrawable(int color, int strokeWidth, )

        @Override
        public void draw(@NonNull Canvas canvas) {

        }

        @Override
        public void setAlpha(int i) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }
    }
}
