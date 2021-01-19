package com.rittmann.passwordnotify.support

import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.dao.room.config.AppDatabase
import com.rittmann.passwordnotify.data.dao.room.config.TableManagerPassword
import com.rittmann.passwordnotify.data.dao.room.config.selectAll
import com.rittmann.passwordnotify.data.dao.room.config.toDao
import java.util.*
import org.junit.Assert

open class PasswordSupportTest : ActivityTest() {
    protected fun mockManager(): ManagerPassword {
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

    protected fun getAll(): List<ManagerPassword>? {
        val query = TableManagerPassword.TABLE.selectAll()
        return AppDatabase.getDatabase(context)?.managerPasswordDao()?.get(query.toDao())
    }

    protected fun getManager(id: Long): ManagerPassword {
        val query = TableManagerPassword.TABLE.selectAll("${TableManagerPassword.ID} = $id")
        val res = AppDatabase.getDatabase(context)?.managerPasswordDao()?.get(query.toDao())
        if (res.isNullOrEmpty())
            Assert.fail()
        return res!![0]
    }

    protected fun insertManager(manager: ManagerPassword) {
        manager.id = AppDatabase.getDatabase(context)?.managerPasswordDao()?.insert(manager) ?: 0L
    }

    protected fun deleteAll(){
        AppDatabase.getDatabase(context)?.managerPasswordDao()?.deleteAll()
    }
}