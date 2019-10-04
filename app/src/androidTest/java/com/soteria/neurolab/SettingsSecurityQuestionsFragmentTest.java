package com.soteria.neurolab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.View;

import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.material.textfield.TextInputLayout;
import com.soteria.neurolab.utilities.PasswordAuthentication;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Checks.checkNotNull;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagKey;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
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
public class SettingsSecurityQuestionsFragmentTest {

    //Sets the rule for passing through intents and setting up the shared preferences
    @Rule
    public ActivityTestRule<SettingsActivity> settingsSecurityRule
            = new ActivityTestRule<>(SettingsActivity.class);

    //Grabs the resources for the app for string comparisons
    private Resources neurolabResources = getApplicationContext().getResources();

    @Before
    public void prepareQuestions() {
        //Prepares the password for insertion into the Shared Preferences
        PasswordAuthentication authenticator = new PasswordAuthentication();
        String hashedPassword = authenticator.hash("Password1!".toCharArray());

        //Grabs the shared preferences from the main activity
        Activity activity = settingsSecurityRule.getActivity();
        Context context = activity.getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.shared_preferences_filename), MODE_PRIVATE);

        //Set the test values as the questions and answers
        SharedPreferences.Editor testEditPreference = pref.edit();
        testEditPreference.putString("passwordHash", hashedPassword );
        testEditPreference.putString("QUESTION_ONE", "What is a potato?" );
        testEditPreference.putString("ANSWER_ONE", "A potato" );
        testEditPreference.putString("QUESTION_TWO", "What is a carrot?" );
        testEditPreference.putString("ANSWER_TWO", "Not a potato" );
        testEditPreference.apply();
        settingsSecurityRule.launchActivity(new Intent());

        //Navigates to the fragment the test focuses on
        onView(withId(R.id.settings_questions_option)).perform(click());
        onView(withId(R.id.settings_textview_security_title)).check(matches(isDisplayed()));
    }

    @Test
    public void testElementsOnLoad() {
        //Checks to see if all required elements are visible
        onView(withId(R.id.settings_textview_security_title)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_editText_current_password)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_security_question_one_title)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_security_question_two_title)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_security_question_one_input)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_security_question_two_input)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_security_answer_one_input)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_security_answer_two_input)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_questions_current_inputlayout)).check(matches(isEnabled()));
        onView(withId(R.id.settings_submit_questions)).check(matches(isDisplayed()));

        //Checks to make sure all elements have the required values attached
        onView(withId(R.id.settings_textview_security_title)).check(matches(allOf(isDisplayed(),
                withText(neurolabResources.getString(R.string.settings_textview_security_title))
        )));
        onView(withId(R.id.settings_editText_current_password)).check(matches(allOf(isDisplayed(),
                withHint(neurolabResources.getString(R.string.settings_password_current))
        )));
        onView(withId(R.id.settings_security_question_one_title)).check(matches(allOf(isDisplayed(),
                withText(neurolabResources.getString(R.string.settings_textview_security_question_one))
        )));
        onView(withId(R.id.settings_security_question_two_title)).check(matches(allOf(isDisplayed(),
                withText(neurolabResources.getString(R.string.settings_textview_security_question_two))
        )));
        onView(withId(R.id.settings_security_question_one_input)).check(matches(allOf(isDisplayed(),
                withHint(neurolabResources.getString(R.string.settings_editText_hint_one))
        )));
        onView(withId(R.id.settings_security_question_two_input)).check(matches(allOf(isDisplayed(),
                withHint(neurolabResources.getString(R.string.settings_editText_hint_two))
        )));
        onView(withId(R.id.settings_security_answer_one_input)).check(matches(allOf(isDisplayed(),
                withHint(neurolabResources.getString(R.string.settings_editText_security_answer_one))
        )));
        onView(withId(R.id.settings_security_answer_two_input)).check(matches(allOf(isDisplayed(),
                withHint(neurolabResources.getString(R.string.settings_editText_security_answer_two))
        )));
        onView(withId(R.id.settings_submit_questions)).check(matches(allOf(isDisplayed(),
                withText(neurolabResources.getString(R.string.settings_button_submit))
        )));
    }

    @Test
    public void sharedPreferencesTest() {

        //Grabs the shared preferences from the main activity
        Activity activity = settingsSecurityRule.getActivity();
        Context context = activity.getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.shared_preferences_filename), MODE_PRIVATE);

        onView(withId(R.id.settings_security_question_one_input)).check(matches(allOf(isDisplayed(),
                withText(pref.getString("QUESTION_ONE", null))
        )));
        onView(withId(R.id.settings_security_question_two_input)).check(matches(allOf(isDisplayed(),
                withText(pref.getString("QUESTION_TWO", null))
        )));
    }

    @Test
    public void resetQuestions() {
        onView(withId(R.id.settings_editText_current_password)).perform(typeText("Password1!"));
        onView(withId(R.id.settings_security_question_one_input)).perform(clearText(), typeText("What is 1 + 1?"));
        onView(withId(R.id.settings_security_question_two_input)).perform(clearText(), typeText("What is 2 + 2?"));
        onView(withId(R.id.settings_security_answer_one_input)).perform(typeText("2"));
        onView(withId(R.id.settings_security_answer_two_input)).perform(typeText("4"));
        onView(withId(R.id.settings_submit_questions)).perform(click());
    }

    @Test
    public void noQuestions() {
        onView(withId(R.id.settings_editText_current_password)).perform(typeText("Password1!"));
        onView(withId(R.id.settings_submit_questions)).perform(click());
        onView(withText("Please make sure both questions and answers are filled out"))
                .inRoot(withDecorView(not(is(settingsSecurityRule.getActivity().getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void oneQuestion() {
        onView(withId(R.id.settings_editText_current_password)).perform(typeText("Password1!"));
        onView(withId(R.id.settings_security_question_one_input)).perform(clearText(), typeText("What is 1 + 1?"));
        onView(withId(R.id.settings_security_answer_one_input)).perform(typeText("2"));
        onView(withId(R.id.settings_submit_questions)).perform(click());
        onView(withText("Please make sure both questions and answers are filled out"))
                .inRoot(withDecorView(not(is(settingsSecurityRule.getActivity().getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void wrongPassword() {
        onView(withId(R.id.settings_editText_current_password)).perform(typeText("Password2!"));
        onView(withId(R.id.settings_security_question_one_input)).perform(clearText(), typeText("What is 1 + 1?"));
        onView(withId(R.id.settings_security_question_two_input)).perform(clearText(), typeText("What is 2 + 2?"));
        onView(withId(R.id.settings_security_answer_one_input)).perform(typeText("2"));
        onView(withId(R.id.settings_security_answer_two_input)).perform(typeText("4"));
        onView(withId(R.id.settings_submit_questions)).perform(click());
        onView(withId(R.id.settings_questions_current_inputlayout)).check(matches(withErrorInInputLayout
                ("Password is incorrect, please try again")));
    }
    /**
     * Checking for an error in a TextInputLayout
     * @param stringMatcher
     * @return
     */
    public static Matcher<View> withErrorInInputLayout(final Matcher<String> stringMatcher) {
        checkNotNull(stringMatcher);

        return new BoundedMatcher<View, TextInputLayout>(TextInputLayout.class) {
            String actualError = "";

            @Override
            public void describeTo(Description description) {
                description.appendText("with error: ");
                stringMatcher.describeTo(description);
                description.appendText("But got: " + actualError);
            }

            @Override
            public boolean matchesSafely(TextInputLayout textInputLayout) {
                CharSequence error = textInputLayout.getError();
                if (error != null) {
                    actualError = error.toString();
                    return stringMatcher.matches(actualError);
                }
                return false;
            }
        };
    }

    public static Matcher<View> withErrorInInputLayout(final String string) {
        return withErrorInInputLayout(CoreMatchers.is(string));
    }

}