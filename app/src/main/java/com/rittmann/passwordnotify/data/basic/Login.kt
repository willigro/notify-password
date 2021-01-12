package com.rittmann.passwordnotify.data.basic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rittmann.passwordnotify.data.dao.room.config.TableLogin
import java.io.Serializable


@Entity(tableName = TableLogin.TABLE)
data class Login(
    @ColumnInfo(name = TableLogin.ID)
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = TableLogin.PASSWORD)
    var name: String = ""
) : Serializable