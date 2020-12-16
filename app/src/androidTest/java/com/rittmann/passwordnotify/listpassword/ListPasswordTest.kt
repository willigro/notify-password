package com.rittmann.passwordnotify.listpassword

import androidx.test.core.app.ActivityScenario
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.dao.room.config.AppDatabase
import com.rittmann.passwordnotify.data.dao.room.config.TableManagerPassword
import com.rittmann.passwordnotify.data.dao.room.config.selectAll
import com.rittmann.passwordnotify.data.dao.room.config.toDao
import com.rittmann.passwordnotify.support.ActivityTest
import com.rittmann.passwordnotify.support.ExpressoUtil.checkValueRecycler
import com.rittmann.passwordnotify.support.ExpressoUtil.getCurrentActivity
import com.rittmann.passwordnotify.support.ExpressoUtil.performClickRecycler
import com.rittmann.passwordnotify.ui.listpasswords.ListPasswordsActivity
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordActivity
import java.util.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ListPasswordTest : ActivityTest() {

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
            checkValueRecycler(this, R.id.txtName, pos, mock.name)
            performClickRecycler(this, pos)
        }

        assertEquals(true, getCurrentActivity() is ManagerPasswordActivity)
    }

    private fun mockManager(): ManagerPassword {
        val r = Random()

        return ManagerPassword(
            0L,
            r.nextInt(1000).toString(),
            r.nextInt(4).toString(),
            numbers = Pair(first = r.nextBoolean(), second = r.nextBoolean()),
            upperCase = Pair(first = r.nextBoolean(), second = r.nextBoolean()),
            lowerCase = Pair(first = r.nextBoolean(), second = r.nextBoolean()),
            accents = Pair(first = r.nextBoolean(), second = r.nextBoolean()),
            special = Pair(first = r.nextBoolean(), second = r.nextBoolean())
        )
    }

    private fun getAll(): List<ManagerPassword>? {
        val query = TableManagerPassword.TABLE.selectAll()
        return AppDatabase.getDatabase(context)?.managerPasswordDao()?.get(query.toDao())
    }

    private fun insertManager(manager: ManagerPassword) {
        manager.id = AppDatabase.getDatabase(context)?.managerPasswordDao()?.insert(manager) ?: 0L
    }
}