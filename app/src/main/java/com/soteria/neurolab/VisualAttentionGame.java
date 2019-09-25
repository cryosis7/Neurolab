package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Random;

public class VisualAttentionGame extends AppCompatActivity {

    private int roundCount = 1;
    private int numOfTargets = 0;
    private int numOfTaps = 0;
    private int targetsFound = 0;
    private double RoundScore = 0;
    private double totalScore = 0;
    private int patientID;
    private int buttonsHorizontal;
    private int buttonsVertical;
    ImageButton[][] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpRounds();

        /*Intent intent = getIntent();
        patientID = intent.getIntExtra("PATIENT_ID", -1);
        if (patientID == -1)
            throw new IllegalArgumentException("Expected Extra 'PATIENT_ID' in Intent - Received none");*/
    }

    public void setUpRounds(){

        if(roundCount == 1 || roundCount == 2 || roundCount == 3){
            buttonsHorizontal = 4;
            buttonsVertical = 4;
            setContentView(R.layout.activity_visual_attention_game_grid4x4);
        } else if(roundCount == 4 || roundCount == 5 || roundCount == 6){
            buttonsHorizontal = 5;
            buttonsVertical = 5;
            setContentView(R.layout.activity_visual_attention_game_grid5x5);
        } else if(roundCount == 7 || roundCount == 8){
            buttonsHorizontal = 6;
            buttonsVertical = 6;
        } else if(roundCount == 9 || roundCount == 10){
            buttonsHorizontal = 7;
            buttonsVertical = 7;
        }

        buttons = new ImageButton[buttonsVertical][buttonsHorizontal];

        final TextView roundNumText = findViewById(R.id.visual_attention_round_text);
        TextView targetsFoundText = findViewById(R.id.visual_attention_found_text);
        final ImageView targetImage = findViewById(R.id.visual_attention_target_image);

        roundNumText.setText("Round " + roundCount);
        int[] imageSet = getImageSets(roundCount);
        Integer randomTarget = getRandomImage(imageSet);
        targetImage.setImageDrawable(resizeImages(randomTarget));
        targetImage.setTag(randomTarget);
        int[] roundImages = imageArray(imageSet, randomTarget);
        int count = 0;

        for (int i = 0; i < buttonsVertical; i++) {
            for (int j = 0; j < buttonsHorizontal; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setImageDrawable(resizeImages(roundImages[count]));
                buttons[i][j].setTag(roundImages[count]);
                count++;

                if (buttons[i][j].getTag().equals(targetImage.getTag())) {
                    numOfTargets++;
                }
                setTargetsFound(targetsFoundText, targetsFound, numOfTargets);
                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        numOfTaps++;
                        TextView targetsFoundText = findViewById(R.id.visual_attention_found_text);
                        if (view.getTag().equals(targetImage.getTag())) {
                            targetsFound++;
                            view.setClickable(false);
                            view.setBackgroundResource(R.drawable.visual_attention_target_button);
                            if (targetsFound == numOfTargets && roundCount != 10) {
                                roundCount++;
                                numOfTargets = 0; numOfTaps = 0; targetsFound = 0;
                                resetButtons(buttonsVertical, buttonsHorizontal);
                                setUpRounds();
                            }
                        }
                        setTargetsFound(targetsFoundText, targetsFound, numOfTargets);
                    }
                });
            }
        }
    }

    public int[] imageArray(int[] roundImages, int target){
        Random rand = new Random();
        int targetSymbols = 0;
        int[] images = null;

        if(roundCount == 1 || roundCount == 2 || roundCount == 3){
            targetSymbols = 4 + rand.nextInt((5 - 4) + 1);
            images = new int[16];
        } else if(roundCount == 4 || roundCount == 5 || roundCount == 6){
            images = new int[25];
            targetSymbols = 5 + rand.nextInt((6 - 5) + 1);
        } else if(roundCount == 7 || roundCount == 8){
            images = new int[36];
            targetSymbols = 7 + rand.nextInt((8 - 7) + 1);
        } else if(roundCount == 9 || roundCount == 10){
            images = new int[49];
            targetSymbols = 9 + rand.nextInt((10 - 9) + 1);
        }

        for(int i = 0; i < targetSymbols; i++){
            images[i] = target;
        }

        for (int i = targetSymbols; i < images.length; i++){
            int randImage = getRandomImage(roundImages);
            while (randImage == target){
                randImage = getRandomImage(roundImages);
            }
            images[i] = randImage;
        }

        int[] firstShuffle = shuffleImages(images);
        return shuffleImages(firstShuffle);
    }

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

    public void resetButtons(int buttonsVertical, int buttonsHorizontal){
        buttons = new ImageButton[buttonsVertical][buttonsHorizontal];
        for (int i = 0; i < buttonsVertical; i++) {
            for (int j = 0; j < buttonsHorizontal; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setClickable(true);
                buttons[i][j].setBackgroundResource(R.drawable.visual_attention_button);
            }
        }
    }

    public void setTargetsFound(TextView text, int targetsFound, int numOfTargets){
        text.setText("Found " + targetsFound + " / " + numOfTargets);
    }

    public double calculateRoundScore(int numOfTargets, int numOfTaps) {
        return (numOfTargets * 100D) / numOfTaps;
    }

    public Drawable resizeImages(int image){
        Drawable dr = getResources().getDrawable(image);
        Bitmap bm = ((BitmapDrawable) dr).getBitmap();
        Drawable drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bm, 105, 105, true));
        return drawable;
    }

    public Integer getRandomImage(int[] list){
        Random rand = new Random();
        return list[rand.nextInt(list.length)];
    }

    public int[] getImageSets(int roundCount){
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

    public int[] roundOneSets(){
        int[] roundOneSetOne = {R.mipmap.circle, R.mipmap.diamond, R.mipmap.plus1, R.mipmap.star};
        int[] roundOneSetTwo = {R.mipmap.checked, R.mipmap.heart, R.mipmap.moon, R.mipmap.star2};
        int[] roundOneSetThree = {R.mipmap.plus, R.mipmap.star3, R.mipmap.sunny, R.mipmap.heart2};
        int[] roundOneSetFour = {R.mipmap.carrot, R.mipmap.leaf, R.mipmap.flower, R.mipmap.notification};
        int[] roundOneSetFive = {R.mipmap.butterfly, R.mipmap.clover, R.mipmap.like, R.mipmap.sun};

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

    public int[] roundTwoSets(){
        int[] roundTwoSetOne = {R.mipmap.crown, R.mipmap.clock, R.mipmap.circle_green, R.mipmap.rainbow};
        int[] roundTwoSetTwo = {R.mipmap.apple, R.mipmap.crown2, R.mipmap.trophy, R.mipmap.unchecked};
        int[] roundTwoSetThree = {R.mipmap.cake, R.mipmap.flash, R.mipmap.lotus_flower, R.mipmap.unchecked2};
        int[] roundTwoSetFour = {R.mipmap.driving, R.mipmap.hourglass, R.mipmap.rocket, R.mipmap.smiley};
        int[] roundTwoSetFive = {R.mipmap.light_bulb, R.mipmap.star4, R.mipmap.target, R.mipmap.tulip};

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

    public int[] roundThreeSets(){
        int[] roundThreeSetOne = {R.mipmap.christian_cross, R.mipmap.pentagon, R.mipmap.plain_circle, R.mipmap.six_pointed_star};
        int[] roundThreeSetTwo = {R.mipmap.cancel, R.mipmap.check, R.mipmap.padlock, R.mipmap.tumble_dry};
        int[] roundThreeSetThree = {R.mipmap.hexagon, R.mipmap.peace_symbol, R.mipmap.plain_square, R.mipmap.plain_star};
        int[] roundThreeSetFour = {R.mipmap.no_entry_sign, R.mipmap.plain_heart, R.mipmap.plain_triangle, R.mipmap.plus_sign};
        int[] roundThreeSetFive = {R.mipmap.casino, R.mipmap.right_arrow, R.mipmap.stop, R.mipmap.yield};

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

    public int[] roundFourSets(){
        int[] roundFourSetOne = {R.mipmap.bone, R.mipmap.check_box, R.mipmap.shining_sun, R.mipmap.star_and_crescent};
        int[] roundFourSetTwo = {R.mipmap.key, R.mipmap.log_in_button, R.mipmap.setting, R.mipmap.weather};
        int[] roundFourSetThree = {R.mipmap.crown4, R.mipmap.kite, R.mipmap.startup, R.mipmap.target2};
        int[] roundFourSetFour = {R.mipmap.flower1, R.mipmap.language, R.mipmap.mushroom, R.mipmap.puzzle};
        int[] roundFourSetFive = {R.mipmap.ball, R.mipmap.butterfly2, R.mipmap.mailbox, R.mipmap.strawberry};


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

    public int[] roundFiveSets(){
        int[] roundFiveSetOne = {R.mipmap.abstract2, R.mipmap.abstract3, R.mipmap.abstract4, R.mipmap.abstract5};
        int[] roundFiveSetTwo = {R.mipmap.native_arrows, R.mipmap.native_peace, R.mipmap.native_camp, R.mipmap.teepee2};
        int[] roundFiveSetThree = {R.mipmap.blobs, R.mipmap.blobs2, R.mipmap.blobs3, R.mipmap.blobs4};
        int[] roundFiveSetFour = {R.mipmap.dice, R.mipmap.dice2, R.mipmap.dice3, R.mipmap.dice4};
        int[] roundFiveSetFive = {R.mipmap.back, R.mipmap.download, R.mipmap.next, R.mipmap.up_arrow2};

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

    public int[] roundSixSets(){
        int[] roundSixSetOne = {R.mipmap.aztec_calendar, R.mipmap.sharing, R.mipmap.sharing2, R.mipmap.sun2};
        int[] roundSixSetTwo = {R.mipmap.pentagram, R.mipmap.pentagram2, R.mipmap.trinity, R.mipmap.trinity2};
        int[] roundSixSetThree = {R.mipmap.snowflake, R.mipmap.snowflake2, R.mipmap.arrow, R.mipmap.arrows2};
        int[] roundSixSetFour = {R.mipmap.rose, R.mipmap.sakura, R.mipmap.sakura2, R.mipmap.tulip2};
        int[] roundSixSetFive = {R.mipmap.share, R.mipmap.sharing3, R.mipmap.sunrise, R.mipmap.sunset};

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

    public int[] roundSevenSets(){
        int[] roundSevenSetOne = {R.mipmap.add, R.mipmap.attention, R.mipmap.check_mark, R.mipmap.check_mark2, R.mipmap.error};
        int[] roundSevenSetTwo = {R.mipmap.arrows, R.mipmap.down_arrow, R.mipmap.down_arrow2, R.mipmap.right_arrow2, R.mipmap.up_arrow};
        int[] roundSevenSetThree = {R.mipmap.chevron_down, R.mipmap.chevron_left, R.mipmap.chevron_right, R.mipmap.double_arrow_left, R.mipmap.double_arrow_right};
        int[] roundSevenSetFour = {R.mipmap.expand, R.mipmap.expand2, R.mipmap.shrink, R.mipmap.shrink2, R.mipmap.shrink3};

        Random random = new Random();
        int setNum = random.nextInt(4) + 1;
        switch (setNum){
            case 1: return roundSevenSetOne;
            case 2: return roundSevenSetTwo;
            case 3: return roundSevenSetThree;
            case 4: return roundSevenSetFour;
        }
        return null;
    }

    public int[] roundEightSets(){
        int[] roundEightSetOne = {R.mipmap.floral_design, R.mipmap.floral_design2, R.mipmap.floral_design3, R.mipmap.floral_design4, R.mipmap.floral_design5};
        int[] roundEightSetTwo = {R.mipmap.gear, R.mipmap.gear2, R.mipmap.gear3, R.mipmap.gear4, R.mipmap.gear5};
        int[] roundEightSetThree = {R.mipmap.amaryllis, R.mipmap.anemone, R.mipmap.daffodil, R.mipmap.flower3, R.mipmap.narcissus};

        Random random = new Random();
        int setNum = random.nextInt(3) + 1;
        switch (setNum){
            case 1: return roundEightSetOne;
            case 2: return roundEightSetTwo;
            case 3: return roundEightSetThree;
        }
        return null;
    }

    public int[] roundNineSets(){
        int[] roundNineSetOne = {R.mipmap.hexagon3, R.mipmap.hexagon4, R.mipmap.rhombus, R.mipmap.hexagon5, R.mipmap.hexagon6};
        int[] roundNineSetTwo = {R.mipmap.triangle3, R.mipmap.triangle4, R.mipmap.triangle5, R.mipmap.triangles};
        int[] roundNineSetThree = {R.mipmap.distribute, R.mipmap.distribute2, R.mipmap.horizontal, R.mipmap.horizontal2, R.mipmap.horizontal3};

        Random random = new Random();
        int setNum = random.nextInt(3) + 1;
        switch (setNum){
            case 1: return roundNineSetOne;
            case 2: return roundNineSetTwo;
            case 3: return roundNineSetThree;
        }
        return null;
    }

    public int[] roundTenSets(){
        int[] roundTenSetOne = {R.mipmap.circle_angle_left, R.mipmap.circle_angle_right, R.mipmap.circles_four, R.mipmap.circles_three_left, R.mipmap.circles_three_right};
        int[] roundTenSetTwo = {R.mipmap.square, R.mipmap.square2, R.mipmap.square3, R.mipmap.trapezium, R.mipmap.trapezium2};
        int[] roundTenSetThree = {R.mipmap.x, R.mipmap.x2, R.mipmap.x3, R.mipmap.x4};
        int[] roundTenSetFour = {R.mipmap.hexagon_dark, R.mipmap.hexagon_dark2, R.mipmap.octagon, R.mipmap.octagon2, R.mipmap.octagon3};

        Random random = new Random();
        int setNum = random.nextInt(4) + 1;
        switch (setNum){
            case 1: return roundTenSetOne;
            case 2: return roundTenSetTwo;
            case 3: return roundTenSetThree;
            case 4: return roundTenSetFour;
        }
        return null;
    }
}
