package com.rittmann.passwordnotify.data.dao.room.interfaces

import androidx.room.Dao
import androidx.room.Query
import com.rittmann.passwordnotify.data.dao.room.config.TableLogin

@Dao
interface LoginDAO {
    @Query("SELECT COUNT(${TableLogin.ID}) FROM ${TableLogin.TABLE}")
    fun hasLogin(): Int
}