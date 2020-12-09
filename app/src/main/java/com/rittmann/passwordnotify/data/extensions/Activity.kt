package com.rittmann.passwordnotify.data.extensions

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.toast(resId: Int) {
    Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show()
}