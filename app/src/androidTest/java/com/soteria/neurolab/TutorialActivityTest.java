package com.soteria.neurolab;

import android.content.Context;
import android.content.Intent;
import android.widget.VideoView;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.Game;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TutorialActivityTest {
    private Context context;
    private Game game;

    @Rule
    public ActivityTestRule<TutorialActivity> rule = new ActivityTestRule<>(TutorialActivity.class, false, false);

    @Before
    public void init() {
        String gameName = "Reaction Time";

        Intent intent = new Intent();
        intent.putExtra("PATIENT_REFERENCE", "SC05");
        intent.putExtra("GAME_NAME", gameName);
        intent.putExtra("ATTEMPTS", 9);

        rule.launchActivity(intent);
        context = rule.getActivity().getApplicationContext();

        DatabaseAccess db = new DatabaseAccess(context);
        game = db.getGameByName(gameName);
    }

    /**
     * The header should be initialised to the title of the game and the description should be
     * pulled from the database and filled in below the header.
     */
    @Test
    public void testInformationFilled() {
        onView(withId(R.id.tutorial_header)).check(matches(withText(game.getGameName())));
        onView(withId(R.id.tutorial_description)).check(matches(withText(game.getGameDesc())));
    }

    /**
     * The game tutorial video should be loaded, displayed to the user and ready for watching.
     */
    @Test
    public void testVideoLoaded() {
        onView(withId(R.id.video_header)).check(matches(withText("Video Demonstration")));
        onView(withId(R.id.video_view)).check(matches(isDisplayed()));

        VideoView video = rule.getActivity().findViewById(R.id.video_view);
        assertEquals(1, video.getCurrentPosition());
        video.start();
        assertTrue(video.isPlaying());
    }

    /**
     * The video should play and pause when tapped on
     */
    @Test
    public void testVideoTapped() {
        VideoView video = rule.getActivity().findViewById(R.id.video_view);
        assertFalse(video.isPlaying());

        onView(withId(R.id.video_view))
                .check(matches(isDisplayed()))
                .perform(click());
        assertTrue(video.isPlaying());

        onView(withId(R.id.video_view))
                .inRoot(withDecorView(is(rule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()))
                .perform(click());
        assertFalse(video.isPlaying());
    }

    /**
     * The game should launch when the start button is tapped.
     */
    @Test
    public void testStartTapped() {
        onView(withId(R.id.tutorial_start_button))
                .check(matches(allOf(withText("Start Game"), isDisplayed(), isClickable())))
                .perform(click());

        onView(withId(R.id.reaction_game_screen)).check(matches(isDisplayed()));
        onView(withId(R.id.tutorial_root)).check(doesNotExist());
    }

    /**
     * The select game screen should be loaded when the hardware back button is pressed
     */
    @Test
    public void testHardwareBackButton() {
        // This test is dependant on being launched from the select game screen so need to launch it
        // from there.
        rule.finishActivity();
        Intent intent = new Intent();
        intent.putExtra("PATIENT_REFERENCE", "SC05");
        ActivityTestRule<SelectGameActivity> selectRule = new ActivityTestRule<>(SelectGameActivity.class, false, false);
        selectRule.launchActivity(intent);

        onView(withText(R.string.title_reaction_time))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.tutorial_root))
                .check(matches(isDisplayed()));
        onView(withId(R.id.select_game_root)).check(doesNotExist());

        context = selectRule.getActivity().getApplicationContext();
        game = new DatabaseAccess(context).getGameByName(context.getString(R.string.title_reaction_time));

        // The actual test
        Espresso.pressBack();
        onView(withId(R.id.select_game_root)).check(matches(isDisplayed()));
        onView(withId(R.id.tutorial_root)).check(doesNotExist());
    }

    /**
     * Once a game is started, the back button should take you back to the select game screen and
     * not the game tutorial screen.
     */
    @Test
    public void testBackFromGame() {
        // This test is dependant on being launched from the select game screen so need to launch it
        // from there.
        rule.finishActivity();
        Intent intent = new Intent();
        intent.putExtra("PATIENT_REFERENCE", "SC05");
        ActivityTestRule<SelectGameActivity> selectRule = new ActivityTestRule<>(SelectGameActivity.class, false, false);
        selectRule.launchActivity(intent);

        onView(withText(R.string.title_reaction_time))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.tutorial_root))
                .check(matches(isDisplayed()));
        onView(withId(R.id.select_game_root)).check(doesNotExist());

        context = selectRule.getActivity().getApplicationContext();
        game = new DatabaseAccess(context).getGameByName(context.getString(R.string.title_reaction_time));

        // The actual test
        onView(withId(R.id.tutorial_start_button))
                .check(matches(allOf(isClickable(), isDisplayed())))
                .perform(click());

        Espresso.pressBack();
        onView(withId(R.id.select_game_root)).check(matches(isDisplayed()));
        onView(withId(R.id.tutorial_root)).check(doesNotExist());
        onView(withId(R.id.reaction_game_screen)).check(doesNotExist());
    }
}
