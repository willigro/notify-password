package com.rittmann.passwordnotify.data.dao.room.config

import androidx.sqlite.db.SimpleSQLiteQuery

object QueryDAO {
    const val ASC = " ASC "
    const val DESC = " DESC "

    fun orderBy(col: String, ordenation: String): String {
        return if (col.isEmpty() || ordenation.isEmpty()) "" else " ORDER BY $col $ordenation"
    }

    fun inDate(col: String): String {
        return " strftime('%d'," + col + ") = ? " +
                " AND strftime('%m'," + col + ") = ? " +
                " AND strftime('%Y'," + col + ") =  ? "
    }

    fun betweenDate(dateStart: String, dateEnd: String, col: String): String {
        return " strftime('%Y-%m-%d',$col) BETWEEN '$dateStart' AND '$dateEnd'"
    }

    fun inMonth(col: String): String {
        return " strftime('%m',$col) = ? AND strftime('%Y',$col) =  ? "
    }

}

fun String.selectAll(where: String = ""): String {
    return "SELECT * FROM $this ${where.where()}"
}

fun String.where(): String {
    return if (isEmpty()) "" else " WHERE $this"
}

//fun String.inDate(calendar: Calendar): String {
//    return " strftime('%d',$this) = '${ODate.getDaySQLite(calendar)}' " +
//            " AND strftime('%m',$this) = '${ODate.getMonthSQLite(calendar)}' " +
//            " AND strftime('%Y',$this) = '${calendar.get(Calendar.YEAR)}' "
//}
//
//fun String.inMonth(calendar: Calendar): String {
//    return " strftime('%m',$this) = '${ODate.getMonthSQLite(calendar)}' AND strftime('%Y',$this) = '${calendar.get(Calendar.YEAR)}' "
//}
//
//fun String.between(init: Calendar, end: Calendar): String {
//    return " strftime('%Y-%m-%d',$this) BETWEEN '${ODate.getOnlyDate(init)}' AND '${ODate.getOnlyDate(end)}'"
//}

fun String.orderBy(ordering: String): String {
    return if (isEmpty() || ordering.isEmpty()) "" else " ORDER BY $this $ordering"
}

fun String.like(like: String): String {
    return " $this LIKE '$like%' "
}

fun String.toDao() = SimpleSQLiteQuery(this)