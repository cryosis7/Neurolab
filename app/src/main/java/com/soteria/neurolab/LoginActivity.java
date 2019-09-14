package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
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
        String password = ((EditText) findViewById(R.id.login_password_edittext)).getText().toString();
    }
}
