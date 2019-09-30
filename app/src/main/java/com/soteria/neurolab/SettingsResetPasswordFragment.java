package com.soteria.neurolab;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

public class SettingsResetPasswordFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public SettingsResetPasswordFragment() {

    }

    public static SettingsResetPasswordFragment newInstance() {
        return new SettingsResetPasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View passwordView =  inflater.inflate(R.layout.fragment_settings_reset, container, false);

        //UI element declarations
        final Button submitButton = passwordView.findViewById(R.id.settings_submit_reset);
        final EditText passwordCurrent = passwordView.findViewById(R.id.settings_current_password);
        final EditText passwordNew = passwordView.findViewById(R.id.settings_password_new);
        final EditText passwordConfirm = passwordView.findViewById(R.id.settings_password_confirm);

        //Code to run when the submit button is pressed
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordCurrent.getText().toString().equals("")) {
                    ((TextInputLayout) getActivity().findViewById(R.id.settings_password_current_inputLayout)).setError("Current Password is blank");
                } else if (!passwordNew.getText().toString().equals(passwordConfirm.getText().toString())){
                    ((TextInputLayout) getActivity().findViewById(R.id.settings_password_new_inputLayout)).setError("New passwords do not match");
                } else { passwordChange(passwordNew.getText().toString()); }
            }
        });
        return passwordView;
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void passwordChange(String oldPassword) {
        char[] password = oldPassword.toCharArray();
        // Lookup the preferences file to access the stored password (If exists)
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.apply();
        String storedHash = pref.getString("passwordHash", null);

        PasswordAuthentication authenticator = new PasswordAuthentication();
        if (authenticator.authenticate(password, storedHash)) {
            String hashedPassword = authenticator.hash(password);
            editor.putString("passwordHash", hashedPassword);
            editor.commit();
            Toast.makeText(getActivity(), "Password has been successfully reset", Toast.LENGTH_SHORT).show();
        }
        else {
            ((TextInputLayout) getActivity().findViewById(R.id.settings_password_new_inputLayout)).setError("Incorrect Password");
        }
    }
}
