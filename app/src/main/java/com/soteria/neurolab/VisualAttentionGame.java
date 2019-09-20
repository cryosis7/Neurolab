package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VisualAttentionGame extends AppCompatActivity {

    private ImageButton[][] buttons = new ImageButton[5][5];
    private int roundCount = 1;
    private int numOfTargets = 0;
    private int numOfTaps = 0;
    private int targetsFound = 0;
    private int incorrectSymbols = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_attention_game);

        int[] roundOneImages = {R.drawable.circle, R.drawable.crown, R.drawable.diamond, R.drawable.flower, R.drawable.star};

        final TextView roundNum = findViewById(R.id.visual_attention_round_text);
        TextView targetsFoundText = findViewById(R.id.visual_attention_found_text);
        final ImageView targetImage = findViewById(R.id.visual_attention_target_image);

        roundNum.setText("Round " + roundCount);
        Integer randomTarget = shuffleImages(roundOneImages);
        targetImage.setImageDrawable(resizeImages(randomTarget));
        targetImage.setTag(randomTarget);

        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                int image = shuffleImages(roundOneImages);
                buttons[i][j].setImageDrawable(resizeImages(image));
                buttons[i][j].setTag(image);

                if(buttons[i][j].getTag().equals(targetImage.getTag())){
                    numOfTargets++;
                }
                setTargetsFound(targetsFoundText, targetsFound, numOfTargets);
                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView targetsFoundText = findViewById(R.id.visual_attention_found_text);
                        if(view.getTag().equals(targetImage.getTag())){
                            targetsFound++;
                            view.setClickable(false);
                            view.setBackgroundResource(R.drawable.visual_attention_button_pressed);
                            if(targetsFound == numOfTargets){

                            }
                        } else{
                            incorrectSymbols++;
                        }
                        numOfTaps++;
                        setTargetsFound(targetsFoundText, targetsFound, numOfTargets);
                    }
                });
            }
        }
    }

    public void setTargetsFound(TextView text, int targetsFound, int numOfTargets){
        text.setText("Found " + targetsFound + " / " + numOfTargets);
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
}
