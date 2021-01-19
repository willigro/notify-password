package com.rittmann.passwordnotify.data.dao.room.config

import androidx.room.TypeConverter
import com.rittmann.androidtools.dateutil.DateUtilImpl
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): Calendar? {
        if (value == null) return null
        return DateUtilImpl.parseDate(value, DateUtilImpl.SIMPLE_DATE_FORMAT)
    }

    @TypeConverter
    fun dateToTimestamp(date: Calendar?): String? {
        if (date == null) return null
        return DateUtilImpl.dateFormat(date, DateUtilImpl.SIMPLE_DATE_FORMAT)
    }

    @TypeConverter
    fun stringToPair(value: String?): Pair<Boolean, Boolean>? {
        if (value == null) return null
        try {
            with(value.split("-")) {
                val a = this[0].toBoolean()
                val b = this[1].toBoolean()
                return Pair(a, b)
            }
        } catch (e: Exception) {
            return Pair(first = false, second = false)
        }
    }

    @TypeConverter
    fun pairToString(value: Pair<Boolean, Boolean>?): String? {
        if (value == null) return null
        return "${value.first}-${value.second}"
    }
}