package com.soteria.neurolab;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.soteria.neurolab.database.DatabaseAccess;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class SearchPatientFragmentTest {

    @Rule
    public ActivityTestRule<SearchCreateDeleteActivity> rule = new ActivityTestRule<>(SearchCreateDeleteActivity.class);

    @Before
    public void setUp() throws Exception {
        onView(withId(R.id.navigation_search_patient)).perform(click());
        onView(withId(R.id.searchPatientFragment)).check(matches(isDisplayed()));
        Log.i("@Before", "setup: Search patient fragment selected");

        init();
    }

    @Test
    public void testSearchFilter() throws InterruptedException {
        //Given
        Log.i("@Test", "testSearchFilter: Checking all patient references are displayed");
        onView(withId(R.id.searchPatient_searchRecycler)).check(matches(hasChildCount(4)));

        Log.i("@Test", "testSearchFilter: Search box has been selected");
        onView(withId(R.id.searchPatient_searchInput)).perform(click());

        //When
        Log.i("@Test", "testSearchFilter: Entering a character");
        onView(withId(R.id.searchPatient_searchInput)).perform(typeText("5"));

        //Then
        Log.i("@Test", "testSearchFilter: Checking that the list has been filtered");
        onView(withId(R.id.searchPatient_searchRecycler)).check(matches(hasChildCount(2)));
    }

    @Test
    public void testFilterExcludedCharacters() {

        //Given
        Log.i("@Test", "testFilterExcludedCharacters: Initialising character to be entered");
        String character = "Q";

        //When
        Log.i("@Test", "testFilterExcludedCharacters: Entering a character");
        onView(withId(R.id.searchPatient_searchInput)).perform(typeText(character));

        //Then
        Log.i("@Test", "testFilterExcludedCharacters: Checking list contains no items");
        onView(withId(R.id.searchPatient_searchRecycler)).check(matches(hasChildCount(0)));
    }

    @Test
    public void testClickItem(){
        Log.i("@Test", "testClickItem: Clicking the first list item");
        onView(withId(R.id.searchPatient_searchRecycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Log.i("@Test", "testClickItem: Checking that the ViewPatientDetails page opens");
        intended(hasComponent(ViewPatientDetails.class.getName()));
    }

    @After
    public void tearDown() throws Exception {
        release();
    }
}