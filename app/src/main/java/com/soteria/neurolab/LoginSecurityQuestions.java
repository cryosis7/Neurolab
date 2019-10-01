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

    final int securityCode = 3579753;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_recover_password);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setContentView(R.layout.activity_login_security_questions);

            //Grab Shared Preferences
            final SharedPreferences prefs = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);

            //UI element declarations
            final Button submitButton = findViewById(R.id.login_submit_questions);
            final Button forgotAnswers = findViewById(R.id.login_forgot_answers);
            final EditText answerOne = findViewById(R.id.login_security_answer_one_input);
            final EditText answerTwo = findViewById(R.id.login_security_answer_two_input);

            //If the "submit" button is pressed
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(answerOne.getText().toString().equals(prefs.getString("ANSWER_ONE", null)) ||
                       answerTwo.getText().toString().equals(prefs.getString("ANSWER_TWO", null))) {
                        //Commence Reset here
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.security_question_answers_incorrect), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //If the "I forgot my answers" button is pressed
            forgotAnswers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder eraseBuilder = new AlertDialog.Builder(LoginSecurityQuestions.this);
                    eraseBuilder.setTitle("Warning");
                    eraseBuilder.setMessage(getString(R.string.security_erase_database_warning));
                    eraseBuilder.setPositiveButton("Erase", new DialogInterface.OnClickListener()  {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(LoginSecurityQuestions.this);
                            final EditText codeInput = new EditText(LoginSecurityQuestions.this);
                            codeInput.setHint("Security Code");
                            codeInput.setHintTextColor(getResources().getColor(R.color.colorDarkGrey));
                            codeInput.setInputType(InputType.TYPE_CLASS_NUMBER);

                            confirmBuilder.setTitle("Confirm Code");
                            confirmBuilder.setMessage(getString(R.string.security_reset_information));
                            confirmBuilder.setView( codeInput );
                            confirmBuilder.setPositiveButton("Confirm", null );
                            confirmBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            dialogInterface.cancel();
                            final AlertDialog showConfirm = confirmBuilder.create();
                            showConfirm.show();
                            Button showConfirmPositiveButton = showConfirm.getButton(AlertDialog.BUTTON_POSITIVE);
                            showConfirmPositiveButton.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if( codeInput.getText().toString().equals(""))
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.security_code_blank), Toast.LENGTH_SHORT).show();
                                    else if( Integer.parseInt(codeInput.getText().toString() ) == securityCode) {
                                        DatabaseAccess db = new DatabaseAccess(LoginSecurityQuestions.this);

                                        db.purgeDatabase();
                                        SharedPreferences pref = LoginSecurityQuestions.this.getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.remove("passwordHash");
                                        editor.remove("QUESTION_ONE");
                                        editor.remove("QUESTION_TWO");
                                        editor.remove("ANSWER_ONE");
                                        editor.remove("ANSWER_TWO");
                                        editor.apply();

                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.security_correct_security_code), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginSecurityQuestions.this, LoginCreatePasswordActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.security_incorrect_security_code), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            TextView bodyText = showConfirm.findViewById(android.R.id.message);
                            bodyText.setTextSize(24);
                        }
                    });

                    eraseBuilder.setNegativeButton("Decline", new DialogInterface.OnClickListener()  {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    final AlertDialog showErase = eraseBuilder.create();
                    showErase.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            showErase.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
                            showErase.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorWarning));
                            showErase.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
                        }
                    });
                    showErase.show();
                    TextView bodyText = showErase.findViewById(android.R.id.message);
                    bodyText.setTextSize(24);
                }
            });
        }
    }

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
