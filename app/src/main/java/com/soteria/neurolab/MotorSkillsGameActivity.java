package com.soteria.neurolab;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

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
    final private List<Button> gameButtonArray[] = new ArrayList[2];

    private int round;
    private int current;

    private LayerDrawable buttonDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing some global variables
        db = DatabaseAccess.getInstance(getApplicationContext());
        round = 1;

        // Sets the top bar title, and back button to reflect that the user is on the visual
        // memory game.
        if (getSupportActionBar() != null) {
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
    private void setupUI() {
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
                startGameButton.setEnabled(false);
                startGameButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Uses the game round to determine the buttons to display, their sizes, and renders them then
     * starts the round
     */
    private void setupGameRound() {
        current = 0;
        gameLayout.removeAllViews();

        //Reset the button arrays
        // - 0 will be the array of buttons with numbers
        // - 1 will be the array of buttons with letters
        gameButtonArray[0] = new ArrayList<>();
        gameButtonArray[1] = new ArrayList<>();
        setGameButtons(0);
        setGameButtons(1);

        //Set help text
        gameTitleTextView.setText(getResources().getString(R.string.motor_skills_game_instruction,
                numberAlphabet[current][0], numberAlphabet[current][1]));
    }

    /**
     * Will generate random x and y coordinates, if there would be no overlap of buttons by placing
     * a button at x/y then it will place the button, set the text and the onClickListener
     *
     * @param arrayListIndex - The index of the gameButtonArrayListArray
     */
    private void setGameButtons(final int arrayListIndex) {
        final int DEFAULT_BUTTON_SIZE = 250;
        int buttonSize = (int) Math.round(DEFAULT_BUTTON_SIZE/((0.12 * round) + 1));
        buttonDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.motor_skills_game_button);
        GradientDrawable gdYellow = (GradientDrawable) buttonDrawable.findDrawableByLayerId(R.id.motor_skills_button_shape);
        gdYellow.setSize(buttonSize, buttonSize);
        final GradientDrawable gdGreen = (GradientDrawable) gdYellow.getConstantState().newDrawable();
        final GradientDrawable gdNext = (GradientDrawable) gdYellow.getConstantState().newDrawable();
        gdGreen.mutate();
        gdGreen.setStroke(5, getResources().getColor(R.color.colorPrimary));
        gdGreen.setColor(getResources().getColor(R.color.colorGreen));
        gdNext.mutate();
        gdNext.setStroke(5, getResources().getColor(R.color.colorPrimary));
        Random rand = new Random();

        //Create one button per round
        for (int i = 0; i < round; i++) {
            int x = 0;
            int y = 0;
            gameButtonArray[arrayListIndex].add(new Button(this));
            gameLayout.addView(gameButtonArray[arrayListIndex].get(i));
            gameButtonArray[arrayListIndex].get(i).setBackground(i == current ? gdNext : gdYellow);
            gameButtonArray[arrayListIndex].get(i).setText(numberAlphabet[i][arrayListIndex]);
            gameButtonArray[arrayListIndex].get(i).setTextSize(buttonSize / 3);

            //The OnClickListener for the number buttons are different from the alphabet buttons
            switch (arrayListIndex){
                case 0:
                    gameButtonArray[arrayListIndex].get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Button button = (Button) view;
                            if(button.getText().equals(numberAlphabet[current][0])){
                                gameButtonArray[arrayListIndex].get(current).setBackground(gdGreen);
                            }
                        }
                    });
                    break;

                case 1:
                    gameButtonArray[arrayListIndex].get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Button button = (Button) view;
                            if(button.getText().equals(numberAlphabet[current][1])){
                                gameButtonArray[0].get(current).setEnabled(false);
                                gameButtonArray[0].get(current).setVisibility(View.INVISIBLE);
                                gameButtonArray[1].get(current).setEnabled(false);
                                gameButtonArray[1].get(current).setVisibility(View.INVISIBLE);
                                current++;
                                gameTitleTextView.setText(getResources().getString(R.string.motor_skills_game_instruction,
                                        numberAlphabet[current][0], numberAlphabet[current][1]));
                                if(current == round){
                                    round++;
                                    setupGameRound();
                                } else {
                                    gameButtonArray[0].get(current).setBackground(gdNext);
                                    gameButtonArray[1].get(current).setBackground(gdNext);
                                }
                            }
                        }
                    });
                    break;
            }

            //Set random x/y coordinates for the button, if the new coordinates would put the button
            //  behind another button then set x/y coordinates again
            boolean shorterDistance = true;
            while (shorterDistance) {
                System.out.println("Setting x and y");
                x = rand.nextInt(721);
                y = rand.nextInt(785);
                //If adding the size of the button would put x/y out of bounds (unseeable)
                //  then generate a new x/y coordinate.
                while (x + buttonSize > 721) {
                    x = rand.nextInt(721);
                }
                while (y + buttonSize > 785) {
                    y = rand.nextInt(785);
                }

                //Checking that the button doesn't overlap with another
                for (Button tempButton : gameButtonArray[0]) {
                    //Need to subtract smallerX from largerX but don't know which is which yet
                    float largerX = x > tempButton.getX() ? x : tempButton.getX();
                    float smallerX = x < tempButton.getX() ? x : tempButton.getX();
                    float largerY = y > tempButton.getY() ? y : tempButton.getY();
                    float smallerY = y < tempButton.getY() ? y : tempButton.getY();
                    //Using pythagoras' theorem to calc the distance between 2 points
                    double distance = Math.sqrt(Math.pow(largerX - smallerX, 2) + Math.pow(largerY - smallerY, 2) * 1.0);
                    shorterDistance = distance < buttonSize ? true : false;
                    System.out.println("Distance = " + distance + ", Button Size = " + buttonSize + ", shorterDistance = " + shorterDistance);
                    if (shorterDistance)
                        //No need to keep on looping if the x/ coordinates already clash with another button
                        break;
                }
                //The same as above but for the other button array
                if (!shorterDistance) {
                    for (Button tempButton : gameButtonArray[1]) {
                        float largerX = x > tempButton.getX() ? x : tempButton.getX();
                        float smallerX = x < tempButton.getX() ? x : tempButton.getX();
                        float largerY = y > tempButton.getY() ? y : tempButton.getY();
                        float smallerY = y < tempButton.getY() ? y : tempButton.getY();
                        double distance = Math.sqrt(Math.pow(largerX - smallerX, 2) + Math.pow(largerY - smallerY, 2) * 1.0);
                        shorterDistance = distance < buttonSize ? true : false;
                        System.out.println("Distance = " + distance + ", Button Size = " + buttonSize + ", shorterDistance = " + shorterDistance);
                        if (shorterDistance)
                            break;
                    }
                }
            }
            //Set x/y coordinates of the button now that we are sure they aren't out of bounds or
            //  behind another button
            gameButtonArray[arrayListIndex].get(i).setX(x);
            gameButtonArray[arrayListIndex].get(i).setY(y);
        }
    }
}

