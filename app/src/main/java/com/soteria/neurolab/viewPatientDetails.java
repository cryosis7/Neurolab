package com.soteria.neurolab;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class viewPatientDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState/*, String patientIdentifier, Date lastDayPlayed */ ) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Patient Details");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_view_patient_details);


        //Information for changing the text information on load
        //TODO pass through patient identifier and last game run information when completed

        /*
        final TextView patientID = findViewById(R.id.patientIDTitleTextView);
        final TextView lastPlayed = findViewById(R.id.patientGamesLastRunTextView);

        patientID.setText("Patient: " + patientIdentifier );

        DateFormat convertDate = new SimpleDateFormat("dd-mm-yyyy hh:mm");
        String inputDate = convertDate.format(lastDayPlayed);
        lastPlayed.setText("Games last run: " + inputDate );
         */


        //Button instructions for run games button
        //TODO add link to send users to patient home once created, remove toast once done.

        final Button runButton = findViewById(R.id.runGamesButton);
        runButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"This links to Patient Home",Toast.LENGTH_SHORT).show();
               // startActivity(new Intent(this, TODO add link here));
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

    public boolean onOptionsItemSelected(MenuItem menu)
    {
        try {
             switch(menu.getItemId()) {
                 case android.R.id.home:
                     super.onBackPressed();
                     return true;
                 default:
                     Toast.makeText(getApplicationContext(), "ERROR - Unknown call, terminating function call", Toast.LENGTH_SHORT).show();
                     return false;
             }
        } catch(Exception e) {
                Toast.makeText(getApplicationContext(),"EXCEPTION " + e + " occurred!",Toast.LENGTH_SHORT).show();
                return false;
        }
    }
}
