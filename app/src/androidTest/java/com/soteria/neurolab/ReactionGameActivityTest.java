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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ReactionGameActivityTest {

    @Rule
    public ActivityTestRule<ReactionGameActivity> rule =
            new ActivityTestRule<>(ReactionGameActivity.class);

    @Test
    public void testGameStartedCorrectly() {
        Log.i("@Test", "---   ---   Testing the start screen is loaded   ---   ---");

        ViewInteraction startButton = onView(withId(R.id.start_button));
        startButton.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        startButton.check(matches(withText(R.string.start_game)));
        onView(withId(R.id.circle_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.information_txt)).check(matches(withText(R.string.reactionGame_information)));
    }
}
