package com.rittmann.passwordnotify.support

import android.app.Activity
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule

open class ActivityTest {

    private val mCountingIdlingResource = CountingIdlingResource(RESOURCE)
    protected val targetContext: Context =
        InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val rule = InstantTaskExecutorRule()

    fun increment() {
        mCountingIdlingResource.increment()
    }

    fun decrement() {
        mCountingIdlingResource.decrement()
    }

    fun getIdlingResource() = mCountingIdlingResource

    inline fun <reified A : Activity> getActivity(): ActivityScenario<A> {
        return ActivityScenario.launch(A::class.java)
    }

    companion object {
        private const val RESOURCE = "GLOBAL"
    }
}


