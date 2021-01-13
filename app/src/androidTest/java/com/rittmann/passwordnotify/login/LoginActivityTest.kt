package com.rittmann.passwordnotify.login

import androidx.test.core.app.ActivityScenario
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.support.ActivityTest
import com.rittmann.passwordnotify.support.ExpressoUtil.viewIsDisplayed
import com.rittmann.passwordnotify.ui.login.LoginActivity
import org.junit.After
import org.junit.Assert.fail
import org.junit.Test

class LoginActivityTest : ActivityTest() {
    private var scenario: ActivityScenario<LoginActivity>? = null

    @After
    fun cleanUp() {
        scenario?.close()
    }

    @Test
    fun showConfirmationFieldWhenThereIsNoRegisteredPassword() {
        scenario = ActivityScenario.launch(LoginActivity::class.java)

        scenario?.onActivity {

            it.viewModel.apply {
                hasLoginRegistered.observeForever {
                    fail()
                }

                hasLoginRegistered()
            }
        }

        viewIsDisplayed(R.id.edtPasswordConfirmation)
        viewIsDisplayed(R.id.labelLoginConfirmation)
    }
}