package com.soteria.neurolab;

import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Random;

public class VisualMemoryActivity extends AppCompatActivity implements VisualMemoryRecyclerAdapter.ItemClickListener {

    VisualMemoryRecyclerAdapter visualAdapter;
    String level = "1";
    int rowColumnCount = 4;
    String lives = "5";
    int currentPos = 1;
    int maxPos = 5;
    GridLayout gameBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.title_visual_short_term_memory);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_visual_memory_game);
        gameBoard = findViewById(R.id.visual_memory_game_board);

        final Button startGameButton = findViewById(R.id.visual_memory_start_game);
        final TextView levelText = findViewById(R.id.visual_memory_current_level);
        final TextView livesText = findViewById(R.id.visual_memory_lives_counter);
        levelText.setText(getResources().getString(R.string.title_visual_short_term_memory));
        livesText.setVisibility(View.INVISIBLE);


        startGameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setUpGrid();
                levelText.setText(getResources().getString(R.string.visual_memory_game_current_level, level));
                livesText.setText(getResources().getString(R.string.visual_memory_game_lives_count, lives));
                livesText.setVisibility(View.VISIBLE);
                startGameButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position)
    {
        Toast.makeText(getApplicationContext(),"Clicked, retrieved" + visualAdapter.getItem(position),Toast.LENGTH_SHORT).show();
        //TextView temp = visualAdapter.getItem(position);
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

    private void setUpGrid() {
        gameBoard.setColumnCount(rowColumnCount);
        gameBoard.setRowCount(rowColumnCount);

        for( int i = 0; i < gameBoard.getChildCount(); i++ )
        {
            
        }
    }
}
