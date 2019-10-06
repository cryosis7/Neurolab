package com.soteria.neurolab;

import android.content.Context;
import android.content.Intent;
import android.widget.VideoView;

import androidx.test.rule.ActivityTestRule;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.Game;
import com.soteria.neurolab.models.Patient;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
        intent.putExtra("PATIENT_NAME", gameName);
        intent.putExtra("ATTEMPTS", 3);

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
        VideoView video = rule.getActivity().findViewById(R.id.videoView);
        assertEquals(1, video.getCurrentPosition());
        video.start();
        assertTrue(video.isPlaying());
    }

    /**
     * The video should play and pause when tapped on
     */
    @Test
    public void testVideoTapped() {
        VideoView video = rule.getActivity().findViewById(R.id.videoView);

        assertFalse(video.isPlaying());
        onView(withId(R.id.videoView))
                .check(matches(isDisplayed()))
                .perform(click());
        assertTrue(video.isPlaying());

        onView(withId(R.id.videoView))
                .check(matches(isDisplayed()))
                .perform(click());
        assertFalse(video.isPlaying());
    }

    /**
     * The game should launch when the start button is tapped.
     */
    @Test
    public void testSeekTapped() {
        onView(withId(R.id.tutorial_start_button))
                .check(matches(allOf(withText("Start Game"), isDisplayed(), isClickable())))
                .perform(click());

        onView(withId(R.id.reaction_game_screen)).check(matches(isDisplayed()));
    }
}
