package com.soteria.neurolab;

import android.content.Context;
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

public class SettingsSecurityQuestionsFragment extends Fragment {

    private SettingsSecurityQuestionsFragment.OnFragmentInteractionListener mListener;
    private String questionOneString, questionTwoString, answerOneString, answerTwoString;

    public SettingsSecurityQuestionsFragment() {

    }

    public static SettingsSecurityQuestionsFragment newInstance() {
        return new SettingsSecurityQuestionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View questionsView =  inflater.inflate(R.layout.settings_questions_fragment, container, false);

        //UI element declarations
        final EditText confirmPassword = getActivity().findViewById(R.id.settings_editText_current_password);
        final EditText questionOne = getActivity().findViewById(R.id.settings_security_question_one_input);
        final EditText answerOne = getActivity().findViewById(R.id.settings_security_answer_one_input);
        final EditText questionTwo = getActivity().findViewById(R.id.settings_security_question_two_input);
        final EditText answerTwo = getActivity().findViewById(R.id.settings_security_answer_two_input);
        final Button submitButton = getActivity().findViewById(R.id.settings_submit_questions);

        questionOneString = questionOne.getText().toString();
        answerOneString = answerOne.getText().toString();
        questionTwoString = questionTwo.getText().toString();
        answerTwoString = answerTwo.getText().toString();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(confirmPassword.getText().toString().equals(""))
                    ((TextInputLayout) getActivity().findViewById(R.id.settings_password_current_inputLayout))
                            .setError("Password field is empty, please enter the account password to continue");
                else if(confirmPassword.getText().toString().equals(//DO THIS
                         )) {}
                else if(questionOneString.equals("") ||
                        answerOneString.equals("") ||
                        questionTwoString.equals("") ||
                        answerTwoString.equals("") ) {
                    Toast.makeText(getActivity(), "Please make sure both questions and answers are filled out",
                            Toast.LENGTH_LONG).show();
                }
                else { setQuestions(); }
            }
        });

        return questionsView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingsResetPasswordFragment.OnFragmentInteractionListener) {
            mListener = (SettingsSecurityQuestionsFragment.OnFragmentInteractionListener) context;
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

    private void setQuestions() {

    }
}
