package com.rittmann.passwordnotify.listpassword

import androidx.test.core.app.ActivityScenario
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.extensions.toIntOrZero
import com.rittmann.passwordnotify.support.ExpressoUtil.checkValueRecycler
import com.rittmann.passwordnotify.support.ExpressoUtil.getCurrentActivity
import com.rittmann.passwordnotify.support.ExpressoUtil.performClick
import com.rittmann.passwordnotify.support.ExpressoUtil.performClickRecycler
import com.rittmann.passwordnotify.support.ExpressoUtil.pressBack
import com.rittmann.passwordnotify.support.ExpressoUtil.putValue
import com.rittmann.passwordnotify.support.ExpressoUtil.scrollToBottom
import com.rittmann.passwordnotify.support.PasswordSupportTest
import com.rittmann.passwordnotify.ui.generatepassword.GeneratePasswordActivity
import com.rittmann.passwordnotify.ui.listpasswords.ListPasswordsActivity
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ListPasswordActivityTest : PasswordSupportTest() {

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

    @Test
    fun listThePasswordsAgainWhenTheSelectedPasswordIsUpdated() {
        deleteAll()

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

        var length = 1
        val name = "Testing $pos"

        (getCurrentActivity() as ManagerPasswordActivity).viewModel.apply {
            isUpdated().observeForever {
            }
        }


        list[pos].length.toIntOrZero().also {
            if (it > 0) {
                length = mock.length.toIntOrZero() * 2
            }
            putValue(R.id.edtLength, length.toString())
        }

        putValue(R.id.edtName, name)

        performClick(R.id.btnUpdaterManager)

        pressBack()

        val listUpdated = getAll()
        assertEquals(listUpdated!![pos].length, length.toString())
        assertEquals(listUpdated[pos].name, name)

        assertEquals(true, getCurrentActivity() is ListPasswordsActivity)

        checkValueRecycler(R.id.recyclerPassword, R.id.txtName, pos, name)
    }
}