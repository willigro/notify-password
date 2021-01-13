package com.rittmann.passwordnotify.ui.login

import android.content.Context
import com.rittmann.passwordnotify.data.dao.room.config.AppDatabase

interface LoginRepository {
    fun hasLogin(): Boolean
}

class LoginRepositoryImpl(context: Context) : LoginRepository {

    private val dao = AppDatabase.getDatabase(context)?.loginDao()

    override fun hasLogin(): Boolean {
        return dao?.hasLogin() ?: 0 > 0
    }
}

