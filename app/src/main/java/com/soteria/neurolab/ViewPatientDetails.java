package com.soteria.neurolab;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.Patient;
import com.soteria.neurolab.utilities.DisclaimerAlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewPatientDetails extends AppCompatActivity {

    /**
     *  Handles the creation of UI elements that change on load and button press logic.
     *
     * @param savedInstanceState - I don't know why does life do this to me I don't know I don't know
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* Variable declarations
         */
        String patientIdentifier = "";
        final DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
        String lastTimePlayed;

        /* Attempts to retrieve data from an intent. If intent retrieval fails, sends the user back
           to the search patient screen.
         */
        try {
            Bundle viewBundle = getIntent().getExtras();
            patientIdentifier = viewBundle.getString("PATIENT_REFERENCE");
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(),"ERROR - Patient does not exist or is corrupted",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SearchPatientFragment.class));
            finish();
        }

        /* Attempts to grab the most recent date the patient has played any game. It will attempt
           to display the date in an appropriate format but if unable to due to a syntax error will
           display the date as is.
         */
        final String patientReference = patientIdentifier;
        final Patient patient = db.getPatient(patientReference);
        try {
            lastTimePlayed = (new SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH).parse(db.getLatestDate(patient.getPatientID()))).toString();
        } catch ( ParseException e ) { lastTimePlayed = db.getLatestDate(patient.getPatientID());
        } catch ( NullPointerException e ) { lastTimePlayed = "No games recorded on current patient"; }

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

        /*Code for changing the patientID and lastGamesRun text views to display the information
          passed through to it from another page. */
        patientID.setText(getResources().getString(R.string.view_patient_details_patient_identifier, patientReference));
        lastPlayed.setText(getString(R.string.view_patient_details_last_date_played, lastTimePlayed));


        /*When run games is pressed, sends user to the patient profile
        TODO add link to send users to patient home once created, remove toast and comment markers  once done. */
        runButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if( db.checkAssignments(patient) ) {
                    Intent gameIntent = new Intent(ViewPatientDetails.this, ReactionGameActivity.class);
                    gameIntent.putExtra("PATIENT_ID", patient.getPatientID());
                    startActivity(gameIntent);
                } else {
                    Toast.makeText(getApplicationContext(),"ERROR - No games have been assigned to this patient",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*When manage patient button is pressed, send the user to the manage patient screen for the
          current patient.
          TODO add link to send users to the manage patient screen once created, remove toast and comment markers  once done. */
        manageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

               Intent manageIntent = new Intent(ViewPatientDetails.this, EditPatientDetails.class);
               manageIntent.putExtra("PATIENT_REFERENCE", patientReference);
               startActivity(manageIntent);

            }
        });

        /*When the view report button is pressed, send the user to the report screen for the current
          patient.
        */
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               Intent reportIntent = new Intent(ViewPatientDetails.this, ViewReportActivity.class);
               reportIntent.putExtra("PATIENT_REFERENCE", patientReference);
               startActivity(reportIntent);
            }
        });

        /*When the delete patient button is pressed, ask the user for confirmation. If they agree,
          remove the patient from the database and send the user to the create patient page.
         */
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Build the alert dialog warning the user of their action.
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(ViewPatientDetails.this);
                deleteBuilder.setTitle(getResources().getString(R.string.title_delete_patient));
                deleteBuilder.setMessage(getString(R.string.view_patient_details_delete_patient_dialog));
                //If delete is pressed, delete the patient and send the user to the search patients screen
                deleteBuilder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseAccess dbDelete = DatabaseAccess.getInstance(getApplicationContext());
                        com.soteria.neurolab.models.Patient patientToDelete = dbDelete.getPatient(patientReference);
                        dbDelete.deletePatient(patientToDelete);
                        dialogInterface.cancel();
                        startActivity(new Intent(ViewPatientDetails.this, SearchCreateDeleteActivity.class));
                        finish();
                    }
                });
                //If cancel is pressed, close the alert dialog.
                deleteBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                final AlertDialog deleteConfirm = deleteBuilder.create();
                deleteConfirm.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0 ) {
                        deleteConfirm.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
                        deleteConfirm.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorWarning));
                        deleteConfirm.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
                        deleteConfirm.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                });
                deleteConfirm.show();
                TextView bodyText = deleteConfirm.findViewById(android.R.id.message);
                bodyText.setTextSize(24);
            }
        });
    }

    /** Sets the actions bar to include an overflow menu with the disclaimer and log out options.
     *
     * @param menu - Instance of the top bar
     * @return true if the top bar was able to be updated and false if an error occurred.
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

    /** Determines the behaviour of options selected from the action bar. Options include displaying
     *  the disclaimer and sending the user back to the main menu.
     *  TODO insert link for logout
     *
     *  @param menuItem - The identifier of the item selected from the top bar.
     *  @return true if an option was able to be selected and false if an exception occurred.
     */
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        try {
             switch(menuItem.getItemId()) {
                 //If the back button is pressed, return the user to the last visited page
                 case android.R.id.home:
                     super.onBackPressed();
                     return true;
                 //If the disclaimer button is pressed, display the disclaimer in an alert dialog
                 case R.id.action_disclaimer:
                     DisclaimerAlertDialog dad = new DisclaimerAlertDialog();
                     dad.showDisclaimer(this, getResources());
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
