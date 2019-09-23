package com.soteria.neurolab;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

/**
 *  This class contains all the logic for the Visual Short Term Memory game. It displays a grid
 *  of squares and shows the patient a pattern. The patient must complete the pattern shown with
 *  a fixed number of tries per pattern. If they fail three times in a single pattern. It is game
 *  over.
 *
 *  This class retrieves the patient ID from the calling class and uses it to store the game session
 *  later on. The attempt is stored into the database as a game session using the number of squares.
 *  In the highest pattern remembered as the result.
 */
public class VisualMemoryActivity extends AppCompatActivity implements View.OnClickListener {

    /*
     * Global variables for use in multiple functions
     * //TODO make patientID empty once intent is ready to be passed through
     */
    //These integers belong to the patient and are passed through at the beginning of the code.
    int patientID = 0, attemptsLeft = 3;
    //These integers determine how many square are in the pattern, the current pattern number the
    //patient is up to, and how many errors they are allowed to make in the current pattern.
    //triesRemaining is currently set to 5.
    int squaresInPattern = 3, currentPatternNumber = 1, triesRemaining;
    //These integers are used for the synchronized thread which displays the buttons in the pattern
    //in order.
    int loopCount = 0, revealPatternCount = 0;
    //These textviews are used to store information messages at the top of the screen to the patient.
    TextView infoText, triesText;
    //A multidimensional button array for holding all the visual memory game board buttons
    Button[][] gridButton = new Button[5][5];
    //An number array list used to store the numbers 1 through to 25.
    ArrayList<Integer> randList = new ArrayList<>();

    /**
     * This function is called when the page is loaded. It sets the UI elements on the page and grabs
     * the intent from the previous class.
     *
     * @param savedInstanceState preset class Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the top bar title, and back button to reflect that the user is on the visual
        // memory game.
        getSupportActionBar().setTitle(R.string.title_visual_short_term_memory);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_visual_memory_game);

        /*
        // Grabs information from the select games pages intent. This will be used for determining
        // the number of times the patient can play the game and for using the patients ID to
        // update the game session table in the database.

        try {
            Bundle visualBundle = getIntent().getExtras();
            patientID = visualBundle.getInt("PATIENT_ID");
            attemptsLeft = visualBundle.getInt("ATTEMPTS");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"ERROR - An error occurred during page transition : " + e,Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ViewPatientDetails.class)); //TODO change viewPatientDetails to select game once added
            finish();
        }
        */

        //  Declarations for the textviews and buttons for setting the text and on click actions
        //  on them on page load.
        final Button playAgain = findViewById(R.id.visual_memory_play_again);
        final Button exitGame = findViewById(R.id.visual_memory_exit_game);
        final Button startGameButton = findViewById(R.id.visual_memory_start_game);
        infoText = findViewById(R.id.visual_memory_game_info);
        triesText = findViewById(R.id.visual_memory_attempts_incorrect_info);
        infoText.setText(getResources().getString(R.string.title_visual_short_term_memory));
        if( attemptsLeft != 1 )
            triesText.setText(getResources().getString(R.string.visual_memory_textview_attempts_plural, Integer.toString(attemptsLeft)));
        else
            triesText.setText(getResources().getString(R.string.visual_memory_textview_attempts_singular, Integer.toString(attemptsLeft)));

        /*
         * Pressing the start game button will set up the visual memory board and will allow the
         * user to play the game.
         */
        startGameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Sets the ID for each button and reduces the patients attempts at the game by one
                setButtons();

                //Makes the game board visible
                findViewById(R.id.gameBoardRow0).setVisibility(View.VISIBLE);
                findViewById(R.id.gameBoardRow1).setVisibility(View.VISIBLE);
                findViewById(R.id.gameBoardRow2).setVisibility(View.VISIBLE);
                findViewById(R.id.gameBoardRow3).setVisibility(View.VISIBLE);
                findViewById(R.id.gameBoardRow4).setVisibility(View.VISIBLE);

                // Prevent the user from starting a new game while one is in progress by hiding the
                // start game button
                startGameButton.setVisibility(View.INVISIBLE);

