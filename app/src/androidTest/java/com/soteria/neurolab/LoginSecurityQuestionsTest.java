package com.soteria.neurolab;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.core.AllOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagKey;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * This class is a test class for the LoginSecurityQuestiions class. If focuses on making sure all
 * elements are appears, including alertDialogs, and that security questions can be answered
 * correctly and appropriate error messages appear when they are not answered correctly.
 *
 * @author Richard Dasan
 */
@RunWith(AndroidJUnit4.class)
public class LoginSecurityQuestionsTest {

    //Sets the rule for passing through intents and setting up the shared preferences
    @Rule
    public ActivityTestRule<LoginSecurityQuestions> loginSecurityRule
            = new ActivityTestRule<>(LoginSecurityQuestions.class );

    //Grabs the resources for the app for string comparisons
    private Resources neurolabResources = getApplicationContext().getResources();

    /**
     * This class focuses on making sure all elements are visible and populated with the right
     * information
     */
    @Test
    public void testElementsOnLoad()
    {
        //The first section of this test checks to make sure all elements are visible to the
        //user
        onView(withId(R.id.login_security_textview_main_title)).check(matches(isDisplayed()));
        onView(withId(R.id.login_security_textview_question_one_title)).check(matches(isDisplayed()));
        onView(withId(R.id.login_security_textview_question_two_title)).check(matches(isDisplayed()));
        onView(withId(R.id.login_security_textview_question_one)).check(matches(isEnabled()));
        onView(withId(R.id.login_security_textview_question_two)).check(matches(isEnabled()));
        onView(withId(R.id.login_security_edittext_answer_one_input)).check(matches(isDisplayed()));
        onView(withId(R.id.login_security_edittext_answer_two_input)).check(matches(isDisplayed()));
        onView(withId(R.id.login_security_button_submit_questions)).check(matches(isDisplayed()));
        onView(withId(R.id.login_security_button_forgot_answers)).check(matches(isDisplayed()));

        //The second section of this test checks to make sure the elements loaded have the required
        //starting values
        onView(withId(R.id.login_security_textview_main_title)).check(matches(allOf(isDisplayed(),
                withText(neurolabResources.getString(R.string.login_security_textview_reset))
        )));
        onView(withId(R.id.login_security_textview_question_one_title)).check(matches(allOf(isDisplayed(),
                withText(neurolabResources.getString(R.string.login_security_textview_question_one))
        )));
        onView(withId(R.id.login_security_textview_question_two_title)).check(matches(allOf(isDisplayed(),
                withText(neurolabResources.getString(R.string.login_security_textview_question_two))
        )));
        onView(withId(R.id.login_security_button_submit_questions)).check(matches(allOf(isDisplayed(),
                withText(neurolabResources.getString(R.string.settings_button_submit))
        )));
        onView(withId(R.id.login_security_button_forgot_answers)).check(matches(allOf(isDisplayed(),
                withText(neurolabResources.getString(R.string.login_security_button_forgot))
        )));
    }

    /**
     * This test checks to see that the right action and message is displayed whenever there is
     * no information in the answer boxes.
     */
    @Test
    public void noAnswers()
    {
        //Pushes the submit button immediately, entering in no information. Should display
        //the incorrect entry message.
        onView(withId(R.id.login_security_button_submit_questions)).perform(click());
        onView(withText(R.string.security_question_answers_incorrect))
                .inRoot(withDecorView(not(is(
                        loginSecurityRule.getActivity().getWindow().getDecorView()))))
                            .check(matches(isDisplayed()));
    }

