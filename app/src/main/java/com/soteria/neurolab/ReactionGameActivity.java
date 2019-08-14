package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Random;

public class ReactionGameActivity extends AppCompatActivity {

    private String patientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_game);
        Intent intent = getIntent();
        if (intent.hasExtra("patientID"))
            patientID = intent.getStringExtra("patientID");
        else throw new IllegalArgumentException("Expected Extra 'patientID' in Intent - Received Null");
    }

    public void startGame(View view) {
        runGame();
        ((ViewGroup) view.getParent()).removeView(view);
    }

    private void runGame() {
        Random random = new Random();

        for (int round = 0; round < 5; round++) {
            int delay = random.nextInt(10000) + 2000;
            try {
                Thread.sleep(delay);
                Toast.makeText(getApplicationContext(), "Time Up", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
