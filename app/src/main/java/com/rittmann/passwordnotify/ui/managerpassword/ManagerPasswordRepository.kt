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
}

class ManagerPasswordRepositoryImpl(private val context: Context) : ManagerPasswordRepository {

    private val dao = AppDatabase.getDatabase(context)?.managerPasswordDao()

    override fun getManagerPasswordById(id: Long): List<ManagerPassword>? {
        val query = TableManagerPassword.TABLE.selectAll("${TableManagerPassword.ID} = $id")
        return dao?.get(query.toDao())
    }

    override fun update(managerPassword: ManagerPassword): Int? {
        return dao?.update(managerPassword)
    }
}