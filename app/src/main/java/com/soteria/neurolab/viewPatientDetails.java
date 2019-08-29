package com.soteria.neurolab;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class viewPatientDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* Variable declarations
         */
        String patientIdentifier, lastDayPlayed, lastTimePlayed;

        /* Attempts to retrieve data from an intent. If intent retrieval fails, insert placeholder data instead
         */
        try {
            Bundle viewBundle = getIntent().getExtras();
            patientIdentifier = viewBundle.getString("pID");
            lastDayPlayed = viewBundle.getString("pLastDayPlayed");
            lastTimePlayed = viewBundle.getString( "pLastTimePlayed");
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(),"No patients passed, go back to try again",Toast.LENGTH_SHORT).show();
            patientIdentifier = "/0";
            lastDayPlayed = "01/01/2019";
            lastTimePlayed = "00:00";
        }

        /*  Update the support action bar to set the back button and title to appropriate values
         */
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.view_patient_details_action_bar_title));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_view_patient_details);

        /*  Sets all buttons and text views to variables for later use
         */
        final TextView patientID = findViewById(R.id.patientIDTitleTextView);
        final TextView lastPlayed = findViewById(R.id.patientGamesLastRunTextView);
        final Button runButton = findViewById(R.id.runGamesButton);
        final Button manageButton = findViewById(R.id.managePatientButton);
        final Button reportButton = findViewById(R.id.viewReportButton);
        final Button deleteButton = findViewById(R.id.deletePatientButton);

        /* Hides buttons for patient access if no patient is passed through. This will only occur
           as a result of an error in passing information. Prevents progression
         */
        if(patientIdentifier.equals("/0"))
        {
            runButton.setVisibility(View.GONE);
            manageButton.setVisibility(View.GONE);
            reportButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

        /*Code for changing the patientID and lastGamesRun text views to display the information
          passed through to it from another page. */
        patientID.setText(getResources().getString(R.string.view_patient_details_patient_identifier, patientIdentifier));
        lastPlayed.setText(getString(R.string.view_patient_details_last_date_played, lastDayPlayed, lastTimePlayed));


        /*When run games is pressed, sends user to the patient profile
        TODO add link to send users to patient home once created, remove toast and comment markers  once done. */
        runButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"RUN GAMES PRESSED: Sending to patient home",Toast.LENGTH_SHORT).show();
               /*
               Intent gameIntent = new Intent(this, TODO add link to patient home here)
               gameIntent.putExtra("pID", patientID);
               startActivity(gameIntent);
                */
            }
        });

        /*When manage patient button is pressed, send the user to the manage patient screen for the
          current patient.
          TODO add link to send users to the manage patient screen once created, remove toast and comment markers  once done. */
        manageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"MANAGE PATIENT PRESSED: Sending to manage patient screen",Toast.LENGTH_SHORT).show();
                /*
               Intent manageIntent = new Intent(this, TODO add link to manage patient here)
               manageIntent.putExtra("pID", patientID);
               startActivity(manageIntent);
                */
            }
        });

        /*When the view report button is pressed, send the user to the report screen for the current
          patient.
          TODO add link to send users to the view report screen once created, remove toast and comment markers once done. */
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"VIEW REPORT PRESSED: Sending to report screen",Toast.LENGTH_SHORT).show();
                /*
               Intent reportIntent = new Intent(this, TODO add link to reports here)
               reportIntent.putExtra("pID", patientID);
               startActivity(reportIntent);
                */
            }
        });

        /*When the delete patient button is pressed, ask the user for confirmation. If they agree,
          remove the patient from the database and send the user to the create patient page.
          TODO add link to search patient and remove patient from database once created, remove toast once done. */
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Build the alert dialog warning the user of their action.
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(viewPatientDetails.this);
                deleteBuilder.setTitle("Deleting Patient");
                deleteBuilder.setMessage(getString(R.string.view_patient_details_delete_patient_dialog));
                //If delete is pressed, delete the patient and send the user to the search patients screen
                deleteBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO enter code to remove patient from database here
                        dialogInterface.cancel();
                        Toast.makeText(getApplicationContext(),"DELETE PRESSED: Removed patient, send to search",Toast.LENGTH_SHORT).show();
                        //TODO remove comment once merged with SearchCreateDeleteActivity
                        //startActivity(new Intent(this, SearchCreateDeleteActivity.class));
                    }
                });
                //If cancel is pressed, close the alert dialog.
                deleteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                final AlertDialog deleteConfirm = deleteBuilder.create();
                deleteConfirm.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0 ) {
                        deleteConfirm.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                });
                deleteConfirm.show();
            }
        });

        /*When the home button is pressed, send the user to the search patient screen
          TODO add link to search patient once created, remove toast once done. */
        final ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"HOME BUTTON PRESSED: Sending to search patients screen",Toast.LENGTH_SHORT).show();
                //TODO remove comment once merged with SearchCreateDeleteActivity
                /*startActivity(new Intent(this, SearchCreateDeleteActivity.class));
                  finish();*/
            }
        });
    }

    /** Sets the actions bar to include an overflow menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        /*Grabs the information from the overflow_menu resource in the menu folder and sets them
          to the action bar*/
        try {
            getMenuInflater().inflate(R.menu.overflow_menu, menu);
            return true;
        } catch ( Exception e ) {
            Toast.makeText(getApplicationContext(),"EXCEPTION " + e + " occurred!",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /** Determines the behaviour of options selected from the action bar.
     *  TODO insert link for logout and database functions for removal
     */
    public boolean onOptionsItemSelected(MenuItem menu)
    {
        try {
             switch(menu.getItemId()) {
                 //If the back button is pressed, return the user to the last visited page
                 case android.R.id.home:
                     super.onBackPressed();
                     return true;
                 //If the disclaimer button is pressed, display the disclaimer in an alert dialog
                 case R.id.action_disclaimer:
                     final AlertDialog.Builder disclaimerBuilder = new AlertDialog.Builder(this );
                     disclaimerBuilder.setTitle("Disclaimer");
                     disclaimerBuilder.setMessage(getString(R.string.disclaimer_body));
                     //If the confirm button is pressed, close the dialog
                     disclaimerBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()  {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             dialogInterface.cancel();
                         }
                     });
                     final AlertDialog showDisclaimer = disclaimerBuilder.create();
                     //Change the button colour of the alert dialog to be the primary colour
                     showDisclaimer.setOnShowListener( new DialogInterface.OnShowListener() {
                         @Override
                         public void onShow(DialogInterface arg0 ) {
                             showDisclaimer.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                         }
                     });
                     showDisclaimer.show();
                     return true;
                  //If the log out button is pressed, send the user to the log in screen
                 case R.id.action_logout:
                     //TODO link to logout, remove toast afterwards
                     Toast.makeText(getApplicationContext(), "LOGOUT PRESSED - Going to log in screen", Toast.LENGTH_SHORT).show();
                     /*startActivity(new Intent(this, //TODO add link to log in here));
                       finish();
                     */
                     return true;
                  //If an unknown option is selected, display an error to the user
                 default:
                     Toast.makeText(getApplicationContext(), "INVALID - Option not valid or completed", Toast.LENGTH_SHORT).show();
                     return false;
             }
        } catch(Exception e) {
                Toast.makeText(getApplicationContext(),"EXCEPTION " + e + " occurred!",Toast.LENGTH_SHORT).show();
                return false;
        }
    }
}
