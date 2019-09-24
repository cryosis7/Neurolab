package com.soteria.neurolab;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

/**
 * This is the test class that is used alongside the VisualMemoryActivity class. There are not
 * very many unit tests associated with this class due to the random nature of the game making it
 * difficult to test for. Because of this, game logic will be tested manually instead of
 * automatically through unit tests, and will be documented as such in the testing documentation
 * found here: https://drive.google.com/drive/folders/1iPzXxW--IL1Zu92Zu4bCywg11pFGdt-T
 *
 * @author Richard Dasan
 */
@RunWith(AndroidJUnit4.class)
public class VisualMemoryGameTest {

    //Rule used for testing the activity
    @Rule
    public ActivityTestRule<VisualMemoryActivity> activityRule =
            new ActivityTestRule<>(VisualMemoryActivity.class, false, false);

    //Sets up the intent for use on load
    @Before
    public void setIntent() {
        Log.i("@Test", "------------------------------------------");
        Intent intent = new Intent();
        intent.putExtra("PATIENT_ID", 1);
        intent.putExtra("ATTEMPTS", 3);
        activityRule.launchActivity(intent);
    }

    /**
     *  Tests the intent to make sure that all the correct values are passed through
     */
    @Test
    public void testIntent() {
        int testPatientID = 1;
        String testAttempts = activityRule.getActivity().getIntent().getIntExtra("ATTEMPTS", 0) + " attempts remaining";

        Log.i("@Test", " --- --- --- --- Performing Test - Intent Values --- --- --- ---");
        onView(withId(R.id.visual_memory_attempts_incorrect_info)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), withText(testAttempts))));
        assertEquals(testPatientID, activityRule.getActivity().getIntent().getIntExtra("PATIENT_ID", 0));
    }

    /**
     *  Tests the onCreate function to make sure that all elements meant to be displayed on load of
     *  the class appear as per normal.
     */
    @Test
    public void onCreateTest() {
        String testStartGame = "Start Game";
        String testVisualMemory = "Visual Short Term Memory";
        String attemptsRemaining = "3 attempts remaining";

        Log.i("@Test", " --- --- --- --- Performing Test - OnCreate Function --- --- --- ---");
        onView(withId(R.id.visual_memory_game_info)).check(matches(allOf
                (withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), withText(testVisualMemory))));
        onView(withId(R.id.visual_memory_start_game)).check(matches(allOf
                (withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), withText(testStartGame))));
        onView(withId(R.id.visual_memory_attempts_incorrect_info)).check(matches(allOf
                (withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), withText(attemptsRemaining))));
    }

    /**
     * This test determines if the grid and its buttons are displayed whenever the start game button
     * is pressed.
     */
    @Test
    public void displayGridTest() {
        LinearLayout gridLayout;
        Button gridButton;

        Log.i("@Test", " --- --- --- --- Performing Test - setUpGrid Display --- --- --- ---");
        onView(withId(R.id.visual_memory_start_game)).perform(click());

        for( int i = 0; i < 5; i++ ) {
            gridLayout = activityRule.getActivity().findViewById(activityRule.getActivity().getResources().getIdentifier(
                    "gameBoardRow" + i, "id", activityRule.getActivity().getPackageName()));
            onView(withId(gridLayout.getId())).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }

        for( int i = 0; i < 5; i++ ) {
            for (int j = 0; j < 5; j++) {
                gridButton = activityRule.getActivity().findViewById(activityRule.getActivity().getResources().getIdentifier(
                        "gameButton" + i + j, "id", activityRule.getActivity().getPackageName()));
                onView(withId(gridButton.getId())).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            }
        }
    }
}
