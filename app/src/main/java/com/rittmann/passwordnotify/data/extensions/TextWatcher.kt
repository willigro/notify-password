package com.rittmann.passwordnotify.data.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.watcherAfter(callback: (s: Editable?) -> Unit) {
    val textWatcherValue = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            callback(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    addTextChangedListener(textWatcherValue)
}