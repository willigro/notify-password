package com.rittmann.passwordnotify.data.dao.room.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.rittmann.passwordnotify.data.basic.Login
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.dao.room.config.TableLogin

@Dao
interface LoginDAO {
    @Query("SELECT COUNT(${TableLogin.ID}) FROM ${TableLogin.TABLE}")
    fun hasLogin(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(login: Login): Long

    @RawQuery(observedEntities = [ManagerPassword::class])
    fun get(query: SupportSQLiteQuery): List<Login>

    @Query("DELETE FROM ${TableLogin.TABLE}")
    fun deleteAll()
}