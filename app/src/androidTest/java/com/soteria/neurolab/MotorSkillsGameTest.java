package com.soteria.neurolab;

import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameAssignment;
import com.soteria.neurolab.models.GameSession;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MotorSkillsGameTest {
    private DatabaseAccess db;
    private int attempts;


    @Rule
    public ActivityTestRule<MotorSkillsGameActivity> rule =
            new ActivityTestRule<>(MotorSkillsGameActivity.class, false, false);

    @Before
    public void setIntent() {
        //Setup test data for all tests
        Log.i("@Before", "Setup database");
        db = new DatabaseAccess(InstrumentationRegistry.getInstrumentation().getTargetContext());
        db.deleteAllPatients();
        db.deleteAllSessions();
        db.deleteAllAssignments();
        createTestPatients();

        //Create intent and start activity
        Log.i("@Before", "Set intent");
        Intent intent = new Intent();
        intent.putExtra("PATIENT_ID", db.getPatient("JK93").getPatientID());
        attempts = db.getAssignment(db.getPatient("JK93").getPatientID(), DatabaseAccess.GAME_ENUM.MOTOR.getGameID()).getGameAttempts();
        intent.putExtra("ATTEMPTS", attempts);
        rule.launchActivity(intent);
    }


    @Test
    public void testPageLoadedCorrectly() {
        Log.i("@Test", "testPageLoadedCorrectly: Testing UI Elements Are Loaded Properly.");

        onView(withId(R.id.motor_skills_game_title)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.title_motor_skills)
        )));

        String attemptsString = attempts == 1 ? rule.getActivity().getResources().getString(R.string.visual_memory_textview_attempts_singular, attempts)
                : rule.getActivity().getResources().getString(R.string.visual_memory_textview_attempts_plural, attempts);

        onView(withId(R.id.motor_skills_game_incorrect_info)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(attemptsString)
        )));

        onView(withId(R.id.motor_skills_game_constraint_layout)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
        )));

        onView(withId(R.id.motor_skills_game_start_game)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.start_game)
        )));

        Log.i("@Test", "testPageLoadedCorrectly: Test Completed.");
    }


    @Test
    public void testGameStart(){
        Log.i("@Test", "testGameStart: Testing Game Elements Are Loaded Properly.");

        onView(withId(R.id.motor_skills_game_start_game)).perform(click());

        //Check A and B Buttons are displayed
        onView(allOf(withTagValue(is((Object) "A")), isDisplayed()));
        onView(allOf(withTagValue(is((Object) "B")), isDisplayed()));

        onView(withId(R.id.motor_skills_game_start_game)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)
        )));

        String roundString = rule.getActivity().getResources().getString(R.string.motor_skills_game_round, 1);

        onView(withId(R.id.motor_skills_game_title)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(roundString)
        )));

        String triesString = rule.getActivity().getResources().getString(R.string.visual_memory_textview_try_plural, 5);

        onView(withId(R.id.motor_skills_game_incorrect_info)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(triesString)
        )));
    }


    @Test
    public void testCompleteRound(){
        Log.i("@Test", "testCompleteRound: Testing the next round starts correctly.");

        onView(withId(R.id.motor_skills_game_start_game)).perform(click());
        onView(allOf(withTagValue(is((Object) "A")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());

        //Round 2 should start after pressing those 3 buttons
        onView(allOf(withTagValue(is((Object) "A")), isDisplayed()));
        onView(allOf(withTagValue(is((Object) "B")), isDisplayed()));
        onView(allOf(withTagValue(is((Object) "C")), isDisplayed()));

        String roundString = rule.getActivity().getResources().getString(R.string.motor_skills_game_round, 2);

        onView(withId(R.id.motor_skills_game_title)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(roundString)
        )));

        String triesString = rule.getActivity().getResources().getString(R.string.visual_memory_textview_try_plural, 5);

        onView(withId(R.id.motor_skills_game_incorrect_info)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(triesString)
        )));
    }


    @Test
    public void testCompleteGame(){
        Log.i("@Test", "testCompleteGame: Testing the UI displays correctly after game is completed.");

        onView(withId(R.id.motor_skills_game_start_game)).perform(click());
        onView(allOf(withTagValue(is((Object) "A")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());

        onView(allOf(withTagValue(is((Object) "A")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "C")))).perform(click());

        onView(allOf(withTagValue(is((Object) "A")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "C")))).perform(click());
        onView(allOf(withTagValue(is((Object) "D")))).perform(click());

        onView(allOf(withTagValue(is((Object) "A")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "C")))).perform(click());
        onView(allOf(withTagValue(is((Object) "D")))).perform(click());
        onView(allOf(withTagValue(is((Object) "E")))).perform(click());

        onView(allOf(withTagValue(is((Object) "A")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "C")))).perform(click());
        onView(allOf(withTagValue(is((Object) "D")))).perform(click());
        onView(allOf(withTagValue(is((Object) "E")))).perform(click());
        onView(allOf(withTagValue(is((Object) "F")))).perform(click());

        onView(allOf(withTagValue(is((Object) "A")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "C")))).perform(click());
        onView(allOf(withTagValue(is((Object) "D")))).perform(click());
        onView(allOf(withTagValue(is((Object) "E")))).perform(click());
        onView(allOf(withTagValue(is((Object) "F")))).perform(click());
        onView(allOf(withTagValue(is((Object) "G")))).perform(click());

        onView(allOf(withTagValue(is((Object) "A")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "C")))).perform(click());
        onView(allOf(withTagValue(is((Object) "D")))).perform(click());
        onView(allOf(withTagValue(is((Object) "E")))).perform(click());
        onView(allOf(withTagValue(is((Object) "F")))).perform(click());
        onView(allOf(withTagValue(is((Object) "G")))).perform(click());
        onView(allOf(withTagValue(is((Object) "H")))).perform(click());

        onView(allOf(withTagValue(is((Object) "A")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "C")))).perform(click());
        onView(allOf(withTagValue(is((Object) "D")))).perform(click());
        onView(allOf(withTagValue(is((Object) "E")))).perform(click());
        onView(allOf(withTagValue(is((Object) "F")))).perform(click());
        onView(allOf(withTagValue(is((Object) "G")))).perform(click());
        onView(allOf(withTagValue(is((Object) "H")))).perform(click());
        onView(allOf(withTagValue(is((Object) "I")))).perform(click());

        onView(allOf(withTagValue(is((Object) "A")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "C")))).perform(click());
        onView(allOf(withTagValue(is((Object) "D")))).perform(click());
        onView(allOf(withTagValue(is((Object) "E")))).perform(click());
        onView(allOf(withTagValue(is((Object) "F")))).perform(click());
        onView(allOf(withTagValue(is((Object) "G")))).perform(click());
        onView(allOf(withTagValue(is((Object) "H")))).perform(click());
        onView(allOf(withTagValue(is((Object) "I")))).perform(click());
        onView(allOf(withTagValue(is((Object) "J")))).perform(click());

        String roundString = rule.getActivity().getResources().getString(R.string.motor_skills_game_score_multiple, 9);

        onView(withId(R.id.motor_skills_game_title)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(roundString)
        )));

        attempts--;

        String attemptsString = attempts == 1 ? rule.getActivity().getResources().getString(R.string.visual_memory_textview_attempts_singular, attempts)
                : rule.getActivity().getResources().getString(R.string.visual_memory_textview_attempts_plural, attempts);

        onView(withId(R.id.motor_skills_game_incorrect_info)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(attemptsString)
        )));

        onView(withId(R.id.motor_skills_game_play_again)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.visual_memory_button_continue)
        )));

        onView(withId(R.id.motor_skills_game_exit_game)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.visual_memory_button_exit)
        )));

        //Assert database game session created
        List<GameSession> gameSessionList =  db.getAllSessions(String.valueOf(db.getPatient("JK93").getPatientID()), "3");
        assertThat(gameSessionList.size(), is(1));
        assertThat(gameSessionList.get(0).getMetrics(), is((double)9));
    }


    @Test
    public void testTriesText(){
        Log.i("@Test", "testTriesText: Testing tries message correctly updates after a mis-click.");

        onView(withId(R.id.motor_skills_game_start_game)).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());

        String triesString = rule.getActivity().getResources().getString(R.string.visual_memory_textview_try_plural, 4);

        onView(withId(R.id.motor_skills_game_incorrect_info)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(triesString)
        )));

        onView(allOf(withTagValue(is((Object) "B")), isDisplayed()));
    }


    @Test
    public void testFailRound(){
        Log.i("@Test", "testFailRound: Testing game completes after 5 mis-clicks");

        onView(withId(R.id.motor_skills_game_start_game)).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());

        String roundString = rule.getActivity().getResources().getString(R.string.motor_skills_game_score_multiple, 0);

        onView(withId(R.id.motor_skills_game_title)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(roundString)
        )));

        attempts--;

        String attemptsString = attempts == 1 ? rule.getActivity().getResources().getString(R.string.visual_memory_textview_attempts_singular, attempts)
                : rule.getActivity().getResources().getString(R.string.visual_memory_textview_attempts_plural, attempts);

        onView(withId(R.id.motor_skills_game_incorrect_info)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(attemptsString)
        )));

        onView(withId(R.id.motor_skills_game_play_again)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.visual_memory_button_continue)
        )));

        onView(withId(R.id.motor_skills_game_exit_game)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.visual_memory_button_exit)
        )));

        //Assert database game session created
        List<GameSession> gameSessionList =  db.getAllSessions(String.valueOf(db.getPatient("JK93").getPatientID()), "3");
        assertThat(gameSessionList.size(), is(1));
        assertThat(gameSessionList.get(0).getMetrics(), is((double)0));
    }


    @Test
    public void testNoAttemptsLeft(){
        Log.i("@Test", "testNoAttemptsLeft: Testing that you cannot play again on 0 attempts left");

        onView(withId(R.id.motor_skills_game_start_game)).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());

        onView(withId(R.id.motor_skills_game_play_again)).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());

        onView(withId(R.id.motor_skills_game_play_again)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)
        )));

        onView(withId(R.id.motor_skills_game_exit_game)).check(matches(allOf(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                withText(R.string.visual_memory_button_exit)
        )));
    }


    @Test
    public void testExitGame(){
        Log.i("@Test", "testExitGame: Testing exit game button takes you back to the correct page");

        onView(withId(R.id.motor_skills_game_start_game)).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());
        onView(allOf(withTagValue(is((Object) "B")))).perform(click());

        onView(withId(R.id.motor_skills_game_exit_game)).perform(click());

        //Get test from editPatientDetails for changing page - Jason needs to push changes on that branch from home PC
    }


    public void createTestPatients(){
        db.createPatient("JK93");
        db.createAssignment(new GameAssignment(DatabaseAccess.GAME_ENUM.MOTOR.getGameID(), db.getPatient("JK93").getPatientID(), 2 ));
    }

}
