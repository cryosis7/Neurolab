package com.soteria.neurolab;

import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameAssignment;
import com.soteria.neurolab.models.Patient;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.CoreMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;

@RunWith(AndroidJUnit4.class)
public class SelectGameActivityTest {
    private String patientReference = "TestingRef";
    private Patient patient;

    @Rule
    public ActivityTestRule<SelectGameActivity> rule = new ActivityTestRule<>(SelectGameActivity.class, false, false);

    @Before
    public void init() {
        final DatabaseAccess db = new DatabaseAccess(rule.getActivity().getApplicationContext());
        patient = db.getPatient(patientReference);
        if (patient != null) db.deletePatient(patient);
        db.createPatient(patientReference);
        patient = db.getPatient(patientReference);
        db.createAssignment(new GameAssignment(
                db.getGameId(rule.getActivity().getResources().getString(R.string.title_reaction_time)),
                patient.getPatientID(),3));
        db.createAssignment(new GameAssignment(
                db.getGameId(rule.getActivity().getResources().getString(R.string.title_visual_short_term_memory)),
                patient.getPatientID(),3));

        Intent intent = new Intent();
        intent.putExtra( "PATIENT_ID", patient.getPatientID() );
        rule.launchActivity(intent);
    }

    @Test
    public void testElementsLoaded() {
        onView(withId(R.id.select_game_header)).check(matches(isDisplayed()));
        onView(withId(R.id.select_game_list)).check(matches(isDisplayed()));
    }

    @After
    public void cleanUp() {
        final DatabaseAccess db = new DatabaseAccess(rule.getActivity().getApplicationContext());
        db.deletePatient(patient);
    }
}
