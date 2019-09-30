package com.soteria.neurolab;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.soteria.neurolab.utilities.PasswordAuthentication;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int PASSWORD_LENGTH_MINIMUM = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Will enable / disable the sign in button when enough characters are entered.
        ((TextInputEditText) findViewById(R.id.login_password_edittext)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                findViewById(R.id.login_sign_in_button).setEnabled(charSequence.length() >= PASSWORD_LENGTH_MINIMUM);
                if (((TextInputLayout) findViewById(R.id.login_password_inputLayout)).isErrorEnabled())
                    ((TextInputLayout) findViewById(R.id.login_password_inputLayout)).setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    public void signInButtonHandler(View view) {
        signIn();
    }

    /**
     * This function is called whenever the "Reset Password" button is pressed
     * @param view the Reset Password button
     */
    public void forgotPasswordButtonHandler(View view) { forgotPassword(); }

    private void signIn() {
        TextInputEditText passwordEditText = findViewById(R.id.login_password_edittext);
        char[] password = passwordEditText.getText().toString().toCharArray();
        passwordEditText.setText("");

        // Lookup the preferences file to access the stored password (If exists)
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.apply();
        String storedHash = pref.getString("passwordHash", null);

        PasswordAuthentication authenticator = new PasswordAuthentication();
        if (storedHash != null) {
            if (authenticator.authenticate(password, storedHash)) {
                startActivity(new Intent(this, SearchCreateDeleteActivity.class));
            }
            else {
                ((TextInputLayout) findViewById(R.id.login_password_inputLayout)).setError("Incorrect Password");
            }
        } else {
            String hashedPassword = authenticator.hash(password);
            editor.putString("passwordHash", hashedPassword);
            editor.commit();
            Toast.makeText(this, "Password Set", Toast.LENGTH_SHORT).show(); //TODO: Remove with 016-create-password
        }
    }

    /**
     * This function is called whenever the "I forgot my Password" button is pressed. It will
     * check to see whether they have any security questions set and if they do, will redirect
     * them to the LoginSecurityQuestions page. If they do not, they will be prompted to do a database
     * purge and a password reset at the same time. Otherwise they can keep attempting to log in.
     */
    private void forgotPassword() {
        //Grabs the SharedPreferences file
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);

        //Checks to see whether the SharedPreferences file has the security questions inside of them
        //TODO FIND ERROR
        if(!pref.contains("QUESTION_ONE") && pref.contains("QUESTION_TWO")) {
            //If the security questions do not exist, call an alertDialog to ask if user wants to
            //wipe their password and the database in order to get back into the app
            final AlertDialog.Builder eraseBuilder = new AlertDialog.Builder(this);
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
        } else {
            //If the security questions do exist, direct the user to the LoginSecurityQuestions
            //page
            startActivity(new Intent(this, LoginSecurityQuestions.class));
        }
    }
}
