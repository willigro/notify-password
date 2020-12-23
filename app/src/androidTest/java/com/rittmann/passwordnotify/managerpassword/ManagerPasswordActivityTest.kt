package com.rittmann.passwordnotify.managerpassword

import androidx.test.core.app.ActivityScenario
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.support.ExpressoUtil
import com.rittmann.passwordnotify.support.ExpressoUtil.checkValue
import com.rittmann.passwordnotify.support.ExpressoUtil.checkValueError
import com.rittmann.passwordnotify.support.ExpressoUtil.performClick
import com.rittmann.passwordnotify.support.ExpressoUtil.putValue
import com.rittmann.passwordnotify.support.PasswordSupportTest
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.fail
import org.junit.Test

class ManagerPasswordActivityTest : PasswordSupportTest() {

    private var scenario: ActivityScenario<ManagerPasswordActivity>? = null

    @After
    fun cleanUp() {
        scenario?.close()
    }

    @Test
    fun checkStartShowingNumbersAndNumberRequired() {
        val name = "JustNumbers"
        val length = "3"

        val manager = ManagerPassword(
            0L,
            name,
            length,
            numbers = Pair(first = true, second = true),
            upperCase = Pair(first = false, second = false),
            lowerCase = Pair(first = false, second = false),
            accents = Pair(first = false, second = false),
            special = Pair(first = false, second = false)
        )

        scenario = ActivityScenario.launch(
            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                context,
                manager
            )
        )

        checkValue(R.id.edtName, name)
        checkValue(R.id.edtLength, length)
    }

    @Test
    fun showErrorOnNameWhenItWasEmpty() {
        val name = "JustNumbers"
        val length = "3"

        val manager = ManagerPassword(
            0L,
            name,
            length,
            numbers = Pair(first = true, second = true),
            upperCase = Pair(first = false, second = false),
            lowerCase = Pair(first = false, second = false),
            accents = Pair(first = false, second = false),
            special = Pair(first = false, second = false)
        )

        scenario = ActivityScenario.launch(
            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                context,
                manager
            )
        )

        putValue(R.id.edtName, "")

        performClick(R.id.btnUpdaterManager)

        checkValueError(R.id.edtName, context.getString(R.string.message_invalid_name))
    }

    @Test
    fun showErrorOnLengthWhenItWasEmpty() {
        val name = "JustNumbers"
        val length = "3"

        val manager = ManagerPassword(
            0L,
            name,
            length,
            numbers = Pair(first = true, second = true),
            upperCase = Pair(first = false, second = false),
            lowerCase = Pair(first = false, second = false),
            accents = Pair(first = false, second = false),
            special = Pair(first = false, second = false)
        )

        scenario = ActivityScenario.launch(
            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                context,
                manager
            )
        )

        putValue(R.id.edtLength, "")

        performClick(R.id.btnUpdaterManager)

        checkValueError(R.id.edtLength, context.getString(R.string.message_invalid_length))
    }

    @Test
    fun showErrorOnLengthWhenWasZero() {
        val manager = mockManager()

        scenario = ActivityScenario.launch(
            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                context,
                manager
            )
        )

        putValue(R.id.edtLength, "0")

        performClick(R.id.btnUpdaterManager)

        checkValueError(R.id.edtLength, context.getString(R.string.message_invalid_length))
    }

    @Test
    fun showErrorOnLengthWhenWasNegative() {
        val manager = mockManager()

        scenario = ActivityScenario.launch(
            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                context,
                manager
            )
        )

        putValue(R.id.edtLength, "-20")

        performClick(R.id.btnUpdaterManager)

        checkValueError(R.id.edtLength, context.getString(R.string.message_invalid_length))
    }

    @Test
    fun showErrorOnLengthAndNameWhenThemWasEmpty() {
        val name = "JustNumbers"
        val length = "3"

        val manager = ManagerPassword(
            0L,
            name,
            length,
            numbers = Pair(first = true, second = true),
            upperCase = Pair(first = false, second = false),
            lowerCase = Pair(first = false, second = false),
            accents = Pair(first = false, second = false),
            special = Pair(first = false, second = false)
        )

        scenario = ActivityScenario.launch(
            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                context,
                manager
            )
        )

        putValue(R.id.edtLength, "")
        putValue(R.id.edtName, "")

        performClick(R.id.btnUpdaterManager)

        checkValueError(R.id.edtLength, context.getString(R.string.message_invalid_length))
        checkValueError(R.id.edtName, context.getString(R.string.message_invalid_name))
    }

    @Test
    fun updatePasswordManager() {
        val startName = "StartName"
        val startLength = "3"

        val updatedName = "Update Name"
        val updatedLength = "2"

        val manager = ManagerPassword(
            0L,
            startName,
            startLength,
            numbers = Pair(first = true, second = true),
            upperCase = Pair(first = false, second = false),
            lowerCase = Pair(first = false, second = false),
            accents = Pair(first = false, second = false),
            special = Pair(first = false, second = false)
        )

        insertManager(manager)
        assertNotEquals(0L, manager.id)

        scenario = ActivityScenario.launch(
            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                context,
                manager
            )
        )

        putValue(R.id.edtLength, updatedLength)
        putValue(R.id.edtName, updatedName)

        val executeOn = ExpressoUtil.ExecuteOn(2)
        scenario?.onActivity {
            it.viewModel.apply {
                getManagerPasswordData().observeForever { manager ->
                    executeOn.next {
                        assertEquals(updatedName, manager.name)
                        assertEquals(updatedLength, manager.length)
                        assertNotEquals(0L, manager.id)

                        GlobalScope.launch {
                            val managerOnDao = withContext(Dispatchers.IO) {
                                getManager(manager.id)
                            }

                            assertEquals(managerOnDao.id, manager.id)
                            assertEquals(managerOnDao.name, manager.name)
                            assertEquals(managerOnDao.length, manager.length)
                        }
                    }
                }

                isUpdateFailed().observeForever {
                    fail()
                }
            }
        }

        performClick(R.id.btnUpdaterManager)
    }

    @Test
    fun deletePasswordManager() {
        val manager = mockManager()

        insertManager(manager)
        assertNotEquals(0L, manager.id)

        scenario = ActivityScenario.launch(
            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                context,
                manager
            )
        )

        putValue(R.id.edtLength, "2")
        putValue(R.id.edtName, "updatedName")

        scenario?.onActivity {
            it.viewModel.apply {
                deleteResult().observeForever { result ->
                    assertEquals(true, result)

                    GlobalScope.launch {
                        val list = withContext(Dispatchers.IO) {
                            getAll()
                        }

                        list?.forEach { m ->
                            if (m.id == manager.id)
                                fail()
                        }
                    }
                }

                isUpdateFailed().observeForever {
                    fail()
                }
            }
        }

        performClick(R.id.btnDelete)
    }
}