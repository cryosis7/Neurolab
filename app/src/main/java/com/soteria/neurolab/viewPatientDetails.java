package com.soteria.neurolab;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

public class viewPatientDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Patient Details");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_view_patient_details);


        //Button instructions for back button on Support Bar
        //TODO incorporate functionality to go back in the app, remove toast once done.


        //Button instructions for run games button
        //TODO add link to send users to patient home once created, remove toast once done.

        final Button runButton = findViewById(R.id.runGamesButton);
        runButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"This links to Patient Home",Toast.LENGTH_SHORT).show();
               // startActivity(new Intent(this, //TODO add link here));
            }
        });


        //Button instructions for manage patient button
        //TODO add link to send users to the manage patient screen once created, remove toast once done.

        final Button manageButton = findViewById(R.id.managePatientButton);
        manageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"This links to Manage Patients",Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(this, //TODO add link here));
            }
        });


        //Button instructions for view report button
        //TODO add link to send users to the view report screen once created, remove toast once done.

        final Button reportButton = findViewById(R.id.viewReportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"This links to View Report",Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(this, //TODO add link here));
            }
        });


        //Button instructions for delete patient
        //TODO add link to search patient once created, remove toast once done.

        final Button deleteButton = findViewById(R.id.deletePatientButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Deleted patient, sending to search patients",Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(this, //TODO add link here));
            }
        });


        //Button instructions for the home image button
        //TODO add link to search patient once created, remove toast once done.

        final ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Taking user home to search patients",Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(this, //TODO add link here));
            }
        });

    }
}
