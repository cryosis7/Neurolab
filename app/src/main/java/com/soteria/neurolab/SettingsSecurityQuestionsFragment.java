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

/**
 * This class sets or reset the security questions for the account. This can only be done if the
 * account password is known. If the security questions have been filled in, they are saved in the
 * Shared Preferences file. If there are already Shared Preferences for security questions, the
 * questions are loaded into the questions editTexts upon load.
 *
 * @author Richard Dasan
 */
public class SettingsSecurityQuestionsFragment extends Fragment {

    //Fragment listener used to attach and detach the fragment from the current page.
    //Despite saying it is not in use, it is used in the onAttach and onDetach classes.
    private SettingsSecurityQuestionsFragment.OnFragmentInteractionListener mListener;

    //The strings used to store the questions and answers made by the user
    private String questionOneString, questionTwoString, answerOneString, answerTwoString;

    //Default constructor for the class
    public SettingsSecurityQuestionsFragment() { }

    public static SettingsSecurityQuestionsFragment newInstance() {
        return new SettingsSecurityQuestionsFragment();
    }

    /**
     * A required function of any displaying class. Holds the savedInstanceState.
     *
     * @param savedInstanceState Necessary for onCreate, allows for storing of user data for use later
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This class handles the button presses of the user and stores all code to check whether
     * all information has been filled out by the user.
     *
     * @param inflater A LayoutInflater used to help set up the view
     * @param container A ViewGroup
     * @param savedInstanceState See onCreate
     * @return the view to be displayed
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Sets up the view
        final View questionsView =  inflater.inflate(R.layout.fragment_settings_questions, container, false);

        //Retrieve the shared preferences
        final SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);

        //UI element declarations
        final EditText confirmPassword = questionsView.findViewById(R.id.settings_editText_current_password);
        final EditText questionOne = questionsView.findViewById(R.id.settings_security_question_one_input);
        final EditText answerOne = questionsView.findViewById(R.id.settings_security_answer_one_input);
        final EditText questionTwo = questionsView.findViewById(R.id.settings_security_question_two_input);
        final EditText answerTwo = questionsView.findViewById(R.id.settings_security_answer_two_input);
        final Button submitButton = questionsView.findViewById(R.id.settings_submit_questions);

        //If the questions exist in Shared Preferences, set them to the questions editText.
        //Otherwise it will use the preset values.
        if(prefs.contains("QUESTION_ONE") && prefs.contains("QUESTION_TWO")) {
            questionOne.setText(prefs.getString("QUESTION_ONE", "Question One"));
            questionTwo.setText(prefs.getString("QUESTION_TWO", "Question Two"));
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set up an authenticator to compare stored password and user entered password
                PasswordAuthentication authenticator = new PasswordAuthentication();

                //Grab values from the editTexts and store them.
                questionOneString = questionOne.getText().toString();
                answerOneString = answerOne.getText().toString();
                questionTwoString = questionTwo.getText().toString();
                answerTwoString = answerTwo.getText().toString();

                //Prepare user entered password for authentication
                char[] password = confirmPassword.getText().toString().toCharArray();
                SharedPreferences.Editor editor = prefs.edit();
                editor.apply();

                //Grab the encrypted password from Shared Preferences
                String storedHash = prefs.getString("passwordHash", null);

                //Check to make sure all text boxes are entered correctly and have information in them
                //If the password textbox is empty
                if(confirmPassword.getText().toString().equals(""))
                    //Reply with an error stating password is empty
                    ((TextInputLayout) getActivity().findViewById(R.id.settings_questions_current_inputlayout))
                            .setError("Password field is empty, please enter the account password to continue");
                //If the password does not match the one stored in Shared Preferences
                else if(!authenticator.authenticate(password, storedHash)) {
                    //Reply with an error stating passwords do not match
                    ((TextInputLayout) getActivity().findViewById(R.id.settings_questions_current_inputlayout))
                            .setError("Password is incorrect, please try again");
                //If the security questions and answers fields are empty
                } else if(questionOneString.equals("") ||
                        answerOneString.equals("") ||
                        questionTwoString.equals("") ||
                        answerTwoString.equals("") ) {
                    //Reply with a toast stating those fields need to be filled in.
                    Toast.makeText(getActivity(), "Please make sure both questions and answers are filled out",
                            Toast.LENGTH_LONG).show();
                //Else take them to the function that sets the questions in Shared Preferences
                }  else {
                    setQuestions(prefs);
                    answerOne.setText("");
                    answerTwo.setText("");
                }
            }
        });

        //return the view so the users can view it
        return questionsView;
    }

    /**
     * This function helps set up the fragment so that the user can view it, and set up its
     * fragment interaction listener so the user can interact with its elements
     *
     * @param context The context of the parent activity
     */
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

    /**
     * This function helps with shifting the fragment over to another fragment or to an activity
     * once the user has decided to leave the page.
     */
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
     * This function sets the questions and answers inputted by the user to Shared Preferences.
     *
     * @param prefs the Shared Preferences information passed to it by the onCreate Function
     */
    private void setQuestions( SharedPreferences prefs) {
        //Opens up the shared preferences and sets the questions and answers to their relevant
        //strings
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("QUESTION_ONE", questionOneString);
        editor.putString("QUESTION_TWO", questionTwoString);
        editor.putString("ANSWER_ONE", answerOneString);
        editor.putString("ANSWER_TWO", answerTwoString);
        editor.apply();
        //Notifies the user that the changes have been made
        Toast.makeText(getContext(), "Security questions have been saved", Toast.LENGTH_SHORT).show();
    }
}
