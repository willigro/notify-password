package com.rittmann.passwordnotify.support

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.rittmann.passwordnotify.support.recyclerview.TestUtils.withRecyclerView
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsString


object ExpressoUtil {

    fun checkValue(id: Int, value: String, withScroll: Boolean = false) {
        onView(allOf(withId(id), isDisplayed())).apply {
            if (withScroll)
                perform(scrollTo())
        }.check(matches(withText(containsString(value))))
    }

    fun checkValueError(id: Int, value: String, withScroll: Boolean = false) {
        onView(withId(id)).check(matches(hasErrorText(value)))
    }

    fun checkValueRecycler(recyclerId: Int, targetId: Int, position: Int, value: String) {
        onView(withRecyclerView(recyclerId).atPositionOnView(position, targetId)).check(
            matches(withText(value))
        )
    }

    fun viewIsChecked(id: Int) {
        onView(withId(id)).check(matches(isChecked()))
    }

    fun viewIsNotChecked(id: Int) {
        onView(withId(id)).check(matches(not(isChecked())))
    }

    fun performClick(id: Int, withScroll: Boolean = false) {
        onView(withId(id)).apply {
            if (withScroll)
                perform(scrollTo())
            perform(click())
        }
    }

    fun performClickRecycler(recyclerId: Int, position: Int) {
        onView(withRecyclerView(recyclerId).atPosition(position)).perform(click())
    }

    fun putValue(id: Int, value: String, withScroll: Boolean = false) {
        onView(withId(id)).apply {
            if (withScroll)
                perform(scrollTo())
            perform(replaceText(value), closeSoftKeyboard())
        }
    }

    fun putValueTextView(id: Int, value: String) {
        onView(withId(id)).perform(setTextInTextView(value))
    }

    fun setTextInTextView(value: String?): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(
                    isDisplayed(), isAssignableFrom(
                        TextView::class.java
                    )
                )
            }

            override fun perform(uiController: UiController, view: View) {
                (view as TextView).text = value
            }

            override fun getDescription(): String {
                return "replace text"
            }
        }
    }


    fun viewIsDisplayed(id: Int, withScroll: Boolean = false) {
        onView(withId(id)).apply {
            if (withScroll)
                perform(scrollTo())
        }.check(matches(isDisplayed()))
    }

    fun viewNotIsDisplayed(id: Int, withScroll: Boolean = false) {
        onView(withId(id)).apply {
            if (withScroll)
                perform(scrollTo())
        }.check(matches(not(isDisplayed())))
    }

    fun viewDoesNotExists(value: String) {
        onView(withText(value)).check(doesNotExist())
    }

    fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun perform(uiController: UiController?, view: View?) {
                uiController?.loopMainThreadForAtLeast(delay)
            }

            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for " + delay + "milliseconds"
            }
        }
    }

    fun checkToast(value: String) {
        onView(withText(value)).inRoot(
            RootMatchers.withDecorView(
                Matchers.not(
                    getCurrentActivity()!!.window.decorView
                )
            )
        ).check(
            matches(
                isDisplayed()
            )
        )
    }

    fun scrollToBottom(resId: Int) {
        onView(withId(resId)).perform(ScrollToBottomAction())
    }

    class ScrollToBottomAction : ViewAction {
        override fun getDescription(): String {
            return "scroll RecyclerView to bottom"
        }

        override fun getConstraints(): Matcher<View> {
            return allOf<View>(isAssignableFrom(RecyclerView::class.java), isDisplayed())
        }

        override fun perform(uiController: UiController?, view: View?) {
            val recyclerView = view as RecyclerView
            val itemCount = recyclerView.adapter?.itemCount
            val position = itemCount?.minus(1) ?: 0
            recyclerView.scrollToPosition(position)
            uiController?.loopMainThreadUntilIdle()
        }
    }

    class ExecuteOn(private val callTime: Int) {
        var current = 1

        fun next(callback: () -> Unit) {
            if (current == callTime)
                callback()
            current++
        }
    }

    @Throws(Throwable::class)
    fun getCurrentActivity(): Activity? {
        getInstrumentation().waitForIdleSync()
        val activity = arrayOfNulls<Activity>(1)
        onView(isRoot()).check { _, _ ->
            val activities =
                ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            activity[0] = Iterables.getOnlyElement(activities)
        }
        return activity[0]
    }
}