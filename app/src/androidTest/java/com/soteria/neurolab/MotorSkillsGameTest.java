package com.soteria.neurolab;

import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameAssignment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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
    public void testGameStartedCorrectly() {
        Log.i("@Test", "Testing Game Elements Are Loaded Properly.");

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

        Log.i("@Test", "Test Completed.");
    }

    


    public void createTestPatients(){
        db.createPatient("JK93");
        db.createAssignment(new GameAssignment(DatabaseAccess.GAME_ENUM.MOTOR.getGameID(), db.getPatient("JK93").getPatientID(), 1 ));
    }

}
