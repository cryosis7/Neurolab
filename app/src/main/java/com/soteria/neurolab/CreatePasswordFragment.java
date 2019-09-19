package com.soteria.neurolab;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.soteria.neurolab.utilities.PasswordAuthentication;

import static android.content.Context.MODE_PRIVATE;

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
                if(validPassword()) {
                    createPassword();
                    ((LoginCreatePasswordActivity)getActivity()).switchFragment();
                }
            }
        });

        return view;
    }

    /**
     * Validates whether the two passwords are equal and meet the following criteria:
     * At least 6 characters
     * Must contain at least one letter, number and symbol
     * @return true if password meet all the criteria
     */
    private boolean validPassword() {
        TextInputEditText passwordEditText1 = getView().findViewById(R.id.create_password_edit_text_1);
        TextInputEditText passwordEditText2 = getView().findViewById(R.id.create_password_edit_text_2);
        String password1 = passwordEditText1.getText().toString();
        String password2 = passwordEditText2.getText().toString();

        if (!password1.equals(password2)) {
            //TODO: Set Error
            return false;
        }
        else if (!password1.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$")) {
            //TODO: Set Error
            return false;
        }

        return true;
    }

    private void createPassword() {
        TextInputEditText passwordEditText1 = getView().findViewById(R.id.create_password_edit_text_1);
        String password1 = passwordEditText1.getText().toString();
        TextInputEditText passwordEditText2 = getView().findViewById(R.id.create_password_edit_text_2);
        passwordEditText1.setText("");
        passwordEditText2.setText("");

        PasswordAuthentication authenticator = new PasswordAuthentication();
        String hashedPassword = authenticator.hash(password1.toCharArray());

        SharedPreferences pref = getContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("passwordHash", hashedPassword);
        editor.apply();
    }
}
