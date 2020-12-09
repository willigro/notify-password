package com.rittmann.passwordnotify.data.extensions

import android.util.Log

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