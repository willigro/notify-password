package com.rittmann.passwordnotify.ui.managerpassword

import android.content.Context
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.dao.room.config.AppDatabase
import com.rittmann.passwordnotify.data.dao.room.config.TableManagerPassword
import com.rittmann.passwordnotify.data.dao.room.config.selectAll
import com.rittmann.passwordnotify.data.dao.room.config.toDao

interface ManagerPasswordRepository {
    fun getManagerPasswordById(id: Long): List<ManagerPassword>?
    fun update(managerPassword: ManagerPassword): Int?
    fun register(managerPassword: ManagerPassword): Long?
}

class ManagerPasswordRepositoryImpl(context: Context) : ManagerPasswordRepository {

    private val dao = AppDatabase.getDatabase(context)?.managerPasswordDao()

    override fun getManagerPasswordById(id: Long): List<ManagerPassword>? {
        val query = TableManagerPassword.TABLE.selectAll("${TableManagerPassword.ID} = $id")
        return dao?.get(query.toDao())
    }

    override fun update(managerPassword: ManagerPassword): Int? {
        return dao?.update(managerPassword)
    }

    override fun register(managerPassword: ManagerPassword): Long? {
        return dao?.insert(managerPassword)
    }
}