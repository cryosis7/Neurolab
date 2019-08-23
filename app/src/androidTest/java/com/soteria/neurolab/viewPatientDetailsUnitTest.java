package com.soteria.neurolab;

import android.util.Log;

import androidx.test.espresso.Espresso;
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
import static org.hamcrest.core.AllOf.allOf;


@RunWith(AndroidJUnit4.class)
public class viewPatientDetailsUnitTest
{
    @Rule
    public ActivityTestRule<viewPatientDetails> rule = new ActivityTestRule<>(viewPatientDetails.class);

    @Test
    public void deleteButtonTest()
    {
        String myDialogText="You are about to delete this patient and all data associated with them.";
        Log.i("@Test", " --- --- --- --- Performing Test - Delete Button --- --- --- ---");

        Espresso.onView(withId(R.id.deletePatientButton)).perform(click()).check(matches(allOf(withText(myDialogText), isDisplayed())));
    }
}
