package com.soteria.neurolab;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class VisualMemoryActivity extends AppCompatActivity implements View.OnClickListener {

    /*
        Global variables for use in multiple functions
     */
    int level = 1, lives = 5, patientID = 0, attemptsLeft = 4, currentPatternNumber = 1;
    TextView livesText, levelText;
    Button[][] gridButton = new Button[5][5];

    /**
     * This function is called when the page is loaded. It sets the UI elements on the page and grabs
     * the intent from the previous class.
     *
     * @param savedInstanceState lol I dunno xD
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            Sets the top bar title, and back button to reflect that the user is on the visual
            memory game.
         */
        getSupportActionBar().setTitle(R.string.title_visual_short_term_memory);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_visual_memory_game);

        /*
            Grab information from the select games page relative to the patient

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
        /*
            Declarations for all UI elements that will be used in the onCreate method
         */
        final Button playAgain = findViewById(R.id.visual_memory_play_again);
        final Button exitGame = findViewById(R.id.visual_memory_exit_game);
        final Button startGameButton = findViewById(R.id.visual_memory_start_game);
        levelText = findViewById(R.id.visual_memory_current_level);
        livesText = findViewById(R.id.visual_memory_lives_counter);
        levelText.setText(getResources().getString(R.string.title_visual_short_term_memory));

        /*
         * Pressing the start game button will set up the visual memory board and will allow the
         * user to play the game.
         */
        startGameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setButtons();
                attemptsLeft--;
                levelText.setText(getResources().getString(R.string.visual_memory_game_current_level, level));
                livesText.setText(getResources().getString(R.string.visual_memory_game_lives_count, lives));
                findViewById(R.id.gameBoardRow0).setVisibility(View.VISIBLE);
                findViewById(R.id.gameBoardRow1).setVisibility(View.VISIBLE);
                findViewById(R.id.gameBoardRow2).setVisibility(View.VISIBLE);
                findViewById(R.id.gameBoardRow3).setVisibility(View.VISIBLE);
                findViewById(R.id.gameBoardRow4).setVisibility(View.VISIBLE);
                livesText.setVisibility(View.VISIBLE);
                startGameButton.setVisibility(View.INVISIBLE);
                setUpGrid();
            }
        });

        /*
         * Pressing the play again button will allow the user to play the game again, resetting
         * the level and lives values. The code is almost the same as that from the start game
         * button.
         */
        playAgain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playAgain.setVisibility(View.INVISIBLE);
                exitGame.setVisibility(View.INVISIBLE);

                 /*
                    Resets the values of the level and lives counter to reflect the start of a new
                    game.
                 */
                level = 1;
                lives = 5;

                attemptsLeft--;
                levelText.setText(getResources().getString(R.string.visual_memory_game_current_level, level));
                livesText.setText(getResources().getString(R.string.visual_memory_game_lives_count, lives));
                livesText.setVisibility(View.VISIBLE);
                setUpGrid();
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

    private void setButtons() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                String buttonID = "gameButton" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                gridButton[i][j] = findViewById(resID);
                gridButton[i][j].setBackgroundResource(R.drawable.button_visual_memory_unselected);
                gridButton[i][j].setTag(R.string.visual_memory_game_error_check, "true");
            }
        }
    }

    /**
     * This class sets up the grid for the memory game and assigns the pattern to the board.
     */
    private void setUpGrid() {
        ArrayList<Integer> randList = new ArrayList<>();

        disableButtons();
        resetButtonText();
        randList.clear();

        for (int i = 0; i < 25; i++) {
            randList.add(i);
        }
        Collections.shuffle(randList, new Random(System.currentTimeMillis()));

        //Determines the pattern and sets the text for each pattern
        for (int i = 0; i < level + 3; i++) { //The pattern count is equal to the level plus 3
            int position = randList.remove(0);
            int x = position / 5;
            int y = position % 5;
            gridButton[x][y].setText(String.valueOf(i + 1));
            gridButton[x][y].setBackgroundResource(R.drawable.button_visual_memory_correct);
            gridButton[x][y].setTextColor(getResources().getColor(R.color.colorBlack));
        }
        //Hides the pattern and sets the on click listener for each button
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                gridButton[i][j].setBackgroundResource(R.drawable.button_visual_memory_unselected);
                gridButton[i][j].setTextColor(getResources().getColor(R.color.colorBlack));
                gridButton[i][j].setOnClickListener(VisualMemoryActivity.this);
            }
        }
    }

    private void disableButtons() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                gridButton[i][j].setOnClickListener(null);
            }
        }
    }

    private void resetButtonText() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                gridButton[i][j].setText("");
            }
        }
    }

    /**
     * Once a game has concluded, this functions takes the number of squares remembered and saves it
     * as an induvidual session. Then, if the user has any attempts left, it will ask them if they
     * would like to go again.
     */
    private void gameOver() {
        currentPatternNumber = 1;
        disableButtons();
        double score = Double.parseDouble(Integer.toString(level + 3));
        Button playAgain = findViewById(R.id.visual_memory_play_again);
        Button exitGame = findViewById(R.id.visual_memory_exit_game);

        GameSession gameSession = new GameSession(patientID, 2, score, new Date());
        DatabaseAccess db = new DatabaseAccess(this);
        db.createSession(gameSession);
        attemptsLeft--;
        if (attemptsLeft > 0) {
            playAgain.setVisibility(View.VISIBLE);
        }
        exitGame.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View view) {
        //If the buttons number matches
        if (((Button) view).getText().toString().equals(Integer.toString(currentPatternNumber))) {
            view.setBackgroundResource(R.drawable.button_visual_memory_correct);
            ((Button) view).setTextColor(getResources().getColor(R.color.colorBlack));
            view.setOnClickListener(null);
            view.setTag(R.string.visual_memory_game_error_check, "true");
            currentPatternNumber++;
            /*
             *    Iterate through all buttons, and sets the colour of all incorrect selections
             *    to their original state
             */
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (gridButton[i][j].getTag(R.string.visual_memory_game_error_check).equals("false")) {
                        gridButton[i][j].setBackgroundResource(R.drawable.button_visual_memory_unselected);
                        gridButton[i][j].setTextColor(getResources().getColor(R.color.colorPrimary));
                        gridButton[i][j].setTag(R.string.visual_memory_game_error_check, "true");
                        gridButton[i][j].setOnClickListener(VisualMemoryActivity.this);
                    }
                }
            }
            if (currentPatternNumber > level + 3) {
                level++;
                levelText.setText(getResources().getString(R.string.visual_memory_game_current_level, level));
                currentPatternNumber = 1;
                if (level >= 20)
                    gameOver();
                else
                    setUpGrid();
            }
        } else {
            view.setBackgroundResource(R.drawable.button_visual_memory_incorrect);
            ((Button) view).setTextColor(getResources().getColor(R.color.colorWarning));
            view.setOnClickListener(null);
            view.setTag(R.string.visual_memory_game_error_check, "false");
            lives--;
            livesText.setText(getResources().getString(R.string.visual_memory_game_lives_count, lives));
            if (lives == 0) {
                levelText.setText(getResources().getString(R.string.visual_memory_game_over));
                livesText.setVisibility(View.INVISIBLE);
                gameOver();
            }
        }

    }
}