package com.rittmann.passwordnotify.data.basic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rittmann.passwordnotify.data.dao.room.config.TableManagerPassword
import java.io.Serializable
import java.util.*

@Entity(tableName = TableManagerPassword.TABLE)
data class ManagerPassword(
    @ColumnInfo(name = TableManagerPassword.ID)
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = TableManagerPassword.NAME)
    var name: String = "",

    @ColumnInfo(name = TableManagerPassword.LENGTH)
    val length: String?,

    @ColumnInfo(name = TableManagerPassword.NUMBERS)
    val numbers: Pair<Boolean, Boolean>,

    @ColumnInfo(name = TableManagerPassword.UPPERCASE)
    val upperCase: Pair<Boolean, Boolean>,

    @ColumnInfo(name = TableManagerPassword.LOWERCASE)
    val lowerCase: Pair<Boolean, Boolean>,

    @ColumnInfo(name = TableManagerPassword.ACCENTS)
    val accents: Pair<Boolean, Boolean>,

    @ColumnInfo(name = TableManagerPassword.SPECIAL)
    val special: Pair<Boolean, Boolean>,

    @ColumnInfo(name = TableManagerPassword.EACH_DAYS_TO_NOTIFY)
    var eachDaysToNotify: Int = 0
) : Serializable {

    @ColumnInfo(name = TableManagerPassword.NOTIFY_DATE_FROM)
    var notificationDateFrom: Calendar? = null

    fun lowerCaseRequired(): Boolean = lowerCase.first && lowerCase.second
    fun upperCaseRequired(): Boolean = upperCase.first && upperCase.second
    fun numberRequired(): Boolean = numbers.first && numbers.second
    fun accentsRequired(): Boolean = accents.first && accents.second
    fun specialRequired(): Boolean = special.first && special.second
}