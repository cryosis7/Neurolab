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
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Checks.checkNotNull;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagKey;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

/**
 * This class is a test class for the Reset Password fragment of the Settings class.
 * It checks to make sure that all elements are viewable and have the correct information displayed,
 * as well as checking the reset password functionality to check that it is working correctly.
 *
 * @author Richard Dasan
 */
@RunWith(AndroidJUnit4.class)
public class SettingsResetPasswordFragmentTest {

    //This rule sets the activity for all tests
    @Rule
    public ActivityTestRule<SettingsActivity> settingsResetRule
            = new ActivityTestRule<>(SettingsActivity.class);

    //Grabs the resources for the app for string comparisons
    private Resources neurolabResources = getApplicationContext().getResources();
    private PasswordAuthentication authenticator = new PasswordAuthentication();

    @Before
    public void preparePassword() {
        //Prepares the password for insertion into the Shared Preferences
        String hashedPassword = authenticator.hash("Password1!".toCharArray());

        //Grabs the shared preferences from the main activity
        Activity activity = settingsResetRule.getActivity();
        Context context = activity.getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.shared_preferences_filename), MODE_PRIVATE);

        //Set the test value as the password
        SharedPreferences.Editor testEditPreference = pref.edit();
        testEditPreference.putString("passwordHash", hashedPassword);
        testEditPreference.apply();
        settingsResetRule.launchActivity(new Intent());
    }

    /**
     *  This test checks to make sure all elements in this fragment are visible and have
     *  the appropriate values inside of them
     */
    @Test
    public void testElementsOnLoad() {
        //Checks to see if all required elements are visible
        onView(withId(R.id.settings_textview_password_title)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_current_password)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_password_new)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_password_confirm)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_submit_reset)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_password_current_inputLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_password_new_inputLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_password_confirm_inputLayout)).check(matches(isDisplayed()));

        //Checks to make sure all elements have the required values attached
        onView(withId(R.id.settings_textview_password_title)).check(matches(allOf(isDisplayed(),
                withText(neurolabResources.getString(R.string.settings_password_reset_title))
        )));
        onView(withId(R.id.settings_current_password)).check(matches(allOf(isDisplayed(),
                withHint(neurolabResources.getString(R.string.settings_password_current))
        )));
        onView(withId(R.id.settings_password_new)).check(matches(allOf(isDisplayed(),
                withHint(neurolabResources.getString(R.string.settings_password_new))
        )));
        onView(withId(R.id.settings_password_confirm)).check(matches(allOf(isDisplayed(),
                withHint(neurolabResources.getString(R.string.settings_password_repeat))
        )));
        onView(withId(R.id.settings_submit_reset)).check(matches(allOf(isDisplayed(),
                withText(neurolabResources.getString(R.string.settings_button_submit))
        )));
    }

    /**
     * This test makes sure that the appropriate error message appears whenever an incorrect
     * current password is entered in by the user
     */
    @Test
    public void incorrectPassword() {
        //Enters in the test values into the appropriate elements
        onView(withId(R.id.settings_current_password)).perform(typeText("Password1111"));
        onView(withId(R.id.settings_password_new)).perform(typeText("Password2@"));
        onView(withId(R.id.settings_password_confirm)).perform(typeText("Password2@"));

        //Clicks the submit button and checks that the error message displayed to the user mentions
        //to the user that an incorrect password has been entered
        onView(withId(R.id.settings_submit_reset)).perform(click());
        onView(withId(R.id.settings_password_current_inputLayout)).check(matches(withErrorInInputLayout
                ("Incorrect Password")));
    }

    /**
     * This test makes sure that the appropriate error message appears whenever a new password entered
     * in by the user does not meet the requirements of a new password
     */
    @Test
    public void setInvalidPassword() {
        //Enters in the test values into the appropriate elements
        onView(withId(R.id.settings_current_password)).perform(typeText("Password1!"));
        onView(withId(R.id.settings_password_new)).perform(typeText("word!"));
        onView(withId(R.id.settings_password_confirm)).perform(typeText("word!"));
        //Clicks the submit button and checks that the error message displayed to the user mentions
        //to the user that an invalid new password has been entered
        onView(withId(R.id.settings_submit_reset)).perform(click());
        onView(withId(R.id.settings_password_new_inputLayout)).check(matches(withErrorInInputLayout
                ("Password must be at least 6 characters and have a special character and a number")));

    }

    /**
     * This test makes sure that the valid password entered by the user overwrites the old password
     * in the shared preferences
     */
    @Test
    public void setValidPassword() {
        //Grabs the shared preferences from the main activity
        Activity activity = settingsResetRule.getActivity();
        Context context = activity.getApplicationContext();
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.shared_preferences_filename), MODE_PRIVATE);

        //Enters in the test values into the appropriate elements
        onView(withId(R.id.settings_current_password)).perform(typeText("Password1!"));
        onView(withId(R.id.settings_password_new)).perform(typeText("Password2@"));
        onView(withId(R.id.settings_password_confirm)).perform(typeText("Password2@"));

        //Clicks the submit button and checks the shared preferences to make sure that the password
        //has been successfully overwritten
        //TODO does not work correctly, new password gives differing hash value
        onView(withId(R.id.settings_submit_reset)).perform(click());
        String hashedPassword = authenticator.hash("Password2@".toCharArray());
        assertEquals(hashedPassword, pref.getString("passwordHash", null));
    }

    /**
     * This test checks if no password fields have been entered. If not, displays an appropriate
     * error message
     */
    @Test
    public void noFieldsFilled() {
        //Clicks the submit button and displays the error message on the current password editText
        onView(withId(R.id.settings_submit_reset)).perform(click());
        onView(withId(R.id.settings_password_current_inputLayout)).check(matches(withErrorInInputLayout
                ("Current password field is blank")));

    }

    /**
     * This test checks if only one password field have been entered. If not, displays an appropriate
     * error message
     */
    @Test
    public void oneFieldFilled() {
        //Enters in the appropriate values for this test
        onView(withId(R.id.settings_current_password)).perform(typeText("Password1!"));
        onView(withId(R.id.settings_password_new)).perform(typeText("Password2@"));

        //Clicks the submit button and displays the error message on the new password editText
        onView(withId(R.id.settings_submit_reset)).perform(click());
        onView(withId(R.id.settings_password_new_inputLayout)).check(matches(withErrorInInputLayout
                ("New passwords do not match")));
    }

    /**
     * This test checks if the new passwords entered are different. If they are, displays an appropriate
     * error message
     */
    @Test
    public void differentNewPasswords() {
        //Enters in the appropriate values for this test
        onView(withId(R.id.settings_current_password)).perform(typeText("Password1!"));
        onView(withId(R.id.settings_password_new)).perform(typeText("Password2@"));
        onView(withId(R.id.settings_password_confirm)).perform(typeText("Password3#"));

        //Clicks the submit button and displays the error message on the new password editText
        onView(withId(R.id.settings_submit_reset)).perform(click());
        onView(withId(R.id.settings_password_new_inputLayout)).check(matches(withErrorInInputLayout
                ("New passwords do not match")));
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