package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Random;

/**
 * This class contains the code for the Visual Attention game. A grid of images is displayed along
 * with a target image, the user must select all of the images in the grid that match the target
 * image. There are ten rounds per game session, and the user is measured on their accuracy in
 * selecting the correct symbols.
 *
 * The patientID and attempts are passed through as intents from the select games screen, a game
 * session is created in the database using the patientID and gameID, it also stores the metrics
 * and date played.
 *
 * @author Brianna Winkels
 */

public class VisualAttentionGame extends AppCompatActivity {

    //Values from patient to be passed through as intents
    private int attemptsLeft = 3; private int patientID = 1;
    //These fields are used by multiple methods and are initialised
    private int roundCount = 1; private int numOfTargets = 0; private int targetsFound = 0;
    private double roundScore = 0; private int numOfTaps = 0; private double totalScore = 0;

    private int buttonsHorizontal; private int buttonsVertical; private int[] imageSet;
    private ImageButton[][] buttons; private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null ) {
            getSupportActionBar().setTitle("Visual Attention");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Grabs information from the select games pages intent. This will be used for determining
        // the number of times the patient can play the game and for using the users ID to
        // update the game session table in the database.
        /*try {
            Bundle visualBundle = getIntent().getExtras();
            patientID = visualBundle.getInt("PATIENT_ID");
            attemptsLeft = visualBundle.getInt("ATTEMPTS");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"ERROR - An error occurred during page transition : " + e,Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ViewPatientDetails.class)); //TODO change ViewPatientDetails to SelectGameActivity once added
            finish();
        }*/

        setUpRounds();
    }

    /**
     * This method sets up each game round by retrieving the target image and images to be used for
     * the round and assigning an image to each button. It also includes the onClickListeners for
     * the grid buttons and submit button
     */
    public void setUpRounds(){
        //The grid size and size of the button array are both determined by the current round number
        if(roundCount == 1 || roundCount == 2 || roundCount == 3){
            buttonsHorizontal = 4;
            buttonsVertical = 4;
            setContentView(R.layout.activity_visual_attention_game_grid4x4);
        } else if(roundCount == 4 || roundCount == 5 || roundCount == 6){
            buttonsHorizontal = 5;
            buttonsVertical = 5;
            setContentView(R.layout.activity_visual_attention_game_grid5x5);
        } else if(roundCount == 7 || roundCount == 8 || roundCount == 9
                    || roundCount == 10){
            buttonsHorizontal = 6;
            buttonsVertical = 6;
            setContentView(R.layout.activity_visual_attention_game_grid6x6);
        }

        buttons = new ImageButton[buttonsVertical][buttonsHorizontal];
        final TextView roundNumText = findViewById(R.id.visual_attention_round_text);
        Button submitAnswer = findViewById(R.id.visual_attention_submit_button);
        final ImageView targetImage = findViewById(R.id.visual_attention_target_image);

        //Sets text to display the round number
        roundNumText.setText(getResources().getString(R.string.visualAttention_round) + " " + roundCount);
        //Gets a set of images depending on the round number
        imageSet = getImageSets();
        //Sets a random target image from the image set
        int randomTarget = getRandomImage();
        //Displays the target image to the patient
        targetImage.setImageDrawable(resizeImages(randomTarget));
        targetImage.setTag(randomTarget);
        int[] roundImages = imageArray(imageSet, randomTarget);
        int count = 0;

        //Loops through and assigns an image to each button
        for (int i = 0; i < buttonsVertical; i++) {
            for (int j = 0; j < buttonsHorizontal; j++) {
                //Gets the id for each button
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                //Sets an image to the current button
                buttons[i][j].setImageDrawable(resizeImages(roundImages[count]));
                //Sets a tag to the button
                buttons[i][j].setTag(roundImages[count]);
                count++;

                //Checks to see if the image for the current button is the same as the target image,
                //if it is then the number of targets is incremented
                if (buttons[i][j].getTag().equals(targetImage.getTag())) {
                    numOfTargets++;
                }
                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Increments the number of taps if a button is tapped
                        numOfTaps++;
                        //If the tapped image is the same as the target image, then the number of
                        //targets found increments, the button is no longer clickable and the
                        //background colour changes to let the patient know it has been tapped
                        if (view.getTag().equals(targetImage.getTag())) {
                            targetsFound++;
                            view.setClickable(false);
                            view.setBackgroundResource(R.drawable.visual_attention_target_button);
                        }
                    }
                });
            }
        }
        //When the user thinks they have found all images for the current round they click the
        //submit button to submit their answers
        submitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If the current round isn't 10 and the user has tapped at least one button then the
                //round score is calculated and added to the total score. The number of targets,
                //number of taps and number of targets found are all reset to zero, and the round
                //count is incremented. The buttons are reset and next round is set up
                if(roundCount != 10 && numOfTaps != 0) {
                    roundScore = calculateRoundScore();
                    totalScore += roundScore;
                    numOfTargets = 0; numOfTaps = 0; targetsFound = 0;
                    roundCount++;
                    resetButtons();
                    setUpRounds();
                } else if(roundCount == 10){
                    //Game over method is called when round 10 is complete
                    roundScore = calculateRoundScore();
                    totalScore += roundScore;
                    gameOver();
                }
            }
        });
    }

    /**
     * Displays the final score and remaining attempts to the patient. If the patient has any
     * attempts remaining they have the option to play again or exit the game
     */
    public void gameOver(){
        //Decrements remaining attempts
        attemptsLeft--;
        //Calculates the final score
        totalScore = totalScore / 10;
        BigDecimal decimalScore = new BigDecimal(totalScore).setScale(2, RoundingMode.HALF_UP);
        double finalScore = decimalScore.doubleValue();

        //Displays the score screen
        setContentView(R.layout.activity_visual_attention_game_score_screen);
        TextView scoreText = findViewById(R.id.visual_attention_score_text);
        scoreText.setText(finalScore + "%");

        TextView attemptsRemaining = findViewById(R.id.visual_attention_attempts_text);
        Button playAgainBtn = findViewById(R.id.visual_attention_play_again_btn);

        //Creates a new game session in the database
        GameSession gameSession = new GameSession(patientID,  4, finalScore, new Date());
        DatabaseAccess db = new DatabaseAccess(this);
        db.createSession(gameSession);

        //Displays the remaining attempts, hides the play again button if no remaining attempts
        if(attemptsLeft >= 1){
            attemptsRemaining.setText(getResources().getString(R.string.visualAttention_attempts_left) + " " + attemptsLeft);
        } else{
            attemptsRemaining.setText(getResources().getString(R.string.visualAttention_no_attempts));
            playAgainBtn.setVisibility(View.INVISIBLE);
        }

        //If the patient plays again, round count, round score and total score are reset.
        //Game restarts from round 1
        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roundCount = 1; roundScore = 0; totalScore = 0;
                setUpRounds();
            }
        });

        //TODO implement functionality to return to select game screen once implemented
       /* exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });*/
    }

    /**
     * Creates an array of images depending on the round number and size of the grid
     * @return the array of images after being shuffled
     */
    public int[] imageArray(int[] roundImages, int target){
        int[] images = null;
        int count = 0;
        switch (roundCount){
            //If the round number is 1, 2 or 3 then 16 images are added for a 4x4 grid. 4 of each
            //image is added to the array
            case 1: case 2: case 3:
                images = new int[16];
                for(int i = 0; i < 4; i++){
                    for(int j = 0; j < 4; j++){
                        images[count] = roundImages[i];
                        count++;
                    }
                }
                break;
            //If the round number is 4, 5 or 6 then 25 images are added for a 5x5 grid. 6 of each
            //image is added to the array, and an additional target image
            case 4: case 5: case 6:
                images = new int[25];
                for (int i = 0; i < 6; i++){
                    for (int j = 0; j < 4; j++){
                        images[count] = roundImages[j];
                        count++;
                        if(count == 24){
                            images[count] = target;
                        }
                    }
                }
                break;
            //If the round number is 7, 8, 9 or 10 then 36 images are added for a 6x6 grid.
            //7 of each image is added to the array, and an additional target image
            case 7: case 8: case 9: case 10:
                images = new int[36];
                for (int i = 0; i < 7; i++){
                    for (int j = 0; j < 5; j++){
                        images[count] = roundImages[j];
                        count++;
                        if(count == 35){
                            images[count] = target;
                        }
                    }
                }
                break;
            }
        //Images are shuffled twice
        int[] firstShuffle = shuffleImages(images);
        return shuffleImages(firstShuffle);
    }

    /**
     * Shuffles the images that will be placed into the grid
     * @param images
     * @return an array of shuffled images
     */
    public int[] shuffleImages(int[] images){
        int tmp, index;
        Random rand = new Random();
        for (int i = images.length - 1; i > 0; i--){
            index = rand.nextInt(i + 1);
            tmp = images[index];
            images[index] = images[i];
            images[i] = tmp;
        }
        return images;
    }

    /**
     * Resets all of the grid buttons to their original state for the next round
     */
    public void resetButtons(){
        buttons = new ImageButton[buttonsVertical][buttonsHorizontal];
        for (int i = 0; i < buttonsVertical; i++) {
            for (int j = 0; j < buttonsHorizontal; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                //Makes all of the buttons clickable again
                buttons[i][j].setClickable(true);
                //Sets the button background to default unpressed button
                buttons[i][j].setBackgroundResource(R.drawable.visual_attention_button);
            }
        }
    }

    /**
     * Resizes the image passed to it
     * @param image
     * @return the resized image
     */
    public Drawable resizeImages(int image){
        Drawable dr = getResources().getDrawable(image);
        Bitmap bm = ((BitmapDrawable) dr).getBitmap();
        Drawable drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bm, 105, 105, true));
        return drawable;
    }

    /**
     * Returns a random image from the image set which will be set as the target image
     * @return a random image
     */
    public Integer getRandomImage(){
        Random rand = new Random();
        return imageSet[rand.nextInt(imageSet.length)];
    }

    /**
     * Calculates the score for the round based on the total number of target images in the grid,
     * the number of target images found, and the number of taps made by the patient
     * @return the round score rounded to two decimal places
     */
    public double calculateRoundScore(){
        roundScore = (((double) targetsFound / numOfTargets) * ((double) targetsFound / numOfTaps) * 100);
        BigDecimal bdScore = new BigDecimal(roundScore).setScale(2, RoundingMode.HALF_UP);
        return bdScore.doubleValue();
    }

    /**
     * Returns a random set of images based on the current round number
     * @return a random set of images
     */
    public int[] getImageSets(){
        switch (roundCount){
            case 1: return roundOneSets();
            case 2: return roundTwoSets();
            case 3: return roundThreeSets();
            case 4: return roundFourSets();
            case 5: return roundFiveSets();
            case 6: return roundSixSets();
            case 7: return roundSevenSets();
            case 8: return roundEightSets();
            case 9: return roundNineSets();
            case 10: return roundTenSets();
        }
        return null;
    }

    /**
     * Instantiates five different image sets for round one and returns a random set of images to
     * be used for the round
     * @return a random set of images for round one
     */
    public int[] roundOneSets(){
        int[] roundOneSetOne = {R.mipmap.circle, R.mipmap.diamond, R.mipmap.plus1, R.mipmap.star};
        int[] roundOneSetTwo = {R.mipmap.checked, R.mipmap.heart, R.mipmap.moon, R.mipmap.star2};
        int[] roundOneSetThree = {R.mipmap.plus, R.mipmap.star3, R.mipmap.sunny, R.mipmap.heart2};
        int[] roundOneSetFour = {R.mipmap.carrot, R.mipmap.leaf, R.mipmap.flower, R.mipmap.notification};
        int[] roundOneSetFive = {R.mipmap.butterfly, R.mipmap.clover, R.mipmap.like, R.mipmap.sun};

        //Gets a random number between one and five, returns an image set based on the random number
        Random random = new Random();
        int setNum = random.nextInt(5) + 1;
        switch (setNum){
            case 1: return roundOneSetOne;
            case 2: return roundOneSetTwo;
            case 3: return roundOneSetThree;
            case 4: return roundOneSetFour;
            case 5: return roundOneSetFive;
        }
        return null;
    }

    /**
     * Instantiates five different image sets for round two and returns a random set of images to
     * be used for the round
     * @return a random set of images for round two
     */
    public int[] roundTwoSets(){
        int[] roundTwoSetOne = {R.mipmap.crown, R.mipmap.clock, R.mipmap.circle_green, R.mipmap.rainbow};
        int[] roundTwoSetTwo = {R.mipmap.apple, R.mipmap.crown2, R.mipmap.trophy, R.mipmap.unchecked};
        int[] roundTwoSetThree = {R.mipmap.cake, R.mipmap.flash, R.mipmap.lotus_flower, R.mipmap.unchecked2};
        int[] roundTwoSetFour = {R.mipmap.driving, R.mipmap.hourglass, R.mipmap.rocket, R.mipmap.smiley};
        int[] roundTwoSetFive = {R.mipmap.light_bulb, R.mipmap.star4, R.mipmap.target, R.mipmap.tulip};

        //Gets a random number between one and five, returns an image set based on the random number
        Random random = new Random();
        int setNum = random.nextInt(5) + 1;
        switch (setNum){
            case 1: return roundTwoSetOne;
            case 2: return roundTwoSetTwo;
            case 3: return roundTwoSetThree;
            case 4: return roundTwoSetFour;
            case 5: return roundTwoSetFive;
        }
        return null;
    }

    /**
     * Instantiates five different image sets for round three and returns a random set of images to
     * be used for the round
     * @return a random set of images for round three
     */
    public int[] roundThreeSets(){
        int[] roundThreeSetOne = {R.mipmap.christian_cross, R.mipmap.pentagon, R.mipmap.plain_circle, R.mipmap.six_pointed_star};
        int[] roundThreeSetTwo = {R.mipmap.cancel, R.mipmap.check, R.mipmap.padlock, R.mipmap.tumble_dry};
        int[] roundThreeSetThree = {R.mipmap.hexagon, R.mipmap.peace_symbol, R.mipmap.plain_square, R.mipmap.plain_star};
        int[] roundThreeSetFour = {R.mipmap.no_entry_sign, R.mipmap.plain_heart, R.mipmap.plain_triangle, R.mipmap.plus_sign};
        int[] roundThreeSetFive = {R.mipmap.casino, R.mipmap.right_arrow, R.mipmap.stop, R.mipmap.yield};

        //Gets a random number between one and five, returns an image set based on the random number
        Random random = new Random();
        int setNum = random.nextInt(5) + 1;
        switch (setNum){
            case 1: return roundThreeSetOne;
            case 2: return roundThreeSetTwo;
            case 3: return roundThreeSetThree;
            case 4: return roundThreeSetFour;
            case 5: return roundThreeSetFive;
        }
        return null;
    }

    /**
     * Instantiates five different image sets for round four and returns a random set of images to
     * be used for the round
     * @return a random set of images for round four
     */
    public int[] roundFourSets(){
        int[] roundFourSetOne = {R.mipmap.bone, R.mipmap.check_box, R.mipmap.shining_sun, R.mipmap.star_and_crescent};
        int[] roundFourSetTwo = {R.mipmap.key, R.mipmap.mandala, R.mipmap.setting, R.mipmap.weather};
        int[] roundFourSetThree = {R.mipmap.crown4, R.mipmap.kite, R.mipmap.startup, R.mipmap.target2};
        int[] roundFourSetFour = {R.mipmap.flower1, R.mipmap.language, R.mipmap.mushroom, R.mipmap.puzzle};
        int[] roundFourSetFive = {R.mipmap.ball, R.mipmap.butterfly2, R.mipmap.sun3, R.mipmap.strawberry};

        //Gets a random number between one and five, returns an image set based on the random number
        Random random = new Random();
        int setNum = random.nextInt(5) + 1;
        switch (setNum){
            case 1: return roundFourSetOne;
            case 2: return roundFourSetTwo;
            case 3: return roundFourSetThree;
            case 4: return roundFourSetFour;
            case 5: return roundFourSetFive;
        }
        return null;
    }

    /**
     * Instantiates five different image sets for round five and returns a random set of images to
     * be used for the round
     * @return a random set of images for round five
     */
    public int[] roundFiveSets(){
        int[] roundFiveSetOne = {R.mipmap.aztec_calendar, R.mipmap.sharing, R.mipmap.sharing2, R.mipmap.sun2};
        int[] roundFiveSetTwo = {R.mipmap.pentagram, R.mipmap.pentagram2, R.mipmap.trinity, R.mipmap.trinity2};
        int[] roundFiveSetThree = {R.mipmap.snowflake, R.mipmap.snowflake2, R.mipmap.arrow, R.mipmap.arrows2};
        int[] roundFiveSetFour = {R.mipmap.rose, R.mipmap.sakura, R.mipmap.sakura2, R.mipmap.tulip2};
        int[] roundFiveSetFive = {R.mipmap.share, R.mipmap.sharing3, R.mipmap.sunrise, R.mipmap.sunset};

        //Gets a random number between one and five, returns an image set based on the random number
        Random random = new Random();
        int setNum = random.nextInt(5) + 1;
        switch (setNum){
            case 1: return roundFiveSetOne;
            case 2: return roundFiveSetTwo;
            case 3: return roundFiveSetThree;
            case 4: return roundFiveSetFour;
            case 5: return roundFiveSetFive;
        }
        return null;
    }

    /**
     * Instantiates five different image sets for round six and returns a random set of images to
     * be used for the round
     * @return a random set of images for round six
     */
    public int[] roundSixSets(){
        int[] roundSixSetOne = {R.mipmap.abstract2, R.mipmap.abstract3, R.mipmap.abstract4, R.mipmap.abstract5};
        int[] roundSixSetTwo = {R.mipmap.native_arrows, R.mipmap.native_peace, R.mipmap.native_camp, R.mipmap.teepee2};
        int[] roundSixSetThree = {R.mipmap.blobs, R.mipmap.blobs2, R.mipmap.blobs3, R.mipmap.blobs4};
        int[] roundSixSetFour = {R.mipmap.dice, R.mipmap.dice2, R.mipmap.dice3, R.mipmap.dice4};
        int[] roundSixSetFive = {R.mipmap.back, R.mipmap.download, R.mipmap.next, R.mipmap.up_arrow2};

        //Gets a random number between one and five, returns an image set based on the random number
        Random random = new Random();
        int setNum = random.nextInt(5) + 1;
        switch (setNum){
            case 1: return roundSixSetOne;
            case 2: return roundSixSetTwo;
            case 3: return roundSixSetThree;
            case 4: return roundSixSetFour;
            case 5: return roundSixSetFive;
        }
        return null;
    }

    /**
     * Instantiates five different image sets for round seven and returns a random set of images to
     * be used for the round
     * @return a random set of images for round seven
     */
    public int[] roundSevenSets(){
        int[] roundSevenSetOne = {R.mipmap.add, R.mipmap.attention, R.mipmap.check_mark, R.mipmap.check_mark2, R.mipmap.error};
        int[] roundSevenSetTwo = {R.mipmap.arrows, R.mipmap.down_arrow, R.mipmap.down_arrow2, R.mipmap.right_arrow2, R.mipmap.up_arrow};
        int[] roundSevenSetThree = {R.mipmap.chevron_down, R.mipmap.chevron_left, R.mipmap.chevron_right, R.mipmap.double_arrow_left, R.mipmap.double_arrow_right};
        int[] roundSevenSetFour = {R.mipmap.expand, R.mipmap.expand2, R.mipmap.shrink, R.mipmap.shrink2, R.mipmap.shrink3};
        int[] roundSevenSetFive = {R.mipmap.diamond3, R.mipmap.diamond4, R.mipmap.diamond5, R.mipmap.diamond6, R.mipmap.diamond7};

        //Gets a random number between one and five, returns an image set based on the random number
        Random random = new Random();
        int setNum = random.nextInt(5) + 1;
        switch (setNum){
            case 1: return roundSevenSetOne;
            case 2: return roundSevenSetTwo;
            case 3: return roundSevenSetThree;
            case 4: return roundSevenSetFour;
            case 5: return roundSevenSetFive;
        }
        return null;
    }

    /**
     * Instantiates five different image sets for round eight and returns a random set of images to
     * be used for the round
     * @return a random set of images for round eight
     */
    public int[] roundEightSets(){

        int[] roundEightSetOne = {R.mipmap.daisy, R.mipmap.daisy2, R.mipmap.flower_heart_petals, R.mipmap.flower_round_petals, R.mipmap.flower_round_petals2};
        int[] roundEightSetTwo = {R.mipmap.hexagon_dark, R.mipmap.circle2, R.mipmap.octagon, R.mipmap.octagon2, R.mipmap.pentagon2};
        int[] roundEightSetThree = {R.mipmap.house, R.mipmap.home, R.mipmap.home_door, R.mipmap.home_door_window, R.mipmap.home_heart};
        int[] roundEightSetFour = {R.mipmap.sun4, R.mipmap.sun5, R.mipmap.sun6, R.mipmap.sun7, R.mipmap.sun8};
        int[] roundEightSetFive = {R.mipmap.car, R.mipmap.garbage_truck, R.mipmap.pickup_truck, R.mipmap.truck, R.mipmap.van};

        //Gets a random number between one and five, returns an image set based on the random number
        Random random = new Random();
        int setNum = random.nextInt(5) + 1;
        switch (setNum){
            case 1: return roundEightSetOne;
            case 2: return roundEightSetTwo;
            case 3: return roundEightSetThree;
            case 4: return roundEightSetFour;
            case 5: return roundEightSetFive;
        }
        return null;
    }

    /**
     * Instantiates five different image sets for round nine and returns a random set of images to
     * be used for the round
     * @return a random set of images for round nine
     */
    public int[] roundNineSets(){
        int[] roundNineSetOne = {R.mipmap.hexagon3, R.mipmap.hexagon4, R.mipmap.rhombus, R.mipmap.hexagon5, R.mipmap.hexagon6};
        int[] roundNineSetTwo = {R.mipmap.triangle7, R.mipmap.triangle4, R.mipmap.triangle5, R.mipmap.triangles, R.mipmap.triangle6};
        int[] roundNineSetThree = {R.mipmap.gear, R.mipmap.gear2, R.mipmap.gear3, R.mipmap.gear4, R.mipmap.gear5};
        int[] roundNineSetFour = {R.mipmap.circle_angle_left, R.mipmap.circle_angle_right, R.mipmap.circles_four, R.mipmap.circles_three_left, R.mipmap.circles_three_right};
        int[] roundNineSetFive = {R.mipmap.night, R.mipmap.night2, R.mipmap.night3, R.mipmap.night4, R.mipmap.night5};

        //Gets a random number between one and five, returns an image set based on the random number
        Random random = new Random();
        int setNum = random.nextInt(5) + 1;
        switch (setNum){
            case 1: return roundNineSetOne;
            case 2: return roundNineSetTwo;
            case 3: return roundNineSetThree;
            case 4: return roundNineSetFour;
            case 5: return roundNineSetFive;
        }
        return null;
    }

    /**
     * Instantiates five different image sets for round ten and returns a random set of images to
     * be used for the round
     * @return a random set of images for round ten
     */
    public int[] roundTenSets(){
        int[] roundTenSetOne = {R.mipmap.floral_design, R.mipmap.floral_design2, R.mipmap.floral_design3, R.mipmap.floral_design4, R.mipmap.floral_design5};
        int[] roundTenSetTwo = {R.mipmap.sun_design, R.mipmap.sun_design2, R.mipmap.sun_design3, R.mipmap.sun_design4, R.mipmap.sun_design5};
        int[] roundTenSetThree = {R.mipmap.amaryllis, R.mipmap.anemone, R.mipmap.daffodil, R.mipmap.flower3, R.mipmap.narcissus};
        int[] roundTenSetFour = {R.mipmap.tree1, R.mipmap.tree2, R.mipmap.tree3, R.mipmap.tree4, R.mipmap.tree5};
        int[] roundTenSetFive = {R.mipmap.snowflake3, R.mipmap.snowflake4, R.mipmap.snowflake5, R.mipmap.snowflake6, R.mipmap.snowflake7};

        //Gets a random number between one and five, returns an image set based on the random number
        Random random = new Random();
        int setNum = random.nextInt(5) + 1;
        switch (setNum){
            case 1: return roundTenSetOne;
            case 2: return roundTenSetTwo;
            case 3: return roundTenSetThree;
            case 4: return roundTenSetFour;
            case 5: return roundTenSetFive;
        }
        return null;
    }
}
