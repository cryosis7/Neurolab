package com.soteria.neurolab;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Checks.checkNotNull;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class LoginCreatePasswordActivityTest {

    @Rule
    public IntentsTestRule<LoginCreatePasswordActivity> rule = new IntentsTestRule<>(LoginCreatePasswordActivity.class);

    @Before
    public void setUp() throws Throwable {
        clearOverlayFocus();
    }

    /**
     * The elements should render properly.
     * All tests are dependant on this.
     */
    @Test
    public void a_loadAllElementsTest() {
        onView(withId(R.id.login_password_label)).check(matches(allOf(
                isDisplayed(),
                withText("Enter Password")
        )));
        onView(withId(R.id.login_password_edittext)).check(matches(allOf(
                isDisplayed(),
                withInputType(129),
                withText("")
        )));
        onView(withId(R.id.login_sign_in_button)).check(matches(allOf(
                isDisplayed(),
                not(isEnabled()),
                withText("Sign In")
        )));
    }

    /**
     * Sign In button should only be enabled when the minimum amount of characters are entered.
     */
    @Test
    public void b_signInButtonEnables() {
        int passwordLengthMinimum = 6;
        String shortPass = new String(new char[passwordLengthMinimum - 1]).replace('\0', 'x');
        String validPass = new String(new char[passwordLengthMinimum]).replace('\0', 'x');

        // Should be disabled
        onView(withId(R.id.login_sign_in_button)).check(matches(not(isEnabled())));
        // Should stay disabled

        onView(withId(R.id.login_password_edittext)).perform(replaceText(shortPass));
        onView(withId(R.id.login_sign_in_button)).check(matches(not(isEnabled())));
        // Should enable
        onView(withId(R.id.login_password_edittext)).perform(clearText(), replaceText(validPass));
        onView(withId(R.id.login_sign_in_button)).check(matches(isEnabled()));
        // Should disable
        onView(withId(R.id.login_password_edittext)).perform(replaceText(shortPass));
        onView(withId(R.id.login_sign_in_button)).check(matches(not(isEnabled())));
    }

    /**
     * Should store the password if it does not exist.
     * @throws Throwable
     */
    @Test
    public void c_setPassword() throws Throwable {
        Activity activity = rule.getActivity();
        Context context = activity.getApplicationContext();

        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("passwordHash");
        editor.apply();

        String storedHash = pref.getString("passwordHash", null);
        assertNull(storedHash);

        onView(withId(R.id.login_password_edittext)).perform(replaceText("Password"));
        onView(withId(R.id.login_sign_in_button)).perform(click());
        Thread.sleep(300);
        clearOverlayFocus();

        storedHash = pref.getString("passwordHash", null);
        assertNotNull(storedHash);
        onView(withId(R.id.login_password_edittext)).check(matches(withText("")));
        onView(withId(R.id.login_sign_in_button)).check(matches(not(isEnabled())));
    }

    /**
     * Should display an error when given an incorrect password
     * @throws Throwable
     */
    @Test
    public void d_incorrectPassword() throws Throwable {
        Context context = rule.getActivity().getApplicationContext();

        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.apply();

        String storedHash = pref.getString("passwordHash", null);
        assertNotNull(storedHash);

        onView(withId(R.id.login_password_edittext)).perform(replaceText("IncorrectPassword"));
        onView(withId(R.id.login_sign_in_button)).perform(click());

        clearOverlayFocus();
        onView(withId(R.id.login_password_edittext)).check(matches(withText("")));
        onView(withId(R.id.login_password_inputLayout)).check(matches(withErrorInInputLayout("Incorrect Password")));
        onView(withId(R.id.login_sign_in_button)).check(matches(not(isEnabled())));
    }

    /**
     * Should launch the main screen when given the correct password.
     */
    @Test
    public void e_correctPassword() {
        Context context = rule.getActivity().getApplicationContext();

        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.apply();

        String storedHash = pref.getString("passwordHash", null);
        assertNotNull(storedHash);

        onView(withId(R.id.login_password_edittext)).perform(replaceText("Password"));
        onView(withId(R.id.login_sign_in_button)).perform(click());

        intended(hasComponent(SearchCreateDeleteActivity.class.getName()));
    }

    /**
     * Helper function to remove focus from the text box - which was causing errors due to a LastPass
     * overlay interrupting the tests.
     * @throws Throwable
     */
    private void clearOverlayFocus() throws Throwable {
        rule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rule.getActivity().findViewById(R.id.login_password_edittext).clearFocus();
            }
        });
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
        return withErrorInInputLayout(is(string));
    }
}
