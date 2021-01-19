package com.rittmann.passwordnotify.data.preferences

import android.content.Context

class SharedPreferencesModel(private val context: Context) {
    private fun getEditor() =
            context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun setUsingKeyguard(value: Boolean) = getEditor().edit().putBoolean(USING_KEYGUARD, value).apply()
    fun isUsingKeyguard(): Boolean = getEditor().getBoolean(USING_KEYGUARD, false)

    companion object {
        private const val PREFERENCES = "myPreferencesMeusGatos"
        private const val USING_KEYGUARD = "usingKeyguard"
    }
}