package com.soteria.neurolab;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This class focuses on retrieving the security questions in the Shared Preferences folder and
 * displays them to the user. If the user can answer the questions correctly the password will reset,
 * and the app will reload.
 *
 * @author Richard Dasan
 */
public class LoginSecurityQuestions extends AppCompatActivity {

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
                       answerOne.getText().toString().equals(prefs.getString("ANSWER_TWO", null))) {
                        //Commence Reset here
                    } else {

                    }
                }
            });

            //If the "I forgot my answers" button is pressed
            forgotAnswers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder eraseBuilder = new AlertDialog.Builder(LoginSecurityQuestions.this);
                    eraseBuilder.setTitle("Warning");
                    eraseBuilder.setMessage(getString(R.string.login_erase_database_warning));
                    eraseBuilder.setPositiveButton("Erase", new DialogInterface.OnClickListener()  {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Call database purge here
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
