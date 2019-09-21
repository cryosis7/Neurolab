package com.soteria.neurolab;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameAssignment;
import com.soteria.neurolab.models.Patient;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class DeletePatientFragmentTest {
    private DatabaseAccess db;

    //Selects the activity to be tested
    @Rule
    public ActivityTestRule<SearchCreateDeleteActivity> rule = new ActivityTestRule(SearchCreateDeleteActivity.class);

    //Selects the delete patient fragment for testing, also empties the database except for games and adds in test data
    @Before
    public void setupFragment(){
        //Setup test data for all tests
        db = new DatabaseAccess(InstrumentationRegistry.getInstrumentation().getTargetContext());
        db.deleteAllPatients();
        db.deleteAllSessions();
        db.deleteAllAssignments();
        createTestPatients();

        //set fragment
        onView(withId(R.id.navigation_delete_patient)).perform(click());
        onView(withId(R.id.deletePatientFragment)).check(matches(isDisplayed()));
        Log.i("@Before", "setupFragment: Delete patient fragment selected from bottom navigation menu");
    }


    @Test
    public void testPreConditions(){
        assertNotNull(db);
    }


    /**
     * Test that the UI loads correctly - should have 4 items in the recycler as they are added in @before
     */
    @Test
    public void testLoadUI(){
        onView(withId(R.id.deletePatient_searchInput)).check(matches(allOf(
                isDisplayed()
        )));

        onView(withId(R.id.deletePatient_selectAllCheckBox)).check(matches(allOf(
                isDisplayed()
        )));

        onView(withId(R.id.deletePatient_searchRecycler)).check(matches(allOf(
                isDisplayed(), hasChildCount(4)
        )));

        onView(withId(R.id.deletePatient_deleteButton)).check(matches(allOf(
                isDisplayed()
        )));
    }


    @Test
    public void testSearchFilter(){
        onView(withId(R.id.deletePatient_searchRecycler)).check(matches(allOf(
                isDisplayed(), hasChildCount(4)
        )));

        Log.i("@Test", "testSearchFilter: Search box has been selected");
        onView(withId(R.id.deletePatient_searchInput)).perform(click());

        //When
        Log.i("@Test", "testSearchFilter: Entering a character");
        onView(withId(R.id.deletePatient_searchInput)).perform(typeText("0"));

        //Then
        Log.i("@Test", "testSearchFilter: Checking that the list has been filtered");
        onView(withId(R.id.deletePatient_searchRecycler)).check(matches(hasChildCount(3)));
    }


    /**
     * Test that the list resets to all after clearing the search text
     */
    @Test
    public void testClearSearchFilter(){
        onView(withId(R.id.deletePatient_searchRecycler)).check(matches(allOf(
                isDisplayed(), hasChildCount(4)
        )));

        Log.i("@Test", "testSearchFilter: Search box has been selected");
        onView(withId(R.id.deletePatient_searchInput)).perform(click());

        Log.i("@Test", "testSearchFilter: Entering a character");
        onView(withId(R.id.deletePatient_searchInput)).perform(typeText("0"));

        Log.i("@Test", "testSearchFilter: Checking that the list has been filtered");
        onView(withId(R.id.deletePatient_searchRecycler)).check(matches(hasChildCount(3)));

        onView(withId(R.id.deletePatient_searchInput)).perform(pressKey(KeyEvent.KEYCODE_DEL));

        onView(withId(R.id.deletePatient_searchRecycler)).check(matches(allOf(
                isDisplayed(), hasChildCount(4)
        )));
    }


    @Test
    public void testDeleteNoPatients(){
        onView(withId(R.id.deletePatient_deleteButton)).perform(click());

        onView(withText(R.string.delete_patient_no_patients_selected)).inRoot(MobileViewMatchers.isToast()).check(matches(isDisplayed()));

        onView(withId(R.id.deletePatient_searchRecycler)).check(matches(allOf(
                isDisplayed(), hasChildCount(4)
        )));
    }


    @Test
    public void testDeleteAllPatients(){
        onView(withId(R.id.deletePatient_searchRecycler)).check(matches(allOf(
                isDisplayed(), hasChildCount(4)
        )));

        onView(withId(R.id.deletePatient_selectAllCheckBox)).perform(click());

        onView(withId(R.id.deletePatient_deleteButton)).perform(click());

        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.deletePatient_searchRecycler)).check(matches(allOf(
                isDisplayed(), hasChildCount(0)
        )));

        //Check Patient table in database
        List<Patient> patients = db.getAllPatients();
        assertThat(patients.size(), is(0));
    }


    @Test
    public void testDeleteOnePatient(){
        onView(withId(R.id.deletePatient_searchRecycler)).check(matches(allOf(
                isDisplayed(), hasChildCount(4)
        )));

        onView(withId(R.id.deletePatient_searchRecycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        MyViewAction.clickChildViewWithId(R.id.delete_patient_recycler_checkbox)));

        onView(withId(R.id.deletePatient_deleteButton)).perform(click());

        onView(withText(R.string.delete_patient_confirmation_dialog_1_selected)).check(matches(allOf
                (withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), withText(R.string.delete_patient_confirmation_dialog_1_selected))));

        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.deletePatient_searchRecycler)).check(matches(allOf(
                isDisplayed(), hasChildCount(3)
        )));

        //Check Patient table in database
        List<Patient> patients = db.getAllPatients();
        assertThat(patients.size(), is(3));
    }


    public void createTestPatients(){
        db.createPatient("JK93");
        db.createPatient("RD01");
        db.createPatient("SC02");
        db.createPatient("BW03");
    }


    /**
     * Helper class for testing elements inside a recycler view
     */
    public static class MyViewAction {

        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }
    }
}
