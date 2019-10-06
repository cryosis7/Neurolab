package com.soteria.neurolab;

import android.content.ComponentName;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.material.textfield.TextInputLayout;
import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameAssignment;
import com.soteria.neurolab.models.Patient;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.isA;

import android.content.Intent;
import androidx.test.espresso.assertion.ViewAssertions;

import junit.framework.TestCase;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;

@RunWith(AndroidJUnit4.class)
public class EditPatientDetailsTest {
    private DatabaseAccess db;
    @Rule
    public ActivityTestRule<EditPatientDetails> rule = new ActivityTestRule<>(EditPatientDetails.class, false, false);

    @Before
    public void setupActivity() {
        this.db = new DatabaseAccess(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Log.i("@Before", "setupActivity: Clearing patients table and adding a new patient");
        this.db.deleteAllPatients();
        this.db.createPatient("JASON93");
        DatabaseAccess databaseAccess = this.db;
        databaseAccess.createAssignment(new GameAssignment(1, databaseAccess.getPatient("JASON93").getPatientID(), 3));
        Intent testIntent = new Intent();
        testIntent.putExtra("PATIENT_REFERENCE", "JASON93");
        this.rule.launchActivity(testIntent);
    }

    @Test
    public void testPreConditions() {
        Assert.assertNotNull(this.db);
    }

    @Test
    public void testDontChangeAnythingConfirm() {
        Log.i("@Test", "testDontChangeAnythingConfirm: Pressing confirm");
        onView(withId(R.id.editPatientDetails_confirmButton)).perform(new ViewAction[]{ViewActions.click()});
        Log.i("@Test", "testDontChangeAnythingConfirm: Asserting toast message and change page");
        onView(withText(R.string.edit_patient_details_patient_updated)).inRoot(MobileViewMatchers.isToast()).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.patientIDTitleTextView)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.patientIDTitleTextView)).check(ViewAssertions.matches(withText("Patient: JASON93")));
        List<Patient> patients = this.db.getAllPatients();
        MatcherAssert.assertThat(Integer.valueOf(patients.size()), CoreMatchers.is(1));
        MatcherAssert.assertThat(patients.get(0).getPatientReference(), CoreMatchers.is("JASON93"));
        List<GameAssignment> ga = this.db.getAssignments(patients.get(0));
        MatcherAssert.assertThat(Integer.valueOf(ga.size()), CoreMatchers.is(1));
        MatcherAssert.assertThat(Integer.valueOf(ga.get(0).getGameID()), CoreMatchers.is(1));
    }

    @Test
    public void testEditPatientWithSpecialCharacters() {
        Log.i("@Test", "testEditPatientWithSpecialCharacters: Clearing the PatientID field");
        onView(withId(R.id.editPatientDetails_inputPatientID)).perform(replaceText("!@#$"));
        onView(withId(R.id.editPatientDetails_inputPatientID)).check(matches(withText("!@#$")));
        Log.i("@Test", "testEditPatientWithSpecialCharacters: Clicking confirm button");
        onView(withId(R.id.editPatientDetails_confirmButton)).perform(click());
        Log.i("@Test", "testCreatePatientWithSpecialCharacters: Checking error message has been shown");
        onView(withId(R.id.editPatientDetails_inputPatientIDLayout)).check(matches(textInputLayoutErrorTextMatcher("PatientID contains 1 or more illegal characters")));
        List<Patient> patients = this.db.getAllPatients();
        assertThat(Integer.valueOf(patients.size()), CoreMatchers.is(1));
        assertThat(patients.get(0).getPatientReference(), CoreMatchers.is("JASON93"));
        List<GameAssignment> ga = this.db.getAssignments(patients.get(0));
        assertThat(Integer.valueOf(ga.size()), CoreMatchers.is(1));
        assertThat(Integer.valueOf(ga.get(0).getGameID()), CoreMatchers.is(1));
    }

    @Test
    public void testEditPatientAllGames() {
        Log.i("@Test", "testEditPatientAllGames: Selecting all games");
        selectAllGames();
        Log.i("@Test", "testEditPatientAllGames: Clicking confirm button");
        onView(withId(R.id.editPatientDetails_confirmButton)).perform(new ViewAction[]{ViewActions.click()});
        Log.i("@Test", "testEditPatientAllGames: Asserting toast message and change page");
        onView(withText(R.string.edit_patient_details_patient_updated)).inRoot(MobileViewMatchers.isToast()).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.viewPatientDetailsActivity)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.patientIDTitleTextView)).check(ViewAssertions.matches(withText("Patient: JASON93")));
        List<Patient> patients = this.db.getAllPatients();
        MatcherAssert.assertThat(Integer.valueOf(patients.size()), CoreMatchers.is(1));
        MatcherAssert.assertThat(patients.get(0).getPatientReference(), CoreMatchers.is("JASON93"));
        List<GameAssignment> ga = this.db.getAssignments(patients.get(0));
        MatcherAssert.assertThat(Integer.valueOf(ga.size()), CoreMatchers.is(4));
        TestCase.assertEquals(ga.get(0).getPatientID(), patients.get(0).getPatientID());
        TestCase.assertEquals(ga.get(0).getGameID(), 1);
        TestCase.assertEquals(ga.get(1).getPatientID(), patients.get(0).getPatientID());
        TestCase.assertEquals(ga.get(1).getGameID(), 2);
        TestCase.assertEquals(ga.get(2).getPatientID(), patients.get(0).getPatientID());
        TestCase.assertEquals(ga.get(2).getGameID(), 3);
        TestCase.assertEquals(ga.get(3).getPatientID(), patients.get(0).getPatientID());
        TestCase.assertEquals(ga.get(3).getGameID(), 4);
    }

    @Test
    public void testCreatePatientNoGames() {
        Log.i("@Test", "testCreatePatientNoGames: De-selecting all games");
        deselectAllGames();
        Log.i("@Test", "testCreatePatientNoGames: Clicking confirm button");
        onView(withId(R.id.editPatientDetails_confirmButton)).perform(new ViewAction[]{ViewActions.click()});
        Log.i("@Test", "testCreatePatientNoGames: Asserting toast message and change page");
        onView(withText(R.string.edit_patient_details_patient_updated)).inRoot(MobileViewMatchers.isToast()).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.viewPatientDetailsActivity)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.patientIDTitleTextView)).check(ViewAssertions.matches(withText("Patient: JASON93")));
        List<Patient> patients = this.db.getAllPatients();
        MatcherAssert.assertThat(Integer.valueOf(patients.size()), CoreMatchers.is(1));
        MatcherAssert.assertThat(patients.get(0).getPatientReference(), CoreMatchers.is("JASON93"));
        MatcherAssert.assertThat(Integer.valueOf(this.db.getAssignments(patients.get(0)).size()), CoreMatchers.is(0));
    }

    @Test
    public void testEditPatient11Characters() {
        Log.i("@Test", "testEditPatient11Characters: Entering 11 character patientID");
        onView(withId(R.id.editPatientDetails_inputPatientID)).perform(new ViewAction[]{ViewActions.replaceText("JasonKrieg1")});
        onView(withId(R.id.editPatientDetails_inputPatientID)).check(ViewAssertions.matches(withText("JasonKrieg1")));
        Log.i("@Test", "testEditPatient11Characters: Clicking confirm button");
        onView(withId(R.id.editPatientDetails_confirmButton)).perform(new ViewAction[]{ViewActions.click()});
        Log.i("@Test", "testEditPatient11Characters: Checking error message has been shown");
        onView(withId(R.id.editPatientDetails_inputPatientIDLayout)).check(ViewAssertions.matches(textInputLayoutErrorTextMatcher("Patient ID is too long, must be 10 characters maximum")));
        List<Patient> patients = this.db.getAllPatients();
        MatcherAssert.assertThat(Integer.valueOf(patients.size()), CoreMatchers.is(1));
        MatcherAssert.assertThat(patients.get(0).getPatientReference(), CoreMatchers.is("JASON93"));
        List<GameAssignment> ga = this.db.getAssignments(patients.get(0));
        MatcherAssert.assertThat(Integer.valueOf(ga.size()), CoreMatchers.is(1));
        MatcherAssert.assertThat(Integer.valueOf(ga.get(0).getGameID()), CoreMatchers.is(1));
    }

    @Test
    public void testChangePatientReference() {
        Log.i("@Test", "testChangePatientReference: Changing Patient ID");
        onView(withId(R.id.editPatientDetails_inputPatientID)).perform(new ViewAction[]{ViewActions.replaceText("Jasonnt")});
        onView(withId(R.id.editPatientDetails_inputPatientID)).check(ViewAssertions.matches(withText("Jasonnt")));
        Log.i("@Test", "testChangePatientReference: Clicking confirm button");
        onView(withId(R.id.editPatientDetails_confirmButton)).perform(new ViewAction[]{ViewActions.click()});
        Log.i("@Test", "testChangePatientReference: Asserting toast message and change page");
        onView(withText(R.string.edit_patient_details_patient_updated)).inRoot(MobileViewMatchers.isToast()).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.viewPatientDetailsActivity)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.patientIDTitleTextView)).check(ViewAssertions.matches(withText("Patient: JASONNT")));
        List<Patient> patients = this.db.getAllPatients();
        MatcherAssert.assertThat(Integer.valueOf(patients.size()), CoreMatchers.is(1));
        MatcherAssert.assertThat(patients.get(0).getPatientReference(), CoreMatchers.is("JASONNT"));
        List<GameAssignment> ga = this.db.getAssignments(patients.get(0));
        MatcherAssert.assertThat(Integer.valueOf(ga.size()), CoreMatchers.is(1));
        MatcherAssert.assertThat(Integer.valueOf(ga.get(0).getGameID()), CoreMatchers.is(1));
    }

    @Test
    public void testChangeEverythingPressExit() {
        Log.i("@Test", "testChangeEverythingPressExit: Changing Patient ID");
        onView(withId(R.id.editPatientDetails_inputPatientID)).perform(new ViewAction[]{ViewActions.replaceText("Jasonnt")});
        onView(withId(R.id.editPatientDetails_inputPatientID)).check(ViewAssertions.matches(withText("Jasonnt")));
        selectAllGames();
        Log.i("@Test", "testChangeEverythingPressExit: Clicking exit button");
        onView(withId(R.id.editPatientDetails_exitButton)).perform(click());
        assertTrue(rule.getActivity().isFinishing());
        List<Patient> patients = this.db.getAllPatients();
        MatcherAssert.assertThat(Integer.valueOf(patients.size()), CoreMatchers.is(1));
        MatcherAssert.assertThat(patients.get(0).getPatientReference(), CoreMatchers.is("JASON93"));
        List<GameAssignment> ga = this.db.getAssignments(patients.get(0));
        MatcherAssert.assertThat(Integer.valueOf(ga.size()), CoreMatchers.is(1));
        MatcherAssert.assertThat(Integer.valueOf(ga.get(0).getGameID()), CoreMatchers.is(1));
    }

    /**
     * Custom matcher for TextInputLayout error text
     *
     * @param expectedErrorText - String expected error text
     * @return - true if error text matches the expected error text
     */
    public static Matcher<View> textInputLayoutErrorTextMatcher(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence error = ((TextInputLayout) item).getError();

                if (error == null) {
                    return false;
                }

                String errorString = error.toString();

                return expectedErrorText.equals(errorString);
            }
        };
    }


    /**
     * Helper function to toggle a checkbox
     * - sourced from https://stackoverflow.com/questions/37819278/android-espresso-click-checkbox-if-not-checked
     *
     * @param checked
     * @return
     */
    public static ViewAction setChecked(final boolean checked) {
        return new ViewAction() {
            @Override
            public BaseMatcher<View> getConstraints() {
                return new BaseMatcher<View>() {
                    @Override
                    public boolean matches(Object item) {
                        return isA(Checkable.class).matches(item);
                    }

                    @Override
                    public void describeMismatch(Object item, Description mismatchDescription) {
                    }

                    @Override
                    public void describeTo(Description description) {
                    }
                };
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                Checkable checkableView = (Checkable) view;
                checkableView.setChecked(checked);
            }
        };
    }

    public void selectAllGames() {
        onView(withId(R.id.editPatientDetails_checkReactionTime)).perform(setChecked(true));
        onView(withId(R.id.editPatientDetails_checkVisualShortTermMemory)).perform(setChecked(true));
        onView(withId(R.id.editPatientDetails_checkSelectiveAttention)).perform(setChecked(true));
        onView(withId(R.id.editPatientDetails_checkMotorSkills)).perform(setChecked(true));
    }

    public void deselectAllGames() {
        onView(withId(R.id.editPatientDetails_checkReactionTime)).perform(setChecked(false));
        onView(withId(R.id.editPatientDetails_checkVisualShortTermMemory)).perform(setChecked(false));
        onView(withId(R.id.editPatientDetails_checkSelectiveAttention)).perform(setChecked(false));
        onView(withId(R.id.editPatientDetails_checkMotorSkills)).perform(setChecked(false));
    }
}