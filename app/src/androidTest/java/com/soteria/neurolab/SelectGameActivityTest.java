package com.soteria.neurolab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameAssignment;
import com.soteria.neurolab.models.Patient;
import com.soteria.neurolab.utilities.PasswordAuthentication;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.CoreMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SelectGameActivityTest {
    private String patientReference = "SC05";
    private Patient patient;
    private Context context;

    @Rule
    public ActivityTestRule<ViewPatientDetails> rule = new ActivityTestRule<>(ViewPatientDetails.class, false, false);

    @Before
    public void init() {
        Intent intent = new Intent();
        intent.putExtra( "PATIENT_REFERENCE", patientReference);
        rule.launchActivity(intent);

        onView(withId(R.id.runGamesButton))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.select_game_root))
                .check(matches(isDisplayed()));

        context = rule.getActivity().getApplicationContext();
        patient = new DatabaseAccess(context).getPatient(patientReference);
    }

    /**
     * Tests whether all elements are loaded onto the screen properly.
     */
    @Test
    public void testElementsLoaded() {
        onView(withId(R.id.select_game_header)).check(matches(allOf(isDisplayed(), withText("Select Game"))));

        List<GameAssignment> assignments = new DatabaseAccess(context).getAssignments(patient.getPatientID());
        onView(withId(R.id.select_game_list)).check(matches(allOf(isDisplayed(), hasChildCount(assignments.size()))));
    }

    /**
     * Tests that the recycler view items contain the right information.
     * Loops through each assignment checking it exists in the recycler view.
     */
    @Test
    public void testValidData() {
        DatabaseAccess db = new DatabaseAccess(context);
        List<GameAssignment> assignments = db.getAssignments(patient.getPatientID());

        for (GameAssignment assignment : assignments) {
            String gameName = db.getGame(assignment.getGameID()).getGameName();
            onView(withId(R.id.select_game_list))
                    .check(matches(hasDescendant(withText(gameName))));
        }
    }

    /**
     * Validates that 'Reaction Time' is launched when it is tapped when the patient has attempts left
     */
    @Test
    public void testSelectGameAttemptsLeft() {
        DatabaseAccess db = new DatabaseAccess(context);
        int gameId = db.getGameId("Reaction Time");
        GameAssignment assignment = db.getAssignment(patient.getPatientID(), gameId);

        int dailyAttempts = db.getTodaysSessions(patient.getPatientID(), gameId).size();
        int savedPreviousAttemptLimit = assignment.getGameAttempts();

        assignment.setGameAttempts(dailyAttempts + 1);
        db.updateAssignment(assignment);

        int maxAttempts = db.getAssignment(patient.getPatientID(), gameId).getGameAttempts();
        int attemptsLeft = maxAttempts - dailyAttempts;
        assertTrue(attemptsLeft > 0);

        onView(withText("Reaction Time"))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.reaction_game_screen)).check(matches(isDisplayed()));

        assignment.setGameAttempts(savedPreviousAttemptLimit);
        db.updateAssignment(assignment);
    }

    /**
     * Validates that 'Reaction Time' is not launched when it is tapped.
     */
    @Test
    public void testSelectGameNoAttempts() {
        DatabaseAccess db = new DatabaseAccess(context);
        int gameId = db.getGameId("Reaction Time");
        GameAssignment assignment = db.getAssignment(patient.getPatientID(), gameId);
        int savedPreviousAttemptLimit = assignment.getGameAttempts();
        assignment.setGameAttempts(0);
        db.updateAssignment(assignment);

        int maxAttempts = db.getAssignment(patient.getPatientID(), gameId).getGameAttempts();
        int dailyAttempts = db.getTodaysSessions(patient.getPatientID(), gameId).size();
        int attemptsLeft = maxAttempts - dailyAttempts;
        assertTrue(attemptsLeft <= 0);

        onView(withText("Reaction Time"))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.reaction_game_screen)).check(doesNotExist());
        onView(withText("There are no attempts remaining for this game."))
                .inRoot(withDecorView(not(is(rule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        assignment.setGameAttempts(savedPreviousAttemptLimit);
        db.updateAssignment(assignment);
    }

    /**
     * Tests that when pressing the back button a password prompt is displayed and entering
     * the correct password will take the user back to the view patient screen.
     */
    @Test
    public void testExitWithPassword() {
        // Set Up
        String testPassword = "password";
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String previousPassword = pref.getString("passwordHash", null);
        editor.putString("passwordHash", new PasswordAuthentication().hash(testPassword.toCharArray()));
        editor.apply();
        assertNotNull(pref.getString("passwordHash", null));

        // Begin actual test
        Espresso.pressBack();

        onView(withInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD))
                .check(matches(isDisplayed()))
                .perform(replaceText(testPassword));
        onView(withText("OK"))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.viewPatientDetailsActivity)).check(matches(isDisplayed()));

        // Resetting password to original value.
        editor.putString("passwordHash", previousPassword);
        editor.apply();
    }

    /**
     * Test attempting to exit with wrong password
     */
    @Test
    public void testExitWithoutPassword() {
        // Set Up
        String testPassword = "password";
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences_filename), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String previousPassword = pref.getString("passwordHash", null);
        editor.putString("passwordHash", new PasswordAuthentication().hash(testPassword.toCharArray()));
        editor.apply();
        assertNotNull(pref.getString("passwordHash", null));

        // Begin actual test
        Espresso.pressBack();

        onView(withInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD))
                .check(matches(isDisplayed()))
                .perform(replaceText("Not the password"));
        onView(withText("OK"))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.viewPatientDetailsActivity)).check(doesNotExist());
        onView(withId(R.id.select_game_root)).check(matches(isDisplayed()));

        onView(withText("Incorrect Password"))
                .inRoot(withDecorView(not(is(rule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        // Resetting password to original value.
        editor.putString("passwordHash", previousPassword);
        editor.apply();
    }
}
