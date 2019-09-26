package com.soteria.neurolab;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.soteria.neurolab.utilities.PasswordAuthentication;

import java.security.InvalidParameterException;

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

        // Will enable / disable the sign in button and error message.
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

    /**
     * Attempts to sign the user into the application.
     * Reads the text from the edit-text then compares it to the password hash in shared preferences.
     * If they match, will launch the menu screen.
     * Will throw an exception if there is no password stored in the preferences.
     */
    private void signIn() {
        TextInputEditText passwordEditText = getView().findViewById(R.id.login_password_edittext);
        char[] password = passwordEditText.getText().toString().toCharArray();
        passwordEditText.setText("");

        // Lookup the preferences file to access the stored password (If exists)
        SharedPreferences pref = getContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.apply();
        String storedHash = pref.getString("passwordHash", null);
        if (storedHash == null)
            throw new InvalidParameterException("No password has been set in the shared preferences");

        PasswordAuthentication authenticator = new PasswordAuthentication();
        // Androids being annoying so I'm just re-writing the DisclaimerAlertDialog function.
        if (authenticator.authenticate(password, storedHash)) {
            final AlertDialog.Builder disclaimerBuilder = new AlertDialog.Builder(getContext());

            disclaimerBuilder.setTitle(getResources().getString(R.string.disclaimer));
            disclaimerBuilder.setMessage(getResources().getString(R.string.disclaimer_body));

            //If the confirm button is pressed, close the dialog
            disclaimerBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    startActivity(new Intent(getContext(), SearchCreateDeleteActivity.class));
                }
            });
            final AlertDialog showDisclaimer = disclaimerBuilder.create();

            //Change the button colour of the alert dialog to be the primary colour
            showDisclaimer.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    showDisclaimer.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
                }
            });
            showDisclaimer.show();
            TextView bodyText = showDisclaimer.findViewById(android.R.id.message);
            bodyText.setTextSize(24);
        } else
            ((TextInputLayout) getView().findViewById(R.id.login_password_inputLayout)).setError("Incorrect Password");
    }
}
