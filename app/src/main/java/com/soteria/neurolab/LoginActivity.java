package com.soteria.neurolab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.soteria.neurolab.utilities.PasswordAuthentication;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int PASSWORD_LENGTH_MINIMUM = 6; // TODO: discuss length and move to better location.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Will enable / disable the sign in button when enough characters are entered.
        ((EditText) findViewById(R.id.login_password_edittext)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                findViewById(R.id.login_sign_in_button).setEnabled(charSequence.length() >= PASSWORD_LENGTH_MINIMUM);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void signInButtonHandler(View view) {
        EditText passwordEditText = findViewById(R.id.login_password_edittext);
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
                passwordEditText.setError("Incorrect Password");
            }
        } else {
            String hashedPassword = authenticator.hash(password);
            editor.putString("passwordHash", hashedPassword);
            editor.commit();
            Toast.makeText(this, "Password Set", Toast.LENGTH_SHORT).show(); //TODO: Remove with 016-create-password
        }
    }
}