    /**
     * This test checks to see that the right action and message is displayed whenever there is
     * the wrong answer in the answer boxes
     */
    @Test
    public void incorrectAnswers()
    {
        //Grabs the shared preferences from the main activity
        Activity activity = loginSecurityRule.getActivity();
        Context context = activity.getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.shared_preferences_filename), MODE_PRIVATE);

        //Set the test values as the questions and answers
        SharedPreferences.Editor testEditPreference = pref.edit();
        testEditPreference.putString("QUESTION_ONE", "testQuestion1");
        testEditPreference.putString("QUESTION_TWO", "testQuestion2");
        testEditPreference.putString("ANSWER_ONE", "testAnswer1");
        testEditPreference.putString("ANSWER_TWO", "testAnswer2");
        testEditPreference.apply();

        //Sets the answers in their respective fields
        onView(withId(R.id.login_security_edittext_answer_one_input)).perform(typeText("test"));
        onView(withId(R.id.login_security_edittext_answer_two_input)).perform(typeText("test"));


        //Pushes the submit button after the answers have been entered. Should display
        //the incorrect entry message.
        onView(withId(R.id.login_security_button_submit_questions)).perform(click());
        onView(withText(R.string.security_question_answers_incorrect))
                .inRoot(withDecorView(not(is(
                        loginSecurityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    /**
     * This test checks to see that the user would be passed to the Login screen whenever there is
     * the right answer in the answer boxes
     */
    @Test
    public void correctAnswers()
    {
        //Grabs the shared preferences from the main activity
        Activity activity = loginSecurityRule.getActivity();
        Context context = activity.getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.shared_preferences_filename), MODE_PRIVATE);

        //Set the test values as the questions and answers
        SharedPreferences.Editor testEditPreference = pref.edit();
        testEditPreference.putString("QUESTION_ONE", "testQuestion1");
        testEditPreference.putString("QUESTION_TWO", "testQuestion2");
        testEditPreference.putString("ANSWER_ONE", "testAnswer1");
        testEditPreference.putString("ANSWER_TWO", "testAnswer2");
        testEditPreference.apply();

        //Sets the answers in their respective fields
        onView(withId(R.id.login_security_edittext_answer_one_input)).perform(typeText("testAnswer1"));
        onView(withId(R.id.login_security_edittext_answer_two_input)).perform(typeText("testAnswer2"));

        //Pushes the submit button after the answers have been entered. Should display
        //the login fragment to enter the main app
        onView(withId(R.id.login_security_button_submit_questions)).perform(click());
        onView(withId(R.id.login_password_label)).check(matches(isEnabled()));
    }

    /**
     * This test checks the alert dialog that appears whenever a user presses the "Forgot Answers"
     * button. Checks to see that the right text appears and that the back button functions as normal
     */
    @Test
    public void forgetAnswersAlertDialogTextCheck()
    {
        //String to test with the inside of the alertDialog
        String testEraseDialog = "If you wish to reset your password and security questions, you " +
                "must erase all data in the database. Information deleted this way will not be " +
                "recovered.\n\n Are you sure you want to proceed?";

        //Displays the alert dialog for when the user presses "Forgot Answers"
        onView(withId(R.id.login_security_button_forgot_answers)).perform(click());

        //Checks that the text is the same as testEraseDialog
        onView(withText(R.string.security_erase_database_warning)).check(matches(AllOf.allOf
                (withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), withText(testEraseDialog))));

        //Checks that the back button works correctly
        onView(withId(android.R.id.button2)).perform(click());
    }

    /**
     * This test checks to see that the wipe database alertDialog appears and its back button works.
     */
    @Test
    public void wipeDatabaseAlertDialogTextCheck()
    {
        //String used to test the text inside the alertDialog
        String testConfirmDialog = "Please enter in the security code to wipe the database " +
                "information. This code can be found inside the user manual.\n\nOnce you confirm " +
                "the security code, the reset will happen immediately.\n\n";

        //Presses the "forgot answers" button
        onView(withId(R.id.login_security_button_forgot_answers)).perform(click());
        //Clicks the "Erase" button inside the alertDialog
        onView(withText("Erase")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Erase")).perform(click());
        //Checks the alertDialog body text contains testConfirmDialog
        onView(withText(R.string.security_reset_information)).check(matches(AllOf.allOf
                (withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), withText(testConfirmDialog))));
        //Checks that the back button works correctly
        onView(withId(android.R.id.button2)).perform(click());
    }

    /**
     * This test checks that the database is cleared correctly upon entering in the right code.
     */
    @Test
    public void wipeDatabaseCorrectCode()
    {
        //Presses the "forgot answers" button
        onView(withId(R.id.login_security_button_forgot_answers)).perform(click());
        //Clicks the "Erase" button inside the alertDialog
        onView(withText("Erase")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Erase")).perform(click());
        //Enters in the correct code inside the editText in the new alertDialog
        onView(withTagKey(R.string.security_tag_security_code)).perform(typeText("3579753"));
        //Presses the confirm button to wipe the database
        onView(withText("Confirm")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        //Checks that the relevant toast that appears whenever the user wipes the database appears
        onView(withText(R.string.security_correct_security_code))
                .inRoot(withDecorView(not(is(
                        loginSecurityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    /**
     * This functions checks that the database is not wiped whenever an incorrect code is used
     */
    @Test
    public void wipeDatabaseIncorrectCode()
    {
        //Presses the "forgot answers" button
        onView(withId(R.id.login_security_button_forgot_answers)).perform(click());
        //Clicks the "Erase" button inside the alertDialog
        onView(withText("Erase")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Erase")).perform(click());
        //Enters in an incorrect code inside the editText in the new alertDialog
        onView(withTagKey(R.string.security_tag_security_code)).perform(typeText("3579754"));
        //Presses the confirm button to attempt to wipe the database
        onView(withText("Confirm")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        //Checks that the relevant toast that appears whenever the user attempts to wipes the
        //database and fails appears
        onView(withText(R.string.security_incorrect_security_code))
                .inRoot(withDecorView(not(is(loginSecurityRule.getActivity().getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));
    }
}