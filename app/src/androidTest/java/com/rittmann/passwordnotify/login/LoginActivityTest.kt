package com.rittmann.passwordnotify.login

import androidx.test.core.app.ActivityScenario
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.Login
import com.rittmann.passwordnotify.data.dao.room.config.AppDatabase
import com.rittmann.passwordnotify.support.ActivityTest
import com.rittmann.passwordnotify.support.ExpressoUtil
import com.rittmann.passwordnotify.support.ExpressoUtil.checkValueError
import com.rittmann.passwordnotify.support.ExpressoUtil.performClick
import com.rittmann.passwordnotify.support.ExpressoUtil.putValue
import com.rittmann.passwordnotify.support.ExpressoUtil.viewIsDisplayed
import com.rittmann.passwordnotify.support.ExpressoUtil.viewNotIsDisplayed
import com.rittmann.passwordnotify.ui.listpasswords.ListPasswordsActivity
import com.rittmann.passwordnotify.ui.login.LoginActivity
import org.junit.After
import org.junit.Assert
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

    @Test
    fun showListPasswordWhenPasswordIsRegistered() {
        deleteAllLogin()

        scenario = ActivityScenario.launch(LoginActivity::class.java).onActivity {
            it.viewModel.apply {
                passwordNotFound.observeForever {
                    fail()
                }

                passwordConfirmationNotFound.observeForever {
                    fail()
                }

                passwordDoesNotMatchWithConfirmation.observeForever {
                    fail()
                }

                passwordNotRegistered.observeForever {
                    fail()
                }
            }
        }

        viewIsDisplayed(R.id.edtPasswordConfirmation)
        viewIsDisplayed(R.id.labelLoginConfirmation)

        putValue(R.id.edtPassword, "Password")
        putValue(R.id.edtPasswordConfirmation, "Password")

        performClick(R.id.btnDoLogin)

        Assert.assertEquals(true, ExpressoUtil.getCurrentActivity() is ListPasswordsActivity)
    }

    @Test
    fun showListPasswordWhenPasswordIsMatchWithRegisteredPassword() {
        val password = "Pass"
        deleteAllLogin()
        mockLogin(password)

        scenario = ActivityScenario.launch(LoginActivity::class.java).onActivity {
            it.viewModel.apply {
                passwordNotFound.observeForever {
                    fail()
                }

                passwordConfirmationNotFound.observeForever {
                    fail()
                }

                passwordDoesNotMatchWithConfirmation.observeForever {
                    fail()
                }

                passwordRegistered.observeForever {
                    fail()
                }

                passwordNotRegistered.observeForever {
                    fail()
                }

                passwordIsNotValid.observeForever {
                    fail()
                }
            }
        }

        viewNotIsDisplayed(R.id.edtPasswordConfirmation)
        viewNotIsDisplayed(R.id.labelLoginConfirmation)

        putValue(R.id.edtPassword, password)

        performClick(R.id.btnDoLogin)

        Assert.assertEquals(true, ExpressoUtil.getCurrentActivity() is ListPasswordsActivity)
    }

    private fun deleteAllLogin() {
        AppDatabase.getDatabase(context)?.loginDao()?.deleteAll()
    }

    private fun mockLogin(password: String) {
        AppDatabase.getDatabase(context)?.loginDao()?.insert(Login(password = password))
    }
}