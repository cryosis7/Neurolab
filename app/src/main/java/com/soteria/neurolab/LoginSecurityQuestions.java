package com.soteria.neurolab;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.soteria.neurolab.database.DatabaseAccess;

/**
 * This class focuses on retrieving the security questions in the Shared Preferences folder and
 * displays them to the user. If the user can answer the questions correctly the password will reset,
 * and the app will reload.
 *
 * @author Richard Dasan
 */
public class LoginSecurityQuestions extends AppCompatActivity {

    //The hardcoded int value needed to perform a reset of the app
    final int securityCode = 2941566;

    /*
        Contains the UI element declarations and button functionality.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets the top bar with the title and backwards functionality
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_recover_password);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setContentView(R.layout.activity_login_security_questions);

            //Grab Shared Preferences
            final SharedPreferences prefs = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);

            //UI element declarations
            final Button submitButton = findViewById(R.id.login_security_button_submit_questions);
            final Button forgotAnswers = findViewById(R.id.login_security_button_forgot_answers);
            final TextView questionOne = findViewById(R.id.login_security_textview_question_one);
            final TextView questionTwo = findViewById(R.id.login_security_textview_question_two);
            final EditText answerOne = findViewById(R.id.login_security_edittext_answer_one_input);
            final EditText answerTwo = findViewById(R.id.login_security_edittext_answer_two_input);

            //Checks to see whether the Shared Preferences contain questions one and two, and sets
            //them to their relevant textviews
            if(prefs.contains("QUESTION_ONE"))
                questionOne.setText(prefs.getString("QUESTION_ONE", "Question one not set"));
            if(prefs.contains("QUESTION_TWO"))
                questionTwo.setText(prefs.getString("QUESTION_TWO", "Question two not set"));

            //If the "submit" button is pressed
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Checks to see whether the questions have the correct answers submitted to them
                    if(answerOne.getText().toString().toLowerCase().equals(prefs.getString("ANSWER_ONE", null).toLowerCase()) &&
                        answerTwo.getText().toString().toLowerCase().equals(prefs.getString("ANSWER_TWO", null).toLowerCase())) {
                        //If they do, access the shared preferences and remove the password value
                        SharedPreferences.Editor prefEdit = prefs.edit();
                        prefEdit.remove("passwordHash");
                        prefEdit.apply();
                        // Send the user back to the login page to reset their password
                        startActivity(new Intent(LoginSecurityQuestions.this, LoginCreatePasswordActivity.class));
                        finish();
                    } else {
                        //Notify the user that their answers were incorrect
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.security_question_answers_incorrect), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //If the "I forgot my answers" button is pressed
            forgotAnswers.setOnClickListener(new View.OnClickListener() {
                //Set up an alert dialog box asking
                @Override
                public void onClick(View view) {
                    //Build an erase alertDialog that warns the user that if they proceed, they will erase
                    //all information in the app as well as their password and security questions
                    final AlertDialog.Builder eraseBuilder = new AlertDialog.Builder(LoginSecurityQuestions.this);
                    eraseBuilder.setTitle("Warning");
                    eraseBuilder.setMessage(getString(R.string.security_erase_database_warning));
                    //If the user accepts
                    eraseBuilder.setPositiveButton("Erase", new DialogInterface.OnClickListener()  {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Builds the confirm alertDialog that asks the user for a security code
                            //found inside the user manual.
                            final AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(LoginSecurityQuestions.this);

                            //Creates an editText for use inside the new alertDialog for entering
                            //in the security code and customizes it appropriately
                            final EditText codeInput = new EditText(LoginSecurityQuestions.this);
                            codeInput.setHint("Security Code");
                            codeInput.setHintTextColor(getResources().getColor(R.color.colorDarkGrey));
                            codeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                            codeInput.setTag(R.string.security_tag_security_code,"security_code_edittext");

                            //Sets the message and buttons appropriately.
                            confirmBuilder.setTitle("Confirm Code");
                            confirmBuilder.setMessage(getString(R.string.security_reset_information));
                            confirmBuilder.setView( codeInput );
                            confirmBuilder.setPositiveButton("Confirm", null );

                            //Closes the confirm alertDialog if back is pressed
                            confirmBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            //Closes the erase alertDialog
                            dialogInterface.cancel();

                            //Creates the confirmDialog and shows it to the user
                            final AlertDialog showConfirm = confirmBuilder.create();
                            showConfirm.show();

                            //Sets the positive button to check the security code entered
                            Button showConfirmPositiveButton = showConfirm.getButton(AlertDialog.BUTTON_POSITIVE);
                            showConfirmPositiveButton.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //If the code is blank, mention that error to the user
                                    if( codeInput.getText().toString().equals(""))
                                        Toast.makeText(getApplicationContext(), getResources()
                                                .getString(R.string.security_code_blank), Toast.LENGTH_SHORT).show();
                                    //Otherwise, erase the database and remove all values of the
                                    //Shared Preferences
                                    else if( Integer.parseInt(codeInput.getText().toString() ) == securityCode) {
                                        //Sets the variable for the database
                                        DatabaseAccess db = new DatabaseAccess(LoginSecurityQuestions.this);

                                        //Purges the database of all values
                                        db.purgeDatabase();

                                        //Sets the variable for the shared preferences
                                        SharedPreferences pref = LoginSecurityQuestions.this.getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();

                                        //Remove all values from the shared preferences
                                        editor.remove("passwordHash");
                                        editor.remove("QUESTION_ONE");
                                        editor.remove("QUESTION_TWO");
                                        editor.remove("ANSWER_ONE");
                                        editor.remove("ANSWER_TWO");
                                        editor.apply();

                                        //Mention the change to the user, and sends them to the login screeen
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.security_correct_security_code), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginSecurityQuestions.this, LoginCreatePasswordActivity.class));
                                        finish();
                                    //Else if the code is incorrect, set an error message stating such.
                                    } else {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.security_incorrect_security_code), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            TextView bodyText = showConfirm.findViewById(android.R.id.message);
                            bodyText.setTextSize(24);
                        }
                    });
                    //If the user declines to erase the database, close the alertDialog
                    eraseBuilder.setNegativeButton("Decline", new DialogInterface.OnClickListener()  {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    //Creates the alertDialog
                    final AlertDialog showErase = eraseBuilder.create();
                    //Sets the aesthetics of the alertDialog
                    showErase.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            showErase.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
                            showErase.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorWarning));
                            showErase.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
                        }
                    });
                    //Show the alertDialog to the user
                    showErase.show();
                    TextView bodyText = showErase.findViewById(android.R.id.message);
                    bodyText.setTextSize(24);
                }
            });
        }
    }

    /**
     *  Enables the page to go back to the previous page by pressing the back button on the
     *  top bar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        } else {
            finish();
            return false;
        }
    }
}
