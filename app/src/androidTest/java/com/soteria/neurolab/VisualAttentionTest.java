package com.soteria.neurolab;

import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.espresso.matcher.ViewMatchers;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameSession;

import static androidx.test.espresso.Espresso.onView;
import static org.junit.Assert.*;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class VisualAttentionTest {

    @Rule
    public ActivityTestRule<VisualAttentionGame> rule =
            new ActivityTestRule<>(VisualAttentionGame.class, false, false);

    @Before
    public void setIntent(){
        Intent intent = new Intent();
        intent.putExtra("PATIENT_ID", 1);
        intent.putExtra("ATTEMPTS", 3);
        rule.launchActivity(intent);
    }

    /**
     * Checks the intent has been passed through
     */
    @Test
    public void testIntent(){
        int patientID = 1;
        int attemtps = 3;

        assertEquals(patientID, rule.getActivity().getIntent().getIntExtra("PATIENT_ID", 0));
        assertEquals(attemtps, rule.getActivity().getIntent().getIntExtra("ATTEMPTS", 0));
    }

    /**
     * Checks that the elements for round one have loaded
     */
    @Test
    public void testGameLoads(){
        String round = "Round 1";

        onView(withId(R.id.visual_attention_grid1)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));

        onView(withId(R.id.visual_attention_round_text)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(round)
        )));

        onView(withId(R.id.visual_attention_target_text)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.visualAttention_tap_text))));
    }

    /**
     * Method for testing that grid buttons are displayed
     * @param gridSize
     */
    public void testGrid(int gridSize){
        LinearLayout layout;
        ImageButton button;

        for(int i = 0; i < gridSize; i++){
            layout = rule.getActivity().findViewById(rule.getActivity().getResources().getIdentifier(
                    "gridRow" + i, "id", rule.getActivity().getPackageName()));
            onView(withId(layout.getId())).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }

        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                button = rule.getActivity().findViewById(rule.getActivity().getResources().getIdentifier(
                        "button_" + i + j, "id", rule.getActivity().getPackageName()));
                onView(withId(button.getId())).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            }
        }
    }

    /**
     * Method for testing the number of targets displayed in the grid, and tests that all targets are found
     * @param gridSize
     * @param numOfTargets
     */
    public void testFindTargets(int gridSize, int numOfTargets){
        ImageView target;
        ImageButton button;
        int targetsFound = 0;

        target = rule.getActivity().findViewById(rule.getActivity().getResources().getIdentifier(
                "visual_attention_target_image", "id", rule.getActivity().getPackageName()));

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                button = rule.getActivity().findViewById(rule.getActivity().getResources().getIdentifier(
                        "button_" + i + j, "id", rule.getActivity().getPackageName()));
                if(target.getTag().equals(button.getTag())){
                    int id = rule.getActivity().getResources().getIdentifier(
                            "button_" + i + j, "id", rule.getActivity().getPackageName());
                    onView(withId(id)).perform(click());
                    targetsFound++;
                }
            }
        }
        assertEquals(targetsFound, numOfTargets);
    }

    /**
     * Tests pressing submit without tapping any symbols
     */
    @Test
    public void testSubmitNoTaps(){
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        onView(withId(R.id.visual_attention_grid1)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
    }

    /**
     * Tests that the 4x4 grid is displayed
     */
    @Test
    public void testDisplayGrid4x4(){
        testGrid(4);
    }

    /**
     * Tests all targets are found im the 4x4 grid
     */
    @Test
    public void testFindTargets4x4() {
        testFindTargets(4, 4);
    }

    /**
     * Tests that the next round is displayed after pressing submit
     */
    @Test
    public void testNextRound4x4(){
        testFindTargets4x4();

        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.visual_attention_grid1)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    /**
     * Tests that the 5x5 grid is displayed after completing rounds 1, 2 and 3
     */
    @Test
    public void testDisplayGrid5x5(){
        testNextRound4x4();
        testNextRound4x4();
        testFindTargets4x4();

        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.visual_attention_grid2)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        testGrid(5);
    }

    /**
     * Tests all targets are found in the 5x5 grid
     */
    @Test
    public void testFindTargets5x5(){
        testFindTargets(5, 7);
    }

    /**
     * Tests that the mext round is displayed after pressing submit
     */
    @Test
    public void testNextRound5x5(){
        testDisplayGrid5x5();
        testFindTargets5x5();
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.visual_attention_grid2)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    /**
     * Tests that the 6x6 grid is displayed after completing rounds 4, 5 and 6
     */
    @Test
    public void testDisplayGrid6x6(){
        testNextRound5x5();
        testGrid(5);
        testFindTargets(5,7);
        onView(withId(R.id.visual_attention_submit_button)).perform(click());
        testGrid(5);
        testFindTargets(5, 7);
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        onView(withId(R.id.visual_attention_grid3)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    /**
     * Tests all targets are found in the 6x6 grid
     */
    @Test
    public void testFindTargets6x6(){
        testFindTargets(6, 8);
    }

    /**
     * Tests that the mext round is displayed after pressing submit
     */
    @Test
    public void testNextRound6x6(){
        testDisplayGrid6x6();
        testFindTargets6x6();
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.visual_attention_grid3)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    /**
     * Tests that the score screen, along with the correct score, play again and exit buttons are
     * all displayed
     */
    @Test
    public void testDisplayScore(){
        String percentage = "100.0%";

        testNextRound6x6();
        testGrid(6);
        testFindTargets6x6();
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        testGrid(6);
        testFindTargets6x6();
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        testGrid(6);
        testFindTargets6x6();
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        onView(withId(R.id.visual_attention_score_screen)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.visual_attention_score_text)).check(matches(
                withText(percentage)));

        onView(withId(R.id.visual_attention_play_btn)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.visual_attention_exit_btn)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    /**
     * Tests that pressing the play again button runs the game again
     */
    @Test
    public void testPlayAgain(){
        testDisplayScore();

        onView(withId(R.id.visual_attention_play_btn)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click());

        testGameLoads();
    }

    /**
     * Tests that a session for the given patient and game are added to the database
     */
    @Test
    public void testGetSessions(){
        DatabaseAccess db = DatabaseAccess.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());
        String patientID = "1";
        String gameID = "4";
        double metrics = 100.0;

        GameSession session = db.getAllSessions(patientID, gameID).get(0);
        assertEquals(patientID, Integer.toString(session.getPatientID()));
        assertEquals(gameID, Integer.toString(session.getGameID()));
        assertEquals(metrics, session.getMetrics(), 1);
    }
}
