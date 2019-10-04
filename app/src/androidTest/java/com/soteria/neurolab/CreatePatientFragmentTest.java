package com.soteria.neurolab;

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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class CreatePatientFragmentTest {
    private DatabaseAccess db;

    //Selects the activity to be tested
    @Rule
    public ActivityTestRule<SearchCreateDeleteActivity> rule = new ActivityTestRule(SearchCreateDeleteActivity.class);

    //Selects the create patient fragment for testing
    @Before
    public void setupFragment(){
        db = new DatabaseAccess(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withId(R.id.navigation_create_patient)).perform(click());
        onView(withId(R.id.createPatientFragment)).check(matches(isDisplayed()));
        Log.i("@Before", "setupFragment: Create patient fragment selected from bottom navigation menu");
    }

    @Test
    public void testPreConditions(){
        assertNotNull(db);
    }

    //Test blank patientID
    @Test
    public void testCreatePatientBlankID(){
        db.deleteAllPatients();

        //Given
        Log.i("@Test", "testCreatePatientBlankID: Clearing the PatientID field");
        onView(withId(R.id.inputPatientID)).perform(replaceText(""));
        onView(withId(R.id.inputPatientID)).check(matches(withText("")));

        //When
        Log.i("@Test", "testCreatePatientBlankID: Clicking create patient button");
        onView(withId(R.id.buttonCreate)).perform(ViewActions.click());

        //Then
        Log.i("@Test", "testCreatePatientBlankID: Checking error message has been shown");
        onView(withId(R.id.inputPatientIDLayout)).check(matches(textInputLayoutErrorTextMatcher("PatientID cannot be blank")));
        //Check database
        List<Patient> patients = db.getAllPatients();
        assertThat(patients.size(), is(0));
    }


    //Tests create patient with special characters
    @Test
    public void testCreatePatientWithSpecialCharacters(){
        db.deleteAllPatients();

        //Given
        Log.i("@Test", "testCreatePatientWithSpecialCharacters: Clearing the PatientID field");
        onView(withId(R.id.inputPatientID)).perform(replaceText("!@#$"));
        onView(withId(R.id.inputPatientID)).check(matches(withText("!@#$")));

        //When
        Log.i("@Test", "testCreatePatientWithSpecialCharacters: Clicking create patient button");
        onView(withId(R.id.buttonCreate)).perform(ViewActions.click());

        //Then
        Log.i("@Test", "testCreatePatientWithSpecialCharacters: Checking error message has been shown");
        onView(withId(R.id.inputPatientIDLayout)).check(matches(textInputLayoutErrorTextMatcher("PatientID contains 1 or more illegal characters")));
        //Check database
        List<Patient> patients = db.getAllPatients();
        assertThat(patients.size(), is(0));
    }


    //Test create patient with all games
    @Test
    public void testCreatePatientAllGames(){
        db.deleteAllPatients();
        db.deleteAllAssignments();

        //Given
        Log.i("@Test", "testCreatePatientAllGames: Entering valid patientID");
        String patientID = "Jason93";
        onView(withId(R.id.inputPatientID)).perform(replaceText(patientID));
        onView(withId(R.id.inputPatientID)).check(matches(withText(patientID)));
        selectAllGames();

        //When
        Log.i("@Test", "testCreatePatientAllGames: Clicking create patient button");
        onView(withId(R.id.buttonCreate)).perform(ViewActions.click());

        //Then
        Log.i("@Test", "testCreatePatientAllGames: Checking snackbar message is shown");
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Patient " + patientID.toUpperCase() + " created")));
        onView(withId(com.google.android.material.R.id.snackbar_action)).check(matches(withText("Open")));
        //Check Patient table in database
        List<Patient> patients = db.getAllPatients();
        assertThat(patients.size(), is(1));
        assertEquals(patients.get(0).getPatientReference(), "JASON93");

        //Check Game_Assignments table in database
        int testPatientID = patients.get(0).getPatientID();
        List<GameAssignment> gameAssignments = db.getAssignments(patients.get(0).getPatientID());
        assertThat(gameAssignments.size(), is(4));
        assertEquals(gameAssignments.get(0).getPatientID(), testPatientID);
        assertEquals(gameAssignments.get(0).getGameID(), 1);
        assertEquals(gameAssignments.get(1).getPatientID(), testPatientID);
        assertEquals(gameAssignments.get(1).getGameID(), 2);
        assertEquals(gameAssignments.get(2).getPatientID(), testPatientID);
        assertEquals(gameAssignments.get(2).getGameID(), 3);
        assertEquals(gameAssignments.get(3).getPatientID(), testPatientID);
        assertEquals(gameAssignments.get(3).getGameID(), 4);
    }


    //Test create patient with no games
    @Test
    public void testCreatePatientNoGames(){
        db.deleteAllPatients();
        db.deleteAllAssignments();

        //Given
        Log.i("@Test", "testCreatePatientNoGames: Entering valid patientID");
        String patientID = "Jason93";
        onView(withId(R.id.inputPatientID)).perform(replaceText(patientID));
        onView(withId(R.id.inputPatientID)).check(matches(withText(patientID)));
        deselectAllGames();

        //When
        Log.i("@Test", "testCreatePatientNoGames: Clicking create patient button");
        onView(withId(R.id.buttonCreate)).perform(ViewActions.click());

        //Then
        Log.i("@Test", "testCreatePatientNoGames: Checking snackbar message is shown");
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Patient " + patientID.toUpperCase() + " created")));
        onView(withId(com.google.android.material.R.id.snackbar_action)).check(matches(withText("Open")));
        //Check Patient table in database
        List<Patient> patients = db.getAllPatients();
        assertThat(patients.size(), is(1));
        assertEquals(patients.get(0).getPatientReference(), "JASON93");

        //Check Game_Assignments table in database
        List<GameAssignment> gameAssignments = db.getAssignments(patients.get(0).getPatientID());
        assertThat(gameAssignments.size(), is(0));
    }


    //Test create patient with 11 characters patient ID
    @Test
    public void testCreatePatient11Characters(){
        db.deleteAllPatients();

        //Given
        Log.i("@Test", "testCreatePatient11Characters: Entering 11 character patientID");
        String patientID = "JasonKrieg1";
        onView(withId(R.id.inputPatientID)).perform(replaceText(patientID));
        onView(withId(R.id.inputPatientID)).check(matches(withText(patientID)));

        //When
        Log.i("@Test", "testCreatePatient11Characters: Clicking create patient button");
        onView(withId(R.id.buttonCreate)).perform(ViewActions.click());

        //Then
        Log.i("@Test", "testCreatePatient11Characters: Checking error message has been shown");
        onView(withId(R.id.inputPatientIDLayout)).check(matches(textInputLayoutErrorTextMatcher("Patient ID is too long, must be 10 characters maximum")));
        //Check database
        List<Patient> patients = db.getAllPatients();
        assertThat(patients.size(), is(0));
    }


    //Tests that the open button on the snackbar (when creating a new patient) will take you to the
    //correct page.
    @Test
    public void testSnackbarOpenButton() {
        db.deleteAllPatients();
        db.deleteAllAssignments();

        //Given
        Log.i("@Test", "testSnackbarOpenButton: Entering valid patientID");
        String patientID = "Jason93";
        onView(withId(R.id.inputPatientID)).perform(replaceText(patientID));
        onView(withId(R.id.inputPatientID)).check(matches(withText(patientID)));
        selectAllGames();

        //When
        Log.i("@Test", "testSnackbarOpenButton: Clicking create patient button");
        onView(withId(R.id.buttonCreate)).perform(ViewActions.click());

        //Then
        Log.i("@Test", "testSnackbarOpenButton: Checking snackbar message is shown");
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Patient " + patientID.toUpperCase() + " created")));
        onView(withId(com.google.android.material.R.id.snackbar_action)).check(matches(withText("Open"))).perform(click());
        onView(withId(R.id.viewPatientDetailsActivity)).check(matches(isDisplayed()));
    }


    /**
     * Custom matcher for TextInputLayout error text
     * @param expectedErrorText - String expected error text
     * @return - true if error text matches the expected error text
     */
    public static Matcher<View> textInputLayoutErrorTextMatcher(final String expectedErrorText){
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(View item) {
                if(!(item instanceof TextInputLayout)){
                    return false;
                }

                CharSequence error = ((TextInputLayout) item).getError();

                if(error == null){
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
                    public void describeMismatch(Object item, Description mismatchDescription) {}

                    @Override
                    public void describeTo(Description description) {}
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


    /**
     * Helper function for selecting all games
     */
    public void selectAllGames(){
        onView(withId(R.id.checkReactionTime)).perform(setChecked(true));
        onView(withId(R.id.checkMotorSkills)).perform(setChecked(true));
        onView(withId(R.id.checkSelectiveAttention)).perform(setChecked(true));
        onView(withId(R.id.checkVisualShortTermMemory)).perform(setChecked(true));
    }


    /**
     * Helper function for de-selecting all games
     */
    public void deselectAllGames(){
        onView(withId(R.id.checkReactionTime)).perform(setChecked(false));
        onView(withId(R.id.checkMotorSkills)).perform(setChecked(false));
        onView(withId(R.id.checkSelectiveAttention)).perform(setChecked(false));
        onView(withId(R.id.checkVisualShortTermMemory)).perform(setChecked(false));
    }
}
