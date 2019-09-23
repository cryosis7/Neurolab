package com.soteria.neurolab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.soteria.neurolab.utilities.DisclaimerAlertDialog;
import com.soteria.neurolab.utilities.PasswordAuthentication;

import static android.content.Context.MODE_PRIVATE;

/**
 * Manages the create password screen that is run when no password is found in the shared preferences file.
 * Hashes a password supplied by the user and stores it in the shared preferences file, then redirects to the login screen
 *
 * @author Scott Curtis
 */
public class CreatePasswordFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_password, container, false);

        view.findViewById(R.id.create_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextInputEditText passwordEditText1 = getView().findViewById(R.id.create_password_edit_text_1);
                TextInputEditText passwordEditText2 = getView().findViewById(R.id.create_password_edit_text_2);
                String password1 = passwordEditText1.getText().toString();
                String password2 = passwordEditText2.getText().toString();
                passwordEditText1.setText("");
                passwordEditText2.setText("");

                CheckBox disclaimerBox = getView().findViewById(R.id.create_password_disclaimer_checkbox);
                boolean validPassword = validPassword(password1, password2);

                if (validPassword && disclaimerBox.isChecked()) {
                    createPassword(password1);
                    ((LoginCreatePasswordActivity) getActivity()).switchFragment();
                } else if (validPassword && !disclaimerBox.isChecked())
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirmation Required")
                            .setMessage("You must agree to the disclaimer to use Neurolab")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();

                disclaimerBox.setChecked(false);
            }
        });

        view.findViewById(R.id.create_password_disclaimer_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked())
                    new DisclaimerAlertDialog().showDisclaimerWithCancel(getContext(), getResources());
            }
        });

        view.findViewById(R.id.create_password_disclaimer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DisclaimerAlertDialog().showDisclaimer(getContext(), getResources());
            }
        });

        ((TextInputEditText) view.findViewById(R.id.create_password_edit_text_1)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (((TextInputLayout) view.findViewById(R.id.create_password_input_layout_1)).isErrorEnabled())
                    ((TextInputLayout) view.findViewById(R.id.create_password_input_layout_1)).setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    /**
     * Validates whether the two passwords are equal and meet the following criteria:
     * At least 6 characters
     * Must contain at least one letter, number and symbol
     *
     * @return true if password meet all the criteria
     */
    private boolean validPassword(String password1, String password2) {
        TextInputLayout passwordLayout = getView().findViewById(R.id.create_password_input_layout_1);

        if (!password1.equals(password2)) {
            passwordLayout.setError(getResources().getString(R.string.create_password_not_equal));
            return false;
        } else if (!password1.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-=_+`~<>,./?;':\"{}\\\\|])[A-Za-z\\d!@#$%^&*()\\-=_+`~<>,./?;':\"{}\\\\|]{6,}$")) {
            Log.i("CreatePasswordFragment", "Failed to match");
            passwordLayout.setError(getResources().getString(R.string.create_password_criteria));
            return false;
        }

        return true;
    }

    /**
     * Hashes a password and stores it into the applications stored preferences.
     * Assumes all validations and criteria are already met.
     *
     * @param password A password to store into the device
     */
    private void createPassword(String password) {
        PasswordAuthentication authenticator = new PasswordAuthentication();
        String hashedPassword = authenticator.hash(password.toCharArray());

        SharedPreferences pref = getContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("passwordHash", hashedPassword);
        editor.apply();
    }
}
