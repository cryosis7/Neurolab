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
        Integer randomTarget = shuffleImages(roundImages);
        targetImage.setImageDrawable(resizeImages(randomTarget));
        targetImage.setTag(randomTarget);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                int image = shuffleImages(roundImages);
                buttons[i][j].setImageDrawable(resizeImages(image));
                buttons[i][j].setTag(image);

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
                            view.setBackgroundColor(Color.YELLOW);
                            if (targetsFound == numOfTargets) {
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

    public Integer shuffleImages(int[] list){
        Random rand = new Random();
        return list[rand.nextInt(list.length)];
    }

    public int[] getRoundImages(int roundCount){
        switch (roundCount){
            case 1:
                return roundOneSets();
            case 2:
                return roundTwoSets();
        }
        return null;
    }

    public int[] roundOneSets(){
        int[] roundOneSetOne = {R.mipmap.circle, R.mipmap.crown, R.mipmap.diamond, R.mipmap.flower, R.mipmap.star};
        int[] roundOneSetTwo = {R.mipmap.checked, R.mipmap.heart, R.mipmap.moon, R.mipmap.notification, R.mipmap.star2};
        int[] roundOneSetThree = {R.mipmap.plus, R.mipmap.square, R.mipmap.star3, R.mipmap.sunny};

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

        Random random = new Random();
        int setNum = 1;
        switch (setNum){
            case 1:
                return roundTwoSetOne;
        }
        return null;
    }
}
