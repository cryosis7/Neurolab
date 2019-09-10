package com.soteria.neurolab;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Checks;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasBackground;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ViewReportActivityTest {

    @Rule
    public ActivityTestRule<ViewReportActivity> rule = new ActivityTestRule<>(ViewReportActivity.class, false, false);

    @Before
    public void setIntent(){
        Log.i("@Test", "--- --- --- --- Setting intents --- --- --- ---");
        Intent testIntent = new Intent();
        testIntent.putExtra( "PATIENT_REFERENCE", "SC05" );
        rule.launchActivity(testIntent);
    }

    /**
     * Checking that all elements load as they are meant to.
     */
    @Test
    public void testLayoutOnStartup() {
        onView(withId(R.id.view_report_all_button)).check(matches(allOf(
                isDisplayed(),
                withText(R.string.view_report_all)
        )));

        onView(withId(R.id.view_report_month_button)).check(matches(allOf(
                isDisplayed(),
                withText(R.string.view_report_month)
        )));

        onView(withId(R.id.view_report_week_button)).check(matches(allOf(
                isDisplayed(),
                withText(R.string.view_report_week)
        )));

        onView(withId(R.id.view_report_text_patient_id)).check(matches(allOf(
                isDisplayed(),
                withText("Patient: SC05")
        )));

        onView(withId(R.id.view_report_report_chart)).check(matches(isDisplayed()));
        onView(withId(R.id.view_report_game_list_spinner)).check(matches(withSpinnerText("Reaction Time")));
    }

    @Test
    public void testSelectGameSpinnerItem() {
        onView(withId(R.id.view_report_game_list_spinner)).check(matches(withSpinnerText("Reaction Time")));
        onView(withId(R.id.view_report_game_list_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)))).atPosition(1).perform(click());
        onView(withId(R.id.view_report_game_list_spinner)).check(matches(withSpinnerText("Short-term Visual Memory")));
    }

    @Test
    public void testTapButton() {
        final ViewInteraction allButton = onView(withId(R.id.view_report_all_button));
        while(ActivityTestRule.getActivity())
        allButton.check(matches(hasBackground(R.drawable.button_selector_pressed)));
    }
}
