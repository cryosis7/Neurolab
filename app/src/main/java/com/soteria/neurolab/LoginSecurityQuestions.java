package com.soteria.neurolab;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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

            //UI element declarations
            final Button submitButton = findViewById(R.id.login_submit_questions);
            final Button forgotAnswers = findViewById(R.id.login_forgot_answers);

            //If the "submit" button is pressed
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            //If the "I forgot my answers" button is pressed
            forgotAnswers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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
