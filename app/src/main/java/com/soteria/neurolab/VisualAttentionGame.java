package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
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

    private ImageButton[][] buttons = new ImageButton[5][5];
    private int roundCount = 1;
    private int numOfTargets = 0;
    private int numOfTaps = 0;
    private int targetsFound = 0;
    private double RoundScore = 0;
    private double totalScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_attention_game);
        setUpRounds();
    }

    public void setUpRounds(){
        final TextView roundNum = findViewById(R.id.visual_attention_round_text);
        TextView targetsFoundText = findViewById(R.id.visual_attention_found_text);
        final ImageView targetImage = findViewById(R.id.visual_attention_target_image);

        roundNum.setText("Round " + roundCount);
        int[] roundImages = getRoundImages(roundCount);
        Integer randomTarget = getRandomImages(roundImages);
        targetImage.setImageDrawable(resizeImages(randomTarget));
        targetImage.setTag(randomTarget);
        int[] image_Array = imageArray(roundImages, randomTarget);
        int count = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                //int image = shuffleImages(roundImages);
                buttons[i][j].setImageDrawable(resizeImages(image_Array[count]));
                buttons[i][j].setTag(image_Array[count]);
                Log.i("counter", "count: " + count);
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
                            if (targetsFound == numOfTargets && roundCount != 4) {
                                Log.i("round score", "score: " + calculateRoundScore(numOfTargets, numOfTaps));
                                roundCount++;
                                numOfTargets = 0; numOfTaps = 0; targetsFound = 0;
                                resetButtons();
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
        int randNum = 5 + rand.nextInt((6 - 5) + 1);
        int[] images = new int[25];
        for(int i = 0; i < randNum; i++){
            images[i] = target;
        }

        if(randNum == 5){
            for (int i = 5; i < images.length; i++){
                int randImage = getRandomImages(roundImages);
                while (randImage == target){
                    randImage = getRandomImages(roundImages);
                }
                images[i] = randImage;
            }
        }

        if(randNum == 6){
            for (int i = 6; i < images.length; i++){
                int randImage = getRandomImages(roundImages);
                while (randImage == target){
                    randImage = getRandomImages(roundImages);
                }
                images[i] = randImage;
            }
        }
        return shuffleImages(images);
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

    public void resetButtons(){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
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
        Drawable drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bm, 130, 130, true));
        return drawable;
    }

    public Integer getRandomImages(int[] list){
        Random rand = new Random();
        return list[rand.nextInt(list.length)];
    }

    public int[] getRoundImages(int roundCount){
        switch (roundCount){
            case 1:
                return roundOneSets();
            case 2:
                return roundTwoSets();
            case 3:
                return roundThreeSets();
            case 4:
                return roundFourSets();
        }
        return null;
    }

    public int[] roundOneSets(){
        int[] roundOneSetOne = {R.mipmap.circle, R.mipmap.crown, R.mipmap.diamond, R.mipmap.flower, R.mipmap.star};
        int[] roundOneSetTwo = {R.mipmap.checked, R.mipmap.heart, R.mipmap.moon, R.mipmap.notification, R.mipmap.star2};
        int[] roundOneSetThree = {R.mipmap.plus, R.mipmap.star3, R.mipmap.sunny, R.mipmap.crown2, R.mipmap.heart2};

        Random random = new Random();
        int setNum = random.nextInt(3) + 1;
        switch (setNum){
            case 1:
                return roundOneSetOne;
            case 2:
                return roundOneSetTwo;
            case 3:
                return roundOneSetThree;
        }
        return null;
    }

    public int[] roundTwoSets(){
        int[] roundTwoSetOne = {R.mipmap.check_box, R.mipmap.christian_cross, R.mipmap.pentagon, R.mipmap.plain_circle, R.mipmap.six_pointed_star};
        int[] roundTwoSetTwo = {R.mipmap.cancel, R.mipmap.check, R.mipmap.padlock, R.mipmap.shining_sun, R.mipmap.tumble_dry};
        int[] roundTwoSetThree = {R.mipmap.hexagon, R.mipmap.log_in_button, R.mipmap.peace_symbol, R.mipmap.plain_square, R.mipmap.plain_star};
        int[] roundTwoSetFour = {R.mipmap.no_entry_sign, R.mipmap.plain_heart, R.mipmap.plain_triangle, R.mipmap.plus_sign, R.mipmap.star_and_crescent};

        Random random = new Random();
        int setNum = random.nextInt(4) + 1;
        switch (setNum){
            case 1:
                return roundTwoSetOne;
            case 2:
                return roundTwoSetTwo;
            case 3:
                return roundTwoSetThree;
            case 4:
                return roundTwoSetFour;
        }
        return null;
    }

    public int[] roundThreeSets(){
        int[] roundThreeSetOne = {R.mipmap.crown3, R.mipmap.dots, R.mipmap.hexagon2, R.mipmap.leo_sign, R.mipmap.sagittarius_arrow_sign};

        Random random = new Random();
        int setNum = random.nextInt(1) + 1;
        switch (setNum){
            case 1:
                return roundThreeSetOne;
        }
        return null;
    }

    public int[] roundFourSets(){
        int[] roundFourSetOne = {R.mipmap.hexagon3, R.mipmap.hexagon4, R.mipmap.rhombus, R.mipmap.triangle, R.mipmap.triangle2};

        Random random = new Random();
        int setNum = random.nextInt(1) + 1;
        switch (setNum){
            case 1:
                return roundFourSetOne;
        }
        return null;
    }
}
