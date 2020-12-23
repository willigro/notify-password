package com.rittmann.passwordnotify.data.extensions

fun String?.parseToInt(callback: (Int) -> Unit, callbackError: (() -> Unit)? = null) {
    try {
        val value = this!!.toInt()
        callback(value)
    } catch (e: Exception) {
        e.printStackTrace()
        callbackError?.invoke()
    }
}

fun Char?.isInt(): Boolean {
    return try {
        this!!.toInt()
        true
    } catch (e: Exception) {
        false
    }
}

fun String?.isPositiveNumber(): Boolean {
    return try {
        val i = this!!.toInt()
        i > 0
    } catch (e: Exception) {
        false
    }
}