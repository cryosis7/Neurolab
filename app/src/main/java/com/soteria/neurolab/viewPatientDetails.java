package com.soteria.neurolab;

import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class viewPatientDetails extends AppCompatActivity {

    @Override
    //TODO Remove comment in the function parameters once information can be passed through
    protected void onCreate(Bundle savedInstanceState) {

        //TODO Hardcoded values, remove after parameters can be passed through
        String patientIdentifier = "SC17";
        Date lastDayPlayed = new Date();

        /*
        Values that are passed through from the previous screen.

        Bundle viewBundle = getIntent.getExtras();
        String patientIdentifier = bundle.getString("pID");
        Date lastDayPlayed = bundle.getDate("pDate");
        */

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Patient Details");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_view_patient_details);


        //Information for changing the text information on load
        //TODO pass through patient identifier and last game run information when completed

        final TextView patientID = findViewById(R.id.patientIDTitleTextView);
        final TextView lastPlayed = findViewById(R.id.patientGamesLastRunTextView);

        patientID.setText("Patient: " + patientIdentifier );

        DateFormat convertDate = new SimpleDateFormat("dd-mm-yyyy hh:mm a");
        String inputDate = convertDate.format(lastDayPlayed);
        lastPlayed.setText("Games last run: " + inputDate );


        //Button instructions for run games button
        //TODO add link to send users to patient home once created, remove toast once done.

        final Button runButton = findViewById(R.id.runGamesButton);
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


        //Button instructions for manage patient button
        //TODO add link to send users to the manage patient screen once created, remove toast once done.

        final Button manageButton = findViewById(R.id.managePatientButton);
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


        //Button instructions for view report button
        //TODO add link to send users to the view report screen once created, remove toast once done.

        final Button reportButton = findViewById(R.id.viewReportButton);
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


        //Button instructions for delete patient
        //TODO add link to search patient and remove patient from database once created, remove toast once done.

        final Button deleteButton = findViewById(R.id.deletePatientButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(viewPatientDetails.this);
                deleteBuilder.setTitle("Deleting Patient");
                deleteBuilder.setMessage("You are about to delete this patient and all data associated with them.\n\n" +
                        "This action cannot be undone.\n\nDo you wish to continue?");
                deleteBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO enter code to remove patient from database here
                        Toast.makeText(getApplicationContext(),"DELETE PRESSED: Removed patient, send to search",Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(this, //TODO add link to search patient here));
                        dialogInterface.cancel();
                    }
                });
                deleteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog deleteConfirm = deleteBuilder.create();
                deleteConfirm.show();
            }
        });


        //Button instructions for the home image button
        //TODO add link to search patient once created, remove toast once done.

        final ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"HOME BUTTON PRESSED: Sending to search patients screen",Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(this, //TODO add link to search patient here));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try {
            getMenuInflater().inflate(R.menu.overflow_menu, menu);
            return true;
        } catch ( Exception e ) {
            Toast.makeText(getApplicationContext(),"EXCEPTION " + e + " occurred!",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"BACK BUTTON PRESSED: Sending to search patients screen",Toast.LENGTH_SHORT).show();
    }

    public boolean onOptionsItemSelected(MenuItem menu)
    {
        try {
             switch(menu.getItemId()) {
                 case android.R.id.home:
                     super.onBackPressed();
                     return true;
                 case R.id.action_disclaimer:
                     final AlertDialog.Builder disclaimerBuilder = new AlertDialog.Builder(this );
                     disclaimerBuilder.setTitle("Disclaimer");
                     disclaimerBuilder.setMessage("Neurolab by Soteria is designed as an assistant tool only.\n" +
                             "It should not be relied upon for treatment, diagnosis or professional medical advice.\n" +
                             "If you have any queries about its use, please consult your local physician or health care provider.\n\n" +
                             "By accessing and using this app, you have confirmed that you have read, understood and accepted the above statement.");
                     disclaimerBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()  {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             dialogInterface.cancel();
                         }
                     });
                     final AlertDialog showDisclaimer = disclaimerBuilder.create();
                     showDisclaimer.setOnShowListener( new DialogInterface.OnShowListener() {
                         @Override
                         public void onShow(DialogInterface arg0 ) {
                             showDisclaimer.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                         }
                     });
                     showDisclaimer.show();
                     return true;
                 case R.id.action_logout:
                     //TODO link to logout, remove toast afterwards
                     Toast.makeText(getApplicationContext(), "LOGOUT PRESSED - Going to log in screen", Toast.LENGTH_SHORT).show();
                     //startActivity(new Intent(this, //TODO add link to log in here));
                     return true;
                 default:
                     return false;
             }
        } catch(Exception e) {
                Toast.makeText(getApplicationContext(),"EXCEPTION " + e + " occurred!",Toast.LENGTH_SHORT).show();
                return false;
        }
    }
}
