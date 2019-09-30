package com.soteria.neurolab;

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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class VisualAttentionTest {

    @Rule
    public ActivityTestRule<VisualAttentionGame> rule =
            new ActivityTestRule<>(VisualAttentionGame.class);

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
                withText(R.string.visualAttention_tap_text)
        )));
    }

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

    @Test
    public void testDisplayGrid4x4(){
        testGrid(4);
    }

    @Test
    public void testFindTargets4x4() {
        testFindTargets(4, 4);
    }

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

    @Test
    public void testDisplayGrid5x5(){
        testFindTargets4x4();
        testNextRound4x4();
        testFindTargets4x4();
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

    @Test
    public void testNextRound5x5(){
        testDisplayGrid5x5();
        testFindTargets(5, 7);
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.visual_attention_grid2)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testDisplayGrid6x6(){
        testNextRound5x5();
        testGrid(5);
        testFindTargets(5,7);
        onView(withId(R.id.visual_attention_submit_button)).perform(click());
        testGrid(5);
        testFindTargets(5, 7);
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.visual_attention_grid3)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testNextRounds6x6(){
        testDisplayGrid6x6();
        testFindTargets(6, 8);

        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.visual_attention_grid3)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testDisplayScore(){
        String percentage = "100.0%";

        testNextRounds6x6();
        testGrid(6);
        testFindTargets(6, 8);
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        testGrid(6);
        testFindTargets(6, 8);
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        testGrid(6);
        testFindTargets(6, 8);
        onView(withId(R.id.visual_attention_submit_button)).perform(click());

        onView(withId(R.id.visual_attention_score_screen)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.visual_attention_score_text)).check(matches(
                withText(percentage)));

    }

    @Test
    public void testPlayAgain(){
        testDisplayScore();

        onView(withId(R.id.visual_attention_play_again_btn)).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click());

        testGameLoads();
    }

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
