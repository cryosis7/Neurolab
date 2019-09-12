package com.soteria.neurolab;

import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

public class VisualMemoryActivity extends AppCompatActivity {

    int level = 1;
    int lives = 5;
    SparseIntArray memoryPattern = new SparseIntArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.title_visual_short_term_memory);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_visual_memory_game);

        final Button startGameButton = findViewById(R.id.visual_memory_start_game);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GridLayout patternGridLayout = findViewById(R.id.visual_memory_pattern_grid);
                Button patternButton = new Button(VisualMemoryActivity.this);
                patternButton.setLayoutParams(new GridLayout.LayoutParams());

                patternGridLayout.removeAllViews();
                for(int i = 0; i < patternGridLayout.getChildCount(); i++ )
                {
                    patternGridLayout.addView(patternButton, i);
                }

                startGameButton.setVisibility(View.INVISIBLE);
            }
        });
    }

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
}
