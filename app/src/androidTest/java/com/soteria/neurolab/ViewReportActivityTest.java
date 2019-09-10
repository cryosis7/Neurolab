package com.soteria.neurolab;


import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.soteria.neurolab.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ViewReportActivityTest {

    @Rule
    public ActivityTestRule<TempLauncherActivity> mActivityTestRule = new ActivityTestRule<>(TempLauncherActivity.class);

    @Test
    public void viewReportActivityTest() {
        ViewInteraction appCompatButton = onView(
allOf(withId(R.id.viewReportBtn), withText("View Report Page"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
4),
isDisplayed()));
        appCompatButton.perform(click());
        
        ViewInteraction button = onView(
allOf(withId(R.id.view_report_all_button),
childAtPosition(
allOf(withId(R.id.button_container),
childAtPosition(
IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
3)),
0),
isDisplayed()));
        button.check(matches(isDisplayed()));
        
        ViewInteraction button2 = onView(
allOf(withId(R.id.view_report_month_button),
childAtPosition(
allOf(withId(R.id.button_container),
childAtPosition(
IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
3)),
1),
isDisplayed()));
        button2.check(matches(isDisplayed()));
        
        ViewInteraction button3 = onView(
allOf(withId(R.id.view_report_week_button),
childAtPosition(
allOf(withId(R.id.button_container),
childAtPosition(
IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
3)),
2),
isDisplayed()));
        button3.check(matches(isDisplayed()));
        
        ViewInteraction textView = onView(
allOf(withId(R.id.view_report_text_patient_id), withText("Patient: SC05"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
0),
isDisplayed()));
        textView.check(matches(withText("Patient: SC05")));
        
        ViewInteraction textView2 = onView(
allOf(withText("Reaction Time"),
childAtPosition(
allOf(withId(R.id.view_report_game_list_spinner),
childAtPosition(
IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
2)),
0),
isDisplayed()));
        textView2.check(matches(withText("Reaction Time")));
        
        ViewInteraction appCompatSpinner = onView(
allOf(withId(R.id.view_report_game_list_spinner),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
3),
isDisplayed()));
        appCompatSpinner.perform(click());
        
        DataInteraction appCompatCheckedTextView = onData(anything())
.inAdapterView(childAtPosition(
withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
0))
.atPosition(1);
        appCompatCheckedTextView.perform(click());
        
        ViewInteraction textView3 = onView(
allOf(withText("Short-term Visual Memory"),
childAtPosition(
allOf(withId(R.id.view_report_game_list_spinner),
childAtPosition(
IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
2)),
0),
isDisplayed()));
        textView3.check(matches(withText("Short-term Visual Memory")));
        
        ViewInteraction appCompatSpinner2 = onView(
allOf(withId(R.id.view_report_game_list_spinner),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
3),
isDisplayed()));
        appCompatSpinner2.perform(click());
        
        DataInteraction appCompatCheckedTextView2 = onData(anything())
.inAdapterView(childAtPosition(
withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
0))
.atPosition(0);
        appCompatCheckedTextView2.perform(click());
        
        ViewInteraction viewGroup = onView(
allOf(withId(R.id.view_report_report_chart),
childAtPosition(
allOf(withId(R.id.view_report_report_container),
childAtPosition(
IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
0)),
1),
isDisplayed()));
        viewGroup.check(matches(isDisplayed()));
        }
    
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup)parent).getChildAt(position));
            }
        };
    }
    }
