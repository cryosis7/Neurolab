package com.soteria.neurolab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Checks.checkNotNull;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class CreatePasswordFragmentTest {
    private Context context = ApplicationProvider.getApplicationContext();

    @Rule
    public ActivityTestRule<LoginCreatePasswordActivity> rule = new ActivityTestRule<>(LoginCreatePasswordActivity.class, false, false);

    /**
     * Clears the password before testing to ensure the CreatePassword fragment is loaded.
     */
    @Before
    public void setUp() {
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("passwordHash", null);
        editor.apply();
        rule.launchActivity(new Intent());
    }

    /**
     * After starting the class, the screen shuold load and all the elements should be initialised correctly.
     */
    @Test
    public void testAllElementsLoad() {
        // Is everything displayed on the screen. (Fails on phones because it's not displayed)
        onView(withId(R.id.fragment_create_password_root)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.login_neurolab_logo), isDisplayed())).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.create_password_label)).check(matches(allOf(withText("Create A New Password"), isDisplayed())));
        onView(withId(R.id.create_password_edit_text_1)).check(matches(allOf(withHint("Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))));
        onView(withId(R.id.create_password_edit_text_2)).check(matches(allOf(withHint("Re-Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))));
        onView(withId(R.id.create_password_disclaimer_checkbox)).check(matches(allOf(not(isChecked()), isClickable(), withText("I agree to the disclaimer"), isDisplayed())));
        onView(withId(R.id.create_password_button)).check(matches(allOf(withText("Create Password"), isClickable(), isDisplayed())));
        onView(withId(R.id.create_password_disclaimer)).check(matches(allOf(withText("Disclaimer"), isClickable(), isDisplayed())));
    }

    /**
     * The test will input two different passwords then try to create the password.
     * An error message should be displayed telling the user that the passwords do not match
     */
    @Test
    public void testPasswordsDontMatch() {
        String pass1 = "Password1!";
        String pass2 = "Password2!";
        onView(withId(R.id.create_password_edit_text_1)).check(matches(allOf(withHint("Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))))
                .perform(clearText(), replaceText(pass1))
                .check(matches(withText(pass1)));
        onView(withId(R.id.create_password_edit_text_2)).check(matches(allOf(withHint("Re-Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))))
                .perform(clearText(), replaceText(pass2))
                .check(matches(withText(pass2)));

        onView(withId(R.id.create_password_button)).check(matches(allOf(withText("Create Password"), isClickable(), isDisplayed())))
                .perform(click());
        onView(withId(R.id.create_password_edit_text_1)).check(matches(withText("")));
        onView(withId(R.id.create_password_edit_text_2)).check(matches(withText("")));

        onView(withId(R.id.create_password_input_layout_1)).check(matches(withErrorInInputLayout("Passwords do not match")));
    }

    /**
     * The test will input a password that contains letters and a symbol but does not contain a number.
     * An error should be displayed to the user informing them that a number is required.
     */
    @Test
    public void testPasswordNoNumber() {
        String pass = "P@$$word";
        onView(withId(R.id.create_password_edit_text_1)).check(matches(allOf(withHint("Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))))
                .perform(clearText(), replaceText(pass))
                .check(matches(withText(pass)));
        onView(withId(R.id.create_password_edit_text_2)).check(matches(allOf(withHint("Re-Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))))
                .perform(clearText(), replaceText(pass))
                .check(matches(withText(pass)));

        onView(withId(R.id.create_password_button)).check(matches(allOf(withText("Create Password"), isClickable(), isDisplayed())))
                .perform(click());
        onView(withId(R.id.create_password_edit_text_1)).check(matches(withText("")));
        onView(withId(R.id.create_password_edit_text_2)).check(matches(withText("")));

        onView(withId(R.id.create_password_input_layout_1)).check(matches(withErrorInInputLayout("Password must be at least 6 characters long and contain a letter, number and symbol")));
    }

    /**
     * The test will input a password that contains letters and a number but does not contain a symbol.
     * An error should be displayed to the user informing them of the valid password criteria.
     */
    @Test
    public void testPasswordNoSymbol() {
        String pass = "P4ssw0rd";
        onView(withId(R.id.create_password_edit_text_1)).check(matches(allOf(withHint("Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))))
                .perform(clearText(), replaceText(pass))
                .check(matches(withText(pass)));
        onView(withId(R.id.create_password_edit_text_2)).check(matches(allOf(withHint("Re-Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))))
                .perform(clearText(), replaceText(pass))
                .check(matches(withText(pass)));

        onView(withId(R.id.create_password_button)).check(matches(allOf(withText("Create Password"), isClickable(), isDisplayed())))
                .perform(click());
        onView(withId(R.id.create_password_edit_text_1)).check(matches(withText("")));
        onView(withId(R.id.create_password_edit_text_2)).check(matches(withText("")));

        onView(withId(R.id.create_password_input_layout_1)).check(matches(withErrorInInputLayout("Password must be at least 6 characters long and contain a letter, number and symbol")));
    }

    /**
     * The test will input a password that contains numbers and a symbol but does not contain any letters.
     * An error should be displayed to the user informing them of the valid password criteria.
     */
    @Test
    public void testPasswordNoLetters() {
        String pass = "8008135!";
        onView(withId(R.id.create_password_edit_text_1)).check(matches(allOf(withHint("Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))))
                .perform(clearText(), replaceText(pass))
                .check(matches(withText(pass)));
        onView(withId(R.id.create_password_edit_text_2)).check(matches(allOf(withHint("Re-Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))))
                .perform(clearText(), replaceText(pass))
                .check(matches(withText(pass)));

        onView(withId(R.id.create_password_button)).check(matches(allOf(withText("Create Password"), isClickable(), isDisplayed())))
                .perform(click());
        onView(withId(R.id.create_password_edit_text_1)).check(matches(withText("")));
        onView(withId(R.id.create_password_edit_text_2)).check(matches(withText("")));

        onView(withId(R.id.create_password_input_layout_1)).check(matches(withErrorInInputLayout("Password must be at least 6 characters long and contain a letter, number and symbol")));
    }

    /**
     * Test whether a disclaimer dialog appears when the checkbox is checked
     */
    @Test
    public void testCheckboxDisclaimerAppears() {
        onView(withId(R.id.create_password_disclaimer_checkbox))
                .check(matches(allOf(not(isChecked()), isClickable(), withText("I agree to the disclaimer"), isDisplayed())))
                .perform(click());
        onView(withText(R.string.disclaimer_body)).check(matches(isDisplayed()));
    }

    /**
     * Test whether the app is closed when the user declines the disclaimer.
     */
    @Test
    public void testCheckboxYeet() {
        onView(withId(R.id.create_password_disclaimer_checkbox))
                .check(matches(allOf(not(isChecked()), isClickable(), withText("I agree to the disclaimer"), isDisplayed())))
                .perform(click());

        onView(withText(R.string.disclaimer_body)).check(matches(isDisplayed()));
        onView(allOf(isClickable(), withText("Decline")))
                .check(matches(isDisplayed()))
                .perform(click());

        assertTrue(rule.getActivity().isFinishing());
    }

    /**
     * Test whether the dialog is successfully minimised when the user accepts the disclaimer
     */
    @Test
    public void testCheckboxAccepted() {
        onView(withId(R.id.create_password_disclaimer_checkbox))
                .check(matches(allOf(not(isChecked()), isClickable(), withText("I agree to the disclaimer"), isDisplayed())))
                .perform(click());

        onView(withText(R.string.disclaimer_body)).check(matches(isDisplayed()));
        onView(allOf(isClickable(), withText("Confirm")))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.disclaimer_body)).check(doesNotExist());
    }

    /**
     * Test whether the disclaimer dialog is successfully loaded when the disclaimer button is pressed,
     * and minimised when the user accepts the disclaimer.
     */
    @Test
    public void testDisclaimerButton() {
        onView(withId(R.id.create_password_disclaimer))
                .check(matches(allOf(withText("Disclaimer"), isClickable(), isDisplayed())))
                .perform(click());

        onView(withText(R.string.disclaimer_body)).check(matches(isDisplayed()));
        onView(allOf(isClickable(), withText("Confirm")))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.disclaimer_body)).check(doesNotExist());
    }

    /**
     * Test whether the disclaimer dialog is successfully loaded when the disclaimer button is pressed,
     * and minimised when the user presses the back button.
     */
    @Test
    public void testDisclaimerWithBackButton() {
        onView(withId(R.id.create_password_disclaimer))
                .check(matches(allOf(withText("Disclaimer"), isClickable(), isDisplayed())))
                .perform(click());

        onView(withText(R.string.disclaimer_body)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onView(withText(R.string.disclaimer_body)).check(doesNotExist());
    }

    /**
     * The test will input a password that contains all the correct criteria.
     * The disclaimer will be accepted and the password will be created. The test should successfully create the password.
     */
    @Test public void testValidPassword() {
        String pass = "P@$$w0rd";

        // Enter the password into the two fields.
        onView(withId(R.id.create_password_edit_text_1))
                .check(matches(allOf(withHint("Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))))
                .perform(clearText(), replaceText(pass))
                .check(matches(withText(pass)));
        onView(withId(R.id.create_password_edit_text_2))
                .check(matches(allOf(withHint("Re-Enter Password"), isFocusable(), isDisplayed(), not(isSelected()))))
                .perform(clearText(), replaceText(pass))
                .check(matches(withText(pass)));

        // Accept disclaimer and close dialog.
        onView(withId(R.id.create_password_disclaimer_checkbox))
                .check(matches(allOf(not(isChecked()), isClickable(), withText("I agree to the disclaimer"), isDisplayed())))
                .perform(click());
        onView(withText(R.string.disclaimer_body)).check(matches(isDisplayed()));
        onView(allOf(isClickable(), withText("Confirm")))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withText(R.string.disclaimer_body)).check(doesNotExist());
        onView(withId(R.id.create_password_disclaimer_checkbox)).check(matches(isChecked()));

        // Press create password button + Verify password boxes are cleared.
        onView(withId(R.id.create_password_button))
                .check(matches(allOf(withText("Create Password"), isClickable(), isDisplayed())))
                .perform(click());
        onView(withId(R.id.create_password_edit_text_1)).check(matches(withText("")));
        onView(withId(R.id.create_password_edit_text_2)).check(matches(withText("")));

        // Confirm login screen is loaded.
        onView(withId(R.id.fragment_login_root)).check(matches(isDisplayed()));

        // Confirm new password is set.
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        assertNotNull(pref.getString("passwordHash", null));
        editor.apply();
    }


    /**
     * Checking for an error in a TextInputLayout
     *
     * @param stringMatcher
     * @return
     */
    private static Matcher<View> withErrorInInputLayout(final Matcher<String> stringMatcher) {
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

    /**
     * Custom Matcher to check for an error in an input layout.
     * @param string The error message to check for
     * @return
     */
    private static Matcher<View> withErrorInInputLayout(final String string) {
        return withErrorInInputLayout(is(string));
    }
}