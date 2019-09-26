package com.soteria.neurolab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasBackground;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;


@RunWith(AndroidJUnit4.class)
public class CreatePasswordFragmentTest {
    private Context context = ApplicationProvider.getApplicationContext();

    @Rule
    public ActivityTestRule<LoginCreatePasswordActivity> rule = new ActivityTestRule<>(LoginCreatePasswordActivity.class, false, false);

    @Before
    public void setUp() {
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("passwordHash", null);
        editor.apply();
        rule.launchActivity(new Intent());
    }

    @Test
    public void testAllElementsLoad() {
        // Is everything displayed on the screen.
        onView(allOf(withId(R.id.login_neurolab_logo), isDisplayed())).check(matches(isCompletelyDisplayed()));
        onView(withChild(withId(R.id.create_password_button))).check(matches(isCompletelyDisplayed()));
        onView(withChild(withId(R.id.login_password_label))).check(matches(isCompletelyDisplayed()));

    }
}