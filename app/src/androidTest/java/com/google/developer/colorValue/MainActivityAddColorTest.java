package com.google.developer.colorValue;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.developer.colorvalue.MainActivity;
import com.google.developer.colorvalue.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by clinton on 3/18/18.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityAddColorTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void onFloatingAddButtonClick_OpensAddActivity(){

        onView((withId(R.id.fab))).perform(click());

        onView((withId(R.id.button_add))).check(matches(ViewMatchers.withText("Add Color")));

    }
}
