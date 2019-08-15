package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Random;
// B===3
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
        new GameInstance().execute();
        ((ViewGroup) view.getParent()).removeView(view);
    }

    private class GameInstance extends AsyncTask<Context, Integer, Integer> {

        @Override
        protected Integer doInBackground(Context... params) {
            Random random = new Random();

            for (int round = 0; round < 5; round++) {
                int delay = random.nextInt(5000) + 2000;
                try {
                    Thread.sleep(delay);
                    publishProgress( + 1, delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return 8008135;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(), values[0]  + " - " + values[1] + "ms", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Integer score) {
            super.onPostExecute(score);
            Toast.makeText(getApplicationContext(), score, Toast.LENGTH_SHORT).show();

            Intent launchGame = new Intent(getApplicationContext(), tempLauncherActivity.class);
            launchGame.putExtra("gameScore", score);
            startActivity(launchGame);
        }
    }
}
