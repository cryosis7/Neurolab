package com.soteria.neurolab;

import android.content.Intent;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class ReactionGameActivityTest {

    @Rule
    public ActivityTestRule<ReactionGameActivity> rule =
            new ActivityTestRule<>(ReactionGameActivity.class, false, false);

    @Before
    public void setIntent() {
        Log.i("@Test", "------------------------------------------");
        Intent intent = new Intent();
        intent.putExtra("PATIENT_ID", "TEST_ID");
        rule.launchActivity(intent);
    }

    @Test
    public void testGameStartedCorrectly() {
        Log.i("@Test", "Testing Game Elements Are Loaded Properly.");

        onView(withId(R.id.start_button)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.start_game)
        )));

        onView(withId(R.id.circle_button)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE),
                withText(R.string.reactionGame_tap)
        )));

        onView(withId(R.id.information_txt)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.reactionGame_information)
        )));

        Log.i("@Test", "Test Completed.");
    }

    @Test
    public void testTapStartButton() {
        Log.i("@Test", "Testing Start Button Functionality");
        onView(withId(R.id.start_button)).perform(click());

        onView(withId(R.id.information_txt)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText("")
        )));

        onView(withId(R.id.start_button)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE),
                withText(R.string.next_round)
        )));

        onView(withId(R.id.circle_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Log.i("@Test", "Test Complete");
    }

    //TODO: Test tapping circle button

    //TODO: Test tapping screen to initiate

    //TODO: Test tapping screen to react

    //TODO: Test tapping early

}
