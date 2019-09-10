package com.soteria.neurolab;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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

    //Selects the activity to be tested
    @Rule
    public ActivityTestRule<SearchCreateDeleteActivity> rule = new ActivityTestRule(SearchCreateDeleteActivity.class);

    //Selects the create patient fragment for testing
    @Before
    public void setupFragment(){
        onView(withId(R.id.navigation_create_patient)).perform(click());
        onView(withId(R.id.createPatientFragment)).check(matches(isDisplayed()));
        Log.i("@Before", "setupFragment: Create patient fragment selected from bottom navigation menu");
    }

    //Test blank patientID
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
        onView(withId(R.id.inputPatientIDLayout)).check(matches(textInputLayoutErrorTextMatcher("PatientID cannot be blank")));
        //assert that no entry was added to the database once database is implemented
    }


    //Tests create patient with special characters
    @Test
    public void testCreatePatientWithSpecialCharacters(){
        //Given - Database stuff will go here once implemented
        Log.i("@Test", "testCreatePatientWithSpecialCharacters: Clearing the PatientID field");
        onView(withId(R.id.inputPatientID)).perform(replaceText("!@#$"));
        onView(withId(R.id.inputPatientID)).check(matches(withText("!@#$")));

        //When
        Log.i("@Test", "testCreatePatientWithSpecialCharacters: Clicking create patient button");
        onView(withId(R.id.buttonCreate)).perform(ViewActions.click());

        //Then
        Log.i("@Test", "testCreatePatientWithSpecialCharacters: Checking error message has been shown");
        onView(withId(R.id.inputPatientIDLayout)).check(matches(textInputLayoutErrorTextMatcher("PatientID contains 1 or more illegal characters")));
        //assert that no entry was added to the database once database is implemented
    }


    //Test create patient
    @Test
    public void testCreatePatient(){
        //Given - Database stuff will go here once implemented
        Log.i("@Test", "testCreatePatient: Entering valid patientID");
        String patientID = "JASON93";
        onView(withId(R.id.inputPatientID)).perform(replaceText(patientID));
        onView(withId(R.id.inputPatientID)).check(matches(withText(patientID)));

        //When
        Log.i("@Test", "testCreatePatient: Clicking create patient button");
        onView(withId(R.id.buttonCreate)).perform(ViewActions.click());

        //Then
        Log.i("@Test", "testCreatePatient: Checking snackbar message is shown");
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Patient " + patientID + " created")));
        onView(withId(com.google.android.material.R.id.snackbar_action)).check(matches(withText("Open")));
        //assert that entry was added to database
    }

    //Add test for after creating patient - open that patient page

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
}
