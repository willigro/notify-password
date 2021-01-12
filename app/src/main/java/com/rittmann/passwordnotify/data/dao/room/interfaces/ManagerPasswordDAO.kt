package com.rittmann.passwordnotify.data.dao.room.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.dao.room.config.TableManagerPassword

@Dao
interface ManagerPasswordDAO {
    @RawQuery(observedEntities = [ManagerPassword::class])
    fun get(query: SupportSQLiteQuery): List<ManagerPassword>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(password: ManagerPassword): Long

    @Update
    fun update(password: ManagerPassword): Int

    @Delete
    fun delete(password: ManagerPassword): Int

    @Query("DELETE FROM ${TableManagerPassword.TABLE}")
    fun deleteAll()
}