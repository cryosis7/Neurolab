package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameAssignment;
import com.soteria.neurolab.models.Patient;
import com.soteria.neurolab.utilities.DisclaimerAlertDialog;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Needs class description
 */
public class EditPatientDetails extends AppCompatActivity {
    private static final String TAG = "EditPatientDetails";
    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient_details);

        //Setup action bar - title and back button
        if(getSupportActionBar() != null ) {
            getSupportActionBar().setTitle(getString(R.string.edit_patient_details_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String patientReferenceOld = "";
        final DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());

        try {
            Bundle viewBundle = getIntent().getExtras();
            patientReferenceOld = viewBundle.getString("PATIENT_REFERENCE");
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(),"ERROR - Patient does not exist or is corrupted",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SearchPatientFragment.class));
            finish();
        }

        patient = db.getPatient(patientReferenceOld);

        //Initializing UI Elements
        final TextInputEditText editPatientID = findViewById(R.id.editPatientDetails_inputPatientID);
        final TextInputLayout editPatientIDLayout = findViewById(R.id.editPatientDetails_inputPatientIDLayout);
        final CheckBox checkBoxReaction = findViewById(R.id.editPatientDetails_checkReactionTime);
        final CheckBox checkBoxMotor = findViewById(R.id.editPatientDetails_checkMotorSkills);
        final CheckBox checkBoxAttention = findViewById(R.id.editPatientDetails_checkSelectiveAttention);
        final CheckBox checkBoxMemory = findViewById(R.id.editPatientDetails_checkVisualShortTermMemory);
        final Button confirmButton = findViewById(R.id.editPatientDetails_confirmButton);
        final Button exitButton = findViewById(R.id.editPatientDetails_exitButton);
        final TextView seekAttemptsCount = findViewById(R.id.editPatientDetails_textAttemptsNumber);
        final SeekBar seekAttemptsBar = findViewById(R.id.editPatientDetails_seekAttempts);

        //So that the keyboard doesn't pop up when opening the page
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //set the patient text to the current patient reference
        editPatientID.setText(patient.getPatientReference());

        //check the checkboxes of the currently assigned games
        final List<GameAssignment> gameAssignments = db.getAssignments(patient.getPatientID());
            for (GameAssignment ga : gameAssignments) {
                switch (ga.getGameID()) {
                    case 1:
                        checkBoxReaction.setChecked(true);
                        break;
                    case 2:
                        checkBoxMemory.setChecked(true);
                        break;
                    case 3:
                        checkBoxMotor.setChecked(true);
                        break;
                    case 4:
                        checkBoxAttention.setChecked(true);
                        break;
                    default:
                        break;
                }
            }

        //Functionality for the seek bar, checks first if patient has any game assignments
        if(gameAssignments.size() != 0){
            seekAttemptsBar.setProgress(db.getAssignments(patient.getPatientID()).get(0).getGameAttempts() - 1);
            seekAttemptsCount.setText(Integer.toString(seekAttemptsBar.getProgress() + 1));
        }
        seekAttemptsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            /**
             * Changes the number displaying the attempts next to the seekbar
             * @param seekBar - The seekbar object
             * @param progress - The progress of the seekbar
             * @param fromUser - Changed by user (true) or system (false)
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekAttemptsCount.setText(Integer.toString(progress + 1));
            }
        });

        //Confirm button functionality
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking that patientID is alphanumerical
                String patientIDRegex = getString(R.string.alpha_numeric_regex);
                Pattern pattern = Pattern.compile(patientIDRegex);
                Matcher matcher = pattern.matcher(editPatientID.getText().toString());

                if(TextUtils.isEmpty(editPatientID.getText().toString())) {
                    editPatientIDLayout.setErrorEnabled(true);
                    editPatientIDLayout.setError(getString(R.string.error_patient_id_blank));
                } else if(!matcher.matches()) {
                    editPatientIDLayout.setErrorEnabled(true);
                    editPatientIDLayout.setError(getString(R.string.error_patient_id_special_characters));
                } else if( editPatientID.getText().toString().length() > 10 ) {
                    editPatientIDLayout.setErrorEnabled(true);
                    editPatientIDLayout.setError(getString(R.string.error_patient_id_length));
                } else {
                    final String patientReference = editPatientID.getText().toString().toUpperCase();

                    //Check that the patient reference isn't already being used
                    if( patientNameCheck(db.getAllPatients(), patientReference, patient.getPatientReference()) ) {
                        patient.setPatientReference(patientReference);
                        db.updatePatient(patient);
                        int attempts = seekAttemptsBar.getProgress() + 1;

                        //Delete all game assignments for the patient
                        for (GameAssignment ga : gameAssignments){
                            db.deleteAssignment(ga);
                        }

                        //Add new game assignments
                        if (checkBoxReaction.isChecked())
                            db.createAssignment(new GameAssignment(1, patient.getPatientID(), attempts));
                        if (checkBoxMemory.isChecked())
                            db.createAssignment(new GameAssignment(2, patient.getPatientID(), attempts));
                        if (checkBoxMotor.isChecked())
                            db.createAssignment(new GameAssignment(3, patient.getPatientID(), attempts));
                        if (checkBoxAttention.isChecked())
                            db.createAssignment(new GameAssignment(4, patient.getPatientID(), attempts));

                        //Produce toast message saying patient updated and go to view patient details page
                        Toast.makeText(EditPatientDetails.this, getResources().getString(R.string.edit_patient_details_patient_updated),Toast.LENGTH_LONG).show();

                        Intent viewIntent = new Intent(EditPatientDetails.this, ViewPatientDetails.class);
                        viewIntent.putExtra("PATIENT_REFERENCE", patientReference);
                        setResult(RESULT_OK, viewIntent);
                        finish();
                    } else {
                        editPatientIDLayout.setErrorEnabled(true);
                        editPatientIDLayout.setError(getString(R.string.error_patient_id_in_use));
                    }
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*
         * Listener on the edit text field to clear the error upon changing the patientID
         */
        editPatientID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editPatientIDLayout.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private boolean patientNameCheck( List<Patient> databasePatients, String newID , String oldID)
    {
        for( Patient temp : databasePatients )
        {
            if( temp.getPatientReference().equals(newID)) {
                if (newID.equals(oldID)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Press the home button to go back home
     * @param view the home image button
     */
    public void launchMainScreen(View view) {
        startActivity(new Intent(view.getContext(), SearchCreateDeleteActivity.class));
        finish();
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
                //If the settings button is pressed, direct the user to the settings page
                case R.id.action_settings:
                    startActivity(new Intent(this, SettingsActivity.class));
                //If the disclaimer button is pressed, display the disclaimer in an alert dialog
                case R.id.action_disclaimer:
                    DisclaimerAlertDialog dad = new DisclaimerAlertDialog();
                    dad.showDisclaimer(this, getResources());
                    return true;
                //If the log out button is pressed, send the user to the log in screen
                case R.id.action_logout:
                    startActivity(new Intent(this, LoginCreatePasswordActivity.class));
                    finish();
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

    /**
     * Overrides the on back pressed to indicate the state (Does not need refresh) that it is
     * returning to the ViewPatientDetails screen
     */
    @Override
    public void onBackPressed() {
        Intent viewIntent = new Intent(this, ViewPatientDetails.class);
        setResult(RESULT_CANCELED, viewIntent);
        finish();
    }
}
