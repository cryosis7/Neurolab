package com.soteria.neurolab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.soteria.neurolab.utilities.DisclaimerAlertDialog;
import com.soteria.neurolab.utilities.PasswordAuthentication;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {

    private static final int PASSWORD_LENGTH_MINIMUM = 6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        final View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Will enable / disable the sign in button when enough characters are entered.
        ((TextInputEditText) view.findViewById(R.id.login_password_edittext)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getView().findViewById(R.id.login_sign_in_button).setEnabled(charSequence.length() >= PASSWORD_LENGTH_MINIMUM);
                if (((TextInputLayout) view.findViewById(R.id.login_password_inputLayout)).isErrorEnabled())
                    ((TextInputLayout) view.findViewById(R.id.login_password_inputLayout)).setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Set on-click listener for the sign-in button.
        view.findViewById(R.id.login_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        return view;
    }

    private void signIn() {
        TextInputEditText passwordEditText = getView().findViewById(R.id.login_password_edittext);
        char[] password = passwordEditText.getText().toString().toCharArray();
        passwordEditText.setText("");

        // Lookup the preferences file to access the stored password (If exists)
        SharedPreferences pref = getContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.apply();
        String storedHash = pref.getString("passwordHash", null);

        PasswordAuthentication authenticator = new PasswordAuthentication();
        if (storedHash != null) {
            if (authenticator.authenticate(password, storedHash)) {
                new DisclaimerAlertDialog().showDisclaimerWithCancel(getContext(), getResources());
                startActivity(new Intent(getContext(), SearchCreateDeleteActivity.class));
            } else {
                ((TextInputLayout) getView().findViewById(R.id.login_password_inputLayout)).setError("Incorrect Password");
            }
        } else {
            String hashedPassword = authenticator.hash(password);
            editor.putString("passwordHash", hashedPassword);
            editor.apply();
            Toast.makeText(getContext(), "Password Set", Toast.LENGTH_SHORT).show(); //TODO: Remove with 016-create-password
        }
    }
}
