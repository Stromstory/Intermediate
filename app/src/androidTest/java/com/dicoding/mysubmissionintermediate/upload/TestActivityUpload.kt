package com.dicoding.mysubmissionintermediate.upload

import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.mysubmissionintermediate.R
import com.dicoding.mysubmissionintermediate.ui.main.MainActivity
import com.dicoding.mysubmissionintermediate.ui.upl.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestActivityUpload {
    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun addStory_Success() {
        onView(withId(R.id.fab_add)).perform(click())
        onView(withId(R.id.btn_Gallery)).perform(click())
        onView(withId(R.id.ed_add_description)).perform(click())
        onView(isAssignableFrom(EditText::class.java)).perform(typeText("Description"))
        onView(withId(R.id.button_add)).perform(click())
    }
}
