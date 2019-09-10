package com.soteria.neurolab;

import android.content.Intent;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.core.internal.deps.guava.collect.Lists;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.text.StringContainsInOrder.stringContainsInOrder;

@RunWith(AndroidJUnit4.class)
public class ReactionGameActivityTest {

    @Rule
    public ActivityTestRule<ReactionGameActivity> rule =
            new ActivityTestRule<>(ReactionGameActivity.class, false, false);

    @Before
    public void setIntent() {
        Log.i("@Test", "------------------------------------------");
        Intent intent = new Intent();
        intent.putExtra("PATIENT_ID", 1);
        rule.launchActivity(intent);
    }

    @Test
    public void testGameStartedCorrectly() {
        Log.i("@Test", "Testing Game Elements Are Loaded Properly.");

        onView(withId(R.id.reaction_game_start_button)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.start_game)
        )));

        onView(withId(R.id.reaction_game_circle_button)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE),
                withText(R.string.reactionGame_tap)
        )));

        onView(withId(R.id.reaction_game_information_txt)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.reactionGame_information)
        )));

        Log.i("@Test", "Test Completed.");
    }

    /**
     * It should press the start button to start a round and verify the start button has disappeared
     */
    @Test
    public void testTapStartButton() {
        Log.i("@Test", "Testing Start Button Functionality");
        onView(withId(R.id.reaction_game_start_button)).perform(click());

        onView(withId(R.id.reaction_game_information_txt)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText("")
        )));

        onView(withId(R.id.reaction_game_start_button)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE),
                withText(R.string.next_round)
        )));

        onView(withId(R.id.reaction_game_circle_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Log.i("@Test", "Test Complete");
    }

    /**
     * It should start a round of the game and tap the button when it appears.
     * After the circle button appears it should wait 250ms then tap the button and verify the layout.
     */
    @Test
    public void testTapCircleButton() {
        Log.i("@Test", "Testing Tapping The Circle Button");
        try {
            onView(withId(R.id.reaction_game_start_button)).perform(click());
            Thread.sleep(250);
            onView(withId(R.id.reaction_game_circle_button)).perform(click());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.reaction_game_information_txt)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(stringContainsInOrder(Lists.newArrayList("Round ", "Reaction Time: ", "ms")))
        )));
        onView(withId(R.id.reaction_game_start_button)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.next_round)
        )));
    }

    /**
     * It should tap the screen to start a round and verify the start button has disappeared
     */
    @Test
    public void testTapScreenToStart() {
        Log.i("@Test", "Testing Tap Screen To Start Functionality");
        onView(withId(R.id.reaction_game_screen)).perform(click());

        onView(withId(R.id.reaction_game_information_txt)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText("")
        )));

        onView(withId(R.id.reaction_game_start_button)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE),
                withText(R.string.next_round)
        )));

        onView(withId(R.id.reaction_game_circle_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Log.i("@Test", "Test Complete");
    }

    /**
     * It should start a round of the game and tap the button when it appears.
     * After the circle button appears it should wait 250ms then tap the button and verify the layout.
     */
    @Test
    public void testTapScreenToReact() {
        Log.i("@Test", "Testing Tapping The Screen To React");
        try {
            onView(withId(R.id.reaction_game_start_button)).perform(click());
            Thread.sleep(250);
            onView(withId(R.id.reaction_game_screen)).perform(click());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.reaction_game_information_txt)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(stringContainsInOrder(Lists.newArrayList("Round ", "Reaction Time: ", "ms")))
        )));
        onView(withId(R.id.reaction_game_start_button)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.next_round)
        )));
    }
}
