package com.soteria.neurolab;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.soteria.neurolab.utilities.PasswordAuthentication;

import static android.content.Context.MODE_PRIVATE;

/**
 * This fragment focuses on allowing the user to reset their password. This can be done by
 * inputting their current password and entering in a new password that meets the requirements of
 * 6 characters with one symbol and one number. If the current password is incorrect or the new
 * passwords do not meet the requirements, informs the user of their error. The new password is
 * stored in the Shared Preferences, overwriting the old password if saved.
 *
 * @author Richard Dasan
 */
public class SettingsResetPasswordFragment extends Fragment {

    //Fragment listener used to attach and detach the fragment from the current page.
    //Despite saying it is not in use, it is used in the onAttach and onDetach classes.
    private OnFragmentInteractionListener mListener;

    //Default class constructor
    public SettingsResetPasswordFragment() {
    }

    public static SettingsResetPasswordFragment newInstance() {
        return new SettingsResetPasswordFragment();
    }

    /**
     * Basic onCreate Function. The fragment set up is set in the onCreateView class
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This class sets up the view to add functionality to the UI.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the code in the view to allow interaction
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View passwordView =  inflater.inflate(R.layout.fragment_settings_reset, container, false);

        //UI element declarations
        final Button submitButton = passwordView.findViewById(R.id.settings_submit_reset);
        final EditText passwordCurrent = passwordView.findViewById(R.id.settings_current_password);
        final EditText passwordNew = passwordView.findViewById(R.id.settings_password_new);
        final EditText passwordConfirm = passwordView.findViewById(R.id.settings_password_confirm);
        final TextInputLayout passwordCurrentLayout = passwordView.findViewById(R.id.settings_password_current_inputLayout);
        final TextInputLayout passwordNewLayout = passwordView.findViewById(R.id.settings_password_new_inputLayout);
        final TextInputLayout passwordConfirmLayout = passwordView.findViewById(R.id.settings_password_confirm_inputLayout);

        //Code to run when the submit button is pressed
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the text values from the relevant edittexts
                String getPasswordCurrent = passwordCurrent.getText().toString();
                String getPasswordNew = passwordNew.getText().toString();
                String getPasswordConfirm = passwordConfirm.getText().toString();

                //The following if statements check for any error messages that might appear if
                //a user has entered in their information incorrectly.
                //If the current password is blank
                if (getPasswordCurrent.equals("")) {
                    passwordCurrentLayout.setErrorEnabled(true);
                    passwordCurrentLayout.setError("Current password field is blank");
                //If the new password does not match the confirm password
                } else if (!getPasswordNew.equals(getPasswordConfirm)) {
                    passwordNewLayout.setErrorEnabled(true);
                    passwordNewLayout.setError("New passwords do not match");
                //If the new password does not meet the conditions for new passwords
                } else if (!getPasswordNew.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-=_+`~<>,./?;':\"{}\\\\|])[A-Za-z\\d!@#$%^&*()\\-=_+`~<>,./?;':\"{}\\\\|]{6,}$")){
                    passwordNewLayout.setErrorEnabled(true);
                    passwordNewLayout.setError("Password must be at least 6 characters and have a special character and a number");
                //If there are no errors, run the passwordChange function
                } else {
                    passwordChange(passwordCurrent.getText().toString() ,passwordNew.getText().toString(), passwordCurrent, passwordNew, passwordConfirm);
                }
            }
        });

        //Set the TextChangeListener to clear the errors once text has been put in them
        passwordCurrent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordCurrentLayout.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        passwordNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordNewLayout.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        passwordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordConfirmLayout.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        passwordView.findViewById(R.id.fragment_reset_root).requestFocus();
        return passwordView;
    }

    //Attaches this fragment to allow users to view it.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingsResetPasswordFragment.OnFragmentInteractionListener) {
            mListener = (SettingsResetPasswordFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //Detaches the current fragment to allow a new fragment to takes its place on switch
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * This function checks to make sure that the current password is correct by checking the
     * shared preferences. If it is, the new password is saved to the SharedPreferences file.
     * Otherwise, alerts the user that the password is incorrect.
     *
     * @param oldPassword The current password saved in Shared Preferences
     * @param newPassword The new password entered in by the user
     * @param current The current password editText
     * @param newer The new password editText
     * @param confirm The confirm password editText
     */
    private void passwordChange(String oldPassword, String newPassword, EditText current, EditText newer, EditText confirm ) {
        //Assign the password values to char arrays for hashing
        char[] passwordToCheck = oldPassword.toCharArray();
        char[] passwordToSave = newPassword.toCharArray();

        // Lookup the preferences file to access the stored password (If exists)
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.apply();
        String storedHash = pref.getString("passwordHash", null);

        //Sets up the password authenticator
        PasswordAuthentication authenticator = new PasswordAuthentication();

        //If the current password matches the value in shared preferences
        if (authenticator.authenticate(passwordToCheck, storedHash)) {
            //Hash the new password, then store it in the shared preferences
            String hashedPassword = authenticator.hash(passwordToSave);
            editor.putString("passwordHash", hashedPassword);
            editor.commit();

            //Notify the user of the changes, and set the editTexts to be empty, allowing for
            //another input if necessary
            Toast.makeText(getActivity(), "Password has been successfully reset", Toast.LENGTH_SHORT).show();
            current.setText(null);
            newer.setText(null);
            confirm.setText(null);

        //Otherwise, display an error to the user stating that the password they have entered is
        //incorrect.
        } else {
            ((TextInputLayout) getActivity().findViewById(R.id.settings_password_current_inputLayout))
                    .setErrorEnabled(true);
            ((TextInputLayout) getActivity().findViewById(R.id.settings_password_current_inputLayout))
                    .setError("Incorrect Password");
        }
    }
}