                // Create the pattern for the game
                setUpPattern();
            }
        });

        /*
         * Pressing the play again button will allow the user to play the game again, resetting
         * the squaresInPattern and lives values. The code is almost the same as that from the start game
         * button.
         */
        playAgain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Reduces the patients attempts at the game by one
                attemptsLeft--;

                // Sets the play again and exit game buttons to invisible so the patient does not
                // click on these accidentally while the game is running.
                playAgain.setVisibility(View.INVISIBLE);
                exitGame.setVisibility(View.INVISIBLE);

                // Resets the values of the squaresInPattern and lives counter to reflect the start of a new
                // game.
                squaresInPattern = 3;

                // Create the pattern for the game
                setUpPattern();
            }
        });

        /*
         * Pressing the exit game button will take the user back to the select games screen.
         * If the game is currently running and not finished, an attempt will be given back to
         * the user.
         */
        exitGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    /**
     * This function sets each button used for the game into a multidimensional button array.
     * Then it sets the drawable resource and tags for if an incorrect button is
     * pressed on each of the game buttons.
     */
    private void setButtons() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                //This line grabs the button for insertion into the array based off its ID.
                gridButton[i][j] = findViewById(getResources().getIdentifier(
                        "gameButton" + i + j, "id", getPackageName()));
                //Sets the drawable for each button
                gridButton[i][j].setBackgroundResource(R.drawable.button_visual_memory_unselected);

                //Sets a second tag on each button for use in the game. Used to define if the
                //button has been pressed incorrectly.
                gridButton[i][j].setTag(R.string.visual_memory_tag_error_check, "false");
                //Sets a third and fourth tag to define whether the button is a part of the pattern,
                //and whether it has been unselected or selected erroneously for the purpose of
                //revealing the pattern when the patient loses a round.
                gridButton[i][j].setTag(R.string.visual_memory_tag_pattern_check, "false");
                gridButton[i][j].setTag(R.string.visual_memory_tag_not_correct_button, "true");
            }
        }
    }

    /**
     * This function creates the pattern for each round of the game. It displays the pattern
     * one button at a time first, then hides the pattern and sets the onClickListener for each
     * button and starts the game.
     */
    private void setUpPattern() {
        triesText.setText("");

        //Resets the number of tries remaining whenever a new pattern is created.
        triesRemaining = 5;

        //Calls functions to reset the board after each round and clear the array list to help
        //prevent errors
        disableAndRefreshButtons();
        randList.clear();

        // Sets the values for randList equal to the maximum number of buttons in the game board,
        // then randomizes the list by shuffling it with a random.
        for (int i = 0; i < 25; i++) {
            randList.add(i);
        }
        Collections.shuffle(randList, new Random(System.currentTimeMillis()));

        //Sets the title textview to alert the patient to remember the pattern shown.
        infoText.setText(getResources().getString(R.string.visual_memory_textview_remember));
        //Determines the pattern and sets the text for each button involved in the pattern.
        for (loopCount = 0; loopCount < squaresInPattern; loopCount++) {
            Handler shortDelayHandler = new Handler();
            shortDelayHandler.postDelayed(new Runnable() {
                @Override
                public synchronized void run() {
                    //Grab the position of the button using the randLists first value.
                    int position = randList.remove(0);
                    int x = position / 5;
                    int y = position % 5;

                    //Set the text, background color and text colour to easily show the button in the
                    //pattern.
                    gridButton[x][y].setText(String.valueOf(revealPatternCount + 1));
                    gridButton[x][y].setBackgroundResource(R.drawable.button_visual_memory_correct);
                    gridButton[x][y].setTextColor(getResources().getColor(R.color.colorBlack));
                    gridButton[x][y].setTag(R.string.visual_memory_tag_pattern_check, "true");
                    revealPatternCount++;
                }
            }, 500 + ( loopCount * 500 ) );

        }

        //Hides the pattern and sets the on click listener for each button. A handler is used to
        //delay the code, allowing the patient to easily see the pattern.
        Handler longDelayHandler = new Handler();
        longDelayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshButtons();
                infoText.setText(getResources().getString(R.string.visual_memory_textview_start_pattern));
            }
        }, 2000 + ( loopCount * 500 ));
    }

    /**
     * Sets all buttons to be their default values in preparation for the next round or after the
     * pattern has been shown to the patient.
     */
    private void refreshButtons() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                gridButton[i][j].setBackgroundResource(R.drawable.button_visual_memory_unselected);
                gridButton[i][j].setTextColor(getResources().getColor(R.color.colorPrimary));
                gridButton[i][j].setOnClickListener(VisualMemoryActivity.this);

            }
        }
    }

    /**
     * This function removes the onClickListener from all buttons and removes their text values
     * in preparation for the next round
     */
    private void disableAndRefreshButtons() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                gridButton[i][j].setBackgroundResource(R.drawable.button_visual_memory_unselected);
                gridButton[i][j].setTextColor(getResources().getColor(R.color.colorPrimary));
                gridButton[i][j].setOnClickListener(null);
                gridButton[i][j].setText("");
                gridButton[i][j].setTag(R.string.visual_memory_tag_pattern_check, "false");
                gridButton[i][j].setTag(R.string.visual_memory_tag_not_correct_button, "true");
            }
        }
    }

    /**
       Strips all buttons of their onClickListener
     */
    private void disableButtons() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                gridButton[i][j].setOnClickListener(null);
            }
        }
    }

    /**
     * Once a game has concluded, this functions takes the number of squares remembered and saves it
     * as an individual session. Then, if the user has any attempts left, it will ask them if they
     * would like to go again.
     */
    private void gameOver() {
        //Sets the current pattern number to one to prepare for the next attempt.
        currentPatternNumber = 1;
        revealPatternCount = 0;
        disableButtons();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (gridButton[i][j].getTag(R.string.visual_memory_tag_not_correct_button) == "true" &&
                        gridButton[i][j].getTag(R.string.visual_memory_tag_pattern_check) == "true") {
                    gridButton[i][j].setBackgroundResource(R.drawable.button_visual_memory_not_found);
                    gridButton[i][j].setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }
        }
        //Calculates the score by using the squaresInPattern number. This is reduced by one as the
        //last pattern is a failure and is not counted towards the score.
        double score = Double.parseDouble(Integer.toString(squaresInPattern--));

        //Creates a new game session in the database
        GameSession gameSession = new GameSession(patientID, 2, score, new Date());
        DatabaseAccess db = new DatabaseAccess(this);
        db.createSession(gameSession);

        //Reduces the attempts left by one and displays the play again and the exit game buttons.
        //If the patient has no attempts left, the play again button will not be shown.
        attemptsLeft--;
        if (attemptsLeft > 0) {
            findViewById(R.id.visual_memory_play_again).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.visual_memory_exit_game).setVisibility(View.VISIBLE);
    }

    /**
     * This function has the code that operates when the user clicks on a button at the top bar.
     * This class only has the back button on the top bar.
     *
     * @param menuItem The button that was pressed on the top bar
     * @return A boolean value depending if the user pressed a button that belonged to a case
     * ( true ) or if it had to refer to the default case ( false )
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                finish();
                return false;
        }
    }

    /**
     * Contains the logic associated with
     *
     * @param view the game button that is pressed by the patient
     */
    @Override
    public void onClick(View view) {
        //If the buttons number matches
        if (((Button) view).getText().toString().equals(Integer.toString(currentPatternNumber))) {
            view.setBackgroundResource(R.drawable.button_visual_memory_correct);
            ((Button) view).setTextColor(getResources().getColor(R.color.colorBlack));
            view.setOnClickListener(null);
            view.setTag(R.string.visual_memory_tag_error_check, "false");
            view.setTag(R.string.visual_memory_tag_not_correct_button, "false");
            currentPatternNumber++;

            /*
             *    Iterate through all buttons, and sets the colour of all incorrect selections
             *    to their original state.
             */
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (gridButton[i][j].getTag(R.string.visual_memory_tag_error_check).equals("true")) {
                        gridButton[i][j].setBackgroundResource(R.drawable.button_visual_memory_unselected);
                        gridButton[i][j].setTextColor(getResources().getColor(R.color.colorPrimary));
                        gridButton[i][j].setTag(R.string.visual_memory_tag_error_check, "false");
                        gridButton[i][j].setOnClickListener(VisualMemoryActivity.this);
                    }
                }
            }
            if (currentPatternNumber > squaresInPattern) {
                squaresInPattern++;
                currentPatternNumber = 1;
                //Prevents overflow by ending the game early
                if (squaresInPattern >= 20)
                    gameOver();
                else {
                    infoText.setText(getResources().getString(R.string.visual_memory_textview_round_complete));
                    triesText.setText(null);
                    Handler finishRoundHandler = new Handler();
                    finishRoundHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            revealPatternCount = 0;
                            setUpPattern();
                        }
                    }, 2000 );
                }
            }
        } else {
            view.setBackgroundResource(R.drawable.button_visual_memory_incorrect);
            ((Button) view).setTextColor(getResources().getColor(R.color.colorWarning));
            view.setOnClickListener(null);
            view.setTag(R.string.visual_memory_tag_error_check, "true");
            triesRemaining--;
            if(triesRemaining == 1)
                triesText.setText(getResources().getString(R.string.visual_memory_textview_try_singular, Integer.toString(triesRemaining)));

            else if (triesRemaining == 0) {
                infoText.setText(getResources().getString(R.string.visual_memory_textview_game_complete));
                triesText.setText(getResources().getString(R.string.visual_memory_textview_well_done));
                gameOver();
            } else
                triesText.setText(getResources().getString(R.string.visual_memory_textview_try_plural, Integer.toString(triesRemaining)));
        }

    }
}