package com.rittmann.passwordnotify.ui.login

import android.content.Context
import com.rittmann.passwordnotify.data.basic.Login
import com.rittmann.passwordnotify.data.dao.room.config.AppDatabase
import com.rittmann.passwordnotify.data.dao.room.config.TableLogin
import com.rittmann.passwordnotify.data.dao.room.config.selectAll
import com.rittmann.passwordnotify.data.dao.room.config.toDao

interface LoginRepository {
    fun hasLogin(): Boolean
    fun registerPassword(login: Login): Long?
    fun checkPassword(password: String): Boolean
}

class LoginRepositoryImpl(context: Context) : LoginRepository {

    private val dao = AppDatabase.getDatabase(context)?.loginDao()

    override fun hasLogin(): Boolean {
        return dao?.hasLogin() ?: 0 > 0
    }

    override fun registerPassword(login: Login): Long? {
        return dao?.insert(login)
    }

    override fun checkPassword(password: String): Boolean {
        val query = TableLogin.TABLE.selectAll("${TableLogin.PASSWORD} = '$password'")
        return dao?.get(query.toDao()).isNullOrEmpty().not()
    }
}

