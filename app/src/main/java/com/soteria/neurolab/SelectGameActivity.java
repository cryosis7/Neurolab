package com.soteria.neurolab;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.soteria.neurolab.utilities.PasswordAuthentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectGameActivity extends AppCompatActivity {
    private RecyclerView gameListView;
    private SelectGameRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private int patientID;
    private Map<String, Class> gameClassMap;
    private List<String> dataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
        Intent intent = getIntent();
        patientID = intent.getIntExtra("PATIENT_ID", -1);
        if (patientID == -1)
            throw new IllegalArgumentException("Expected Int Extra 'PATIENT_ID' in Intent - Received none");

        gameClassMap = initMap();
        dataSet = new ArrayList<>(gameClassMap.keySet()); //TODO: Replace with only their game assignments.

        // Initialise Recycler View
        gameListView = findViewById(R.id.select_game_list);
        gameListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        gameListView.setLayoutManager(layoutManager);
        mAdapter = new SelectGameRecyclerAdapter(dataSet);
        mAdapter.setClickListener(new SelectGameRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String gameName = dataSet.get(position);
                Intent intent = new Intent(view.getContext(), gameClassMap.get(gameName));
                intent.putExtra("PATIENT_ID", patientID);
                startActivity(intent);
            }
        });
        gameListView.setAdapter(mAdapter);
        Log.i("SelectGameActivity", "Recycler View Initialised.");
    }

    /**
     * Overriding the on back pressed to require a password prompt first.
     */
    @Override
    public void onBackPressed() {
        validate();
    }

    /**
     * Creates an alert dialog that prompts the user for the password.
     * Updates the validPassword to the result.
     */
    private void validate() {
        // Get stored password
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.apply();
        final String storedHash = pref.getString("passwordHash", null);
        if (storedHash == null) throw new RuntimeException("No password has been set");

        // Build alert dialog
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.requestFocus();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input.getText().toString();

                        PasswordAuthentication authenticator = new PasswordAuthentication();
                        if (authenticator.authenticate(password.toCharArray(), storedHash))
                            SelectGameActivity.super.onBackPressed(); // Triggers normal back button, skipping the rest of the function.

                        dialog.dismiss();
                        Toast.makeText(SelectGameActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    /**
     * Initiates the map that pairs game names to the names of their classes.
     *
     * @return A map of {string : Class} i.e. "Reaction Time" : ReactionGameActivity.class
     */
    private Map<String, Class> initMap() {
        Map map = new HashMap<>();
        map.put(getResources().getString(R.string.title_reaction_time), ReactionGameActivity.class);
//        map.put(getResources().getString(R.string.title_visual_short_term_memory), VisualMemoryActivity.class); //TODO: Uncomment as the games are made.
//        map.put(getResources().getString(R.string.title_selective_attention), VisualAttentionActivity.class);
        return map;
    }
}
