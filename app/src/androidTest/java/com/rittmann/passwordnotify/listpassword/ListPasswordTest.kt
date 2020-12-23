package com.rittmann.passwordnotify.listpassword

import androidx.test.core.app.ActivityScenario
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.support.ExpressoUtil.checkValueRecycler
import com.rittmann.passwordnotify.support.ExpressoUtil.getCurrentActivity
import com.rittmann.passwordnotify.support.ExpressoUtil.performClick
import com.rittmann.passwordnotify.support.ExpressoUtil.performClickRecycler
import com.rittmann.passwordnotify.support.ExpressoUtil.scrollToBottom
import com.rittmann.passwordnotify.support.PasswordSupportTest
import com.rittmann.passwordnotify.ui.generatepassword.GeneratePasswordActivity
import com.rittmann.passwordnotify.ui.listpasswords.ListPasswordsActivity
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ListPasswordTest : PasswordSupportTest() {

    private var scenario: ActivityScenario<ListPasswordsActivity>? = null

    @After
    fun cleanUp() {
        scenario?.close()
    }

    @Test
    fun listAllPasswords() {
        val mock = mockManager()
        insertManager(mock)

        scenario = ActivityScenario.launch(ListPasswordsActivity::class.java)

        scenario?.onActivity {
            it.viewModel.apply {
                passwordsResult().observeForever { list ->
                    assertEquals(false, list.isNullOrEmpty())
                }
            }
        }

        val list = getAll()
        if (list.isNullOrEmpty())
            fail()

        val pos = list!!.size - 1

        R.id.recyclerPassword.apply {
            scrollToBottom(this)
            checkValueRecycler(this, R.id.txtName, pos, mock.name)
        }
    }

    @Test
    fun listAllPasswordsAndSelectTheLastOneToOpenTheManagerPasswordScreen() {
        val mock = mockManager()
        insertManager(mock)

        scenario = ActivityScenario.launch(ListPasswordsActivity::class.java)

        scenario?.onActivity {
            it.viewModel.apply {
                passwordsResult().observeForever { list ->
                    assertEquals(false, list.isNullOrEmpty())
                }
            }
        }

        val list = getAll()
        if (list.isNullOrEmpty())
            fail()

        val pos = list!!.size - 1

        R.id.recyclerPassword.apply {
            scrollToBottom(this)
            checkValueRecycler(this, R.id.txtName, pos, mock.name)
            performClickRecycler(this, pos)
        }

        assertEquals(true, getCurrentActivity() is ManagerPasswordActivity)
    }

    @Test
    fun openGeneratePasswordScreenWhenBtnIsChick() {
        scenario = ActivityScenario.launch(ListPasswordsActivity::class.java)

        performClick(R.id.btnNewPassword)

        assertEquals(true, getCurrentActivity() is GeneratePasswordActivity)
    }
}