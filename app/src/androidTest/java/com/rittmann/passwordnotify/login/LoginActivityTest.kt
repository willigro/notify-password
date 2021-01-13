package com.rittmann.passwordnotify.login

import androidx.test.core.app.ActivityScenario
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.dao.room.config.AppDatabase
import com.rittmann.passwordnotify.support.ActivityTest
import com.rittmann.passwordnotify.support.ExpressoUtil.checkValueError
import com.rittmann.passwordnotify.support.ExpressoUtil.performClick
import com.rittmann.passwordnotify.support.ExpressoUtil.putValue
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

    @Test
    fun showPasswordErrorWhenItIsNotInformed() {
        deleteAllLogin()

        scenario = ActivityScenario.launch(LoginActivity::class.java)

        viewIsDisplayed(R.id.edtPasswordConfirmation)
        viewIsDisplayed(R.id.labelLoginConfirmation)

        performClick(R.id.btnDoLogin)

        checkValueError(R.id.edtPassword, context.getString(R.string.error_password_not_found))
        checkValueError(
            R.id.edtPasswordConfirmation,
            context.getString(R.string.error_confirmation_not_found)
        )
    }

    @Test
    fun showDoesNotMatchWhenConfirmationAndPasswordNotAreTheSame() {
        deleteAllLogin()

        scenario = ActivityScenario.launch(LoginActivity::class.java)

        viewIsDisplayed(R.id.edtPasswordConfirmation)
        viewIsDisplayed(R.id.labelLoginConfirmation)

        putValue(R.id.edtPassword, "Password")
        putValue(R.id.edtPasswordConfirmation, "Confirmation")

        performClick(R.id.btnDoLogin)

        checkValueError(
            R.id.edtPasswordConfirmation,
            context.getString(R.string.error_confirmation_not_match)
        )
    }

    private fun deleteAllLogin() {
        AppDatabase.getDatabase(context)?.loginDao()?.deleteAll()
    }
}