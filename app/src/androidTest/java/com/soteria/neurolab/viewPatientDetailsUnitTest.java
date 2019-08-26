package com.soteria.neurolab;

import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.core.AllOf.allOf;


@RunWith(AndroidJUnit4.class)
public class viewPatientDetailsUnitTest
{
    /**Establishes the rule for activities*/
    @Rule
    public ActivityTestRule<viewPatientDetails> activityRule = new ActivityTestRule<>(viewPatientDetails.class);

    /**Establishes the rule for intents*/
    @Rule
    public IntentsTestRule<viewPatientDetails> intentsRule = new IntentsTestRule<>(viewPatientDetails.class);

    /**Test to determine whether the delete patient button works as intended by displaying an alert
     * dialog. This test cancels the action. */
    @Test
    public void deletePatientCancelTest()
    {
        String myDialogText="You are about to delete this patient and all data associated with them.";
        Log.i("@Test", " --- --- --- --- Performing Test - Delete Button --- --- --- ---");
        Espresso.onView(withId(R.id.deletePatientButton)).perform(click()).check(matches(allOf(withText(myDialogText), isDisplayed())));
        Espresso.onView(withId(android.R.id.button2)).perform(click());
    }

    /**Test to determine whether the delete patient button works as intended by displaying an alert
     * dialog. This test confirms the deletion of a patient. */
    /*   TODO replace class in the intended function to its correct one once create patient class is created
    @Test
    public void deletePatientConfirmTest()
    {
        String myDialogText="You are about to delete this patient and all data associated with them.";
        Log.i("@Test", " --- --- --- --- Performing Test - Delete Button --- --- --- ---");
        Espresso.onView(withId(R.id.deletePatientButton)).perform(click()).check(matches(allOf(withText(myDialogText), isDisplayed())));
        Espresso.onView(withId(android.R.id.button1)).perform(click());
        intended(hasComponent(TODO Create Patient class name .class.getName()));
        Intents.release();
    }   */

    /**Test to determine whether the view report button works as intended by linking the user to the
     * manage patient page
       TODO replace class in the intended function to its correct one once manage patient class is created */
    /*
    @Test
    public void transferToManagePatientTest()
    {
        Log.i("@Test", " --- --- --- --- Performing Test - Manage Patient Button --- --- --- ---");
        Espresso.onView(withId(R.id.managePatientButton)).perform(click());
        intended(hasComponent(TODO Manage patient class name .class.getName()));
        Intents.release();
    } */

    /**Test to determine whether the view report button works as intended by linking the user to the
     * view report page
       TODO replace class in the intended function to its correct one once patient profile class is created */
    /*
    @Test
    public void transferToViewReportTest()
    {
        Log.i("@Test", " --- --- --- --- Performing Test - View Report Button --- --- --- ---");
        Espresso.onView(withId(R.id.viewReportButton)).perform(click());
        intended(hasComponent(TODO View Report class name .class.getName()));
        Intents.release();
    } */

    /**Test to determine whether the run games button works as intended by linking the user to the
     * patient profile page
       TODO replace class in the intended function to its correct one once patient profile class is created */
    /*
    @Test
    public void transferToPatientProfileTest()
    {
        Log.i("@Test", " --- --- --- --- Performing Test - Run Games Button --- --- --- ---");
        Espresso.onView(withId(R.id.runGamesButton)).perform(click());
        intended(hasComponent(TODO Patient Profile class name .class.getName()));
        Intents.release();
    } */

    /**Test to determine whether the log out button in the overflow menu works as intended by linking
     * the user to the log in page
       TODO replace class in the intended function to its correct one once log in class is created */
    /*
    @Test
    public void transferToLogInScreenTest()
    {
        Log.i("@Test", " --- --- --- --- Performing Test - Overflow Menu Log Out --- --- --- ---");
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Espresso.onView(withId(R.id.action_logout)).perform(click());
        intended(hasComponent(TODO Log In class name .class.getName()));
        Intents.release();
    } */

    /**Test to determine whether the disclaimer button in the overflow menu works as intended by
     * displaying the disclaimer in an alert dialog. Also tests the continue button inside the dialog*/
    @Test
    public void showDisclaimerTest()
    {
        String disclaimerText = "Neurolab by Soteria is designed as an assistant tool only.";
        Log.i("@Test", " --- --- --- --- Performing Test - Overflow Menu Disclaimer --- --- --- ---");
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Espresso.onView(withId(R.id.action_disclaimer)).perform(click()).check(matches(allOf(withText(disclaimerText), isDisplayed())));
        Espresso.onView(withId(android.R.id.button1)).perform(click());
    }
}
