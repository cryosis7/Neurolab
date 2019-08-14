package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class tempLauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_launcher);
    }

    public void launchReactionGame(View view) {
        Toast.makeText(getApplicationContext(), "Launching Game", Toast.LENGTH_SHORT).show();
    }
}
