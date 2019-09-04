package com.soteria.neurolab;

import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

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
public class ViewReportTest {
    //Selects the activity to be tested
    @Rule
    public ActivityTestRule<ViewReportActivity> rule = new ActivityTestRule<>(ViewReportActivity.class, false, false);

    @Before
    public void setIntent(){
        Log.i("@Test", "--- --- --- --- Setting intents --- --- --- ---");
        Intent testIntent = new Intent();
        testIntent.putExtra( "PATIENT_REFERENCE", "SC05" );
        rule.launchActivity(testIntent);
    }

    @Test
    public void intentTest(){
        Log.i("@Test", "--- --- --- --- Intent Test - Correct Info --- --- --- ---");
        String testStringID = "Patient: SC05";

        onView(withId(R.id.view_report_text_patient_id)).check(matches(allOf
                (withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                        withText(testStringID))));
    }

    @Test
    public void gameListSpinnerTest(){
        Log.i("@Test", "--- --- --- --- Game List Spinner Test --- --- --- ---");

    }


}
