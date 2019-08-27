package com.soteria.neurolab;

import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class CreatePatientFragmentTest {

    @Rule
    public ActivityTestRule<SearchCreateDeleteActivity> rule = new ActivityTestRule(SearchCreateDeleteActivity.class);

    @Before
    public void setupFragment(){
        onView(withId(R.id.navigation_create_patient)).perform(click());
        onView(withId(R.id.createPatientFragment)).check(matches(isDisplayed()));
        Log.i("@Before", "setupFragment: Create patient fragment selected from bottom navigation menu");
    }

    @Test
    public void testCreatePatientBlankID(){
        //Given - Database stuff will go here once implemented
        Log.i("@Test", "testCreatePatientBlankID: Clearing the PatientID field");
        onView(withId(R.id.inputPatientID)).perform(replaceText(""));
        onView(withId(R.id.inputPatientID)).check(matches(withText("")));

        //When
        Log.i("@Test", "testCreatePatientBlankID: Clicking create patient button");
        onView(withId(R.id.buttonCreate)).perform(ViewActions.click());

        //Then
        Log.i("@Test", "testCreatePatientBlankID: Checking error message has been shown");
        onView(withId(R.id.inputPatientID)).check(matches(hasErrorText("PatientID cannot be blank")));
        //assert that no entry was added to the database once database is implemented
    }

}
