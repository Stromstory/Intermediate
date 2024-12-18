package com.dicoding.mysubmissionintermediate.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.mysubmissionintermediate.R
import com.dicoding.mysubmissionintermediate.ui.login.LoginActivity
import com.dicoding.mysubmissionintermediate.ui.upl.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestActivityLogin {

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginSuccess() {
        onView(withId(R.id.edt_email))
            .perform(typeText("valid@example.com"), closeSoftKeyboard())
        onView(withId(R.id.edt_password))
            .perform(typeText("correct_password"), closeSoftKeyboard())

        onView(withId(R.id.btn_login)).perform(click())

        onView(withText(R.string.success)).check(matches(isDisplayed()))

        onView(withText(R.string.pop_up_success)).check(matches(isDisplayed()))

        onView(withText(R.string.next)).perform(click())

        onView(withId(R.id.main)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginFailed() {
        onView(withId(R.id.edt_email))
            .perform(typeText("invalid@example.com"), closeSoftKeyboard())
        onView(withId(R.id.edt_password))
            .perform(typeText("wrong_password"), closeSoftKeyboard())

        onView(withId(R.id.btn_login)).perform(click())

        onView(withText(R.string.login_error)).check(matches(isDisplayed()))
    }
}
