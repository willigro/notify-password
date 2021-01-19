package com.rittmann.passwordnotify.generate

import android.util.Log
import com.rittmann.passwordnotify.data.Constants
import com.rittmann.passwordnotify.data.TAG
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.extensions.replaceFor
import com.rittmann.passwordnotify.data.extensions.somethingContainsIn
import java.util.*

object GenerateRandomPassword {
    fun randomPassword(length: Int, permissions: ManagerPassword): String? {
        val chars = mutableListOf<Char>()

        var size = 0

        if (permissions.upperCase.first) {
            chars += Constants.upperCase
            size++
        }
        if (permissions.lowerCase.first) {
            chars += Constants.lowerCase
            size++
        }
        if (permissions.numbers.first) {
            chars += Constants.numbers
            size++
        }
        if (permissions.accents.first) {
            chars.addAll(Constants.accents)
            size++
        }
        if (permissions.special.first) {
            chars.addAll(Constants.special)
            size++
        }

        if (chars.isEmpty())
            return random(
                length,
                Constants.numbers + Constants.upperCase + Constants.lowerCase + Constants.accents + Constants.special
            ).apply {
                Log.i(TAG, this)
            }

        random(length, chars).apply {
            if (length >= size) {
                return adjustPasswordIfNeed(this, permissions).apply {
                    Log.i(TAG, this)
                }
            }
            Log.i(TAG, this)
            return this
        }
    }

    private fun adjustPasswordIfNeed(
        s: String,
        permissions: ManagerPassword
    ): String {
        var newString = s
        val excludeIndex = arrayListOf<Int>()
        if (permissions.lowerCaseRequired() && newString.somethingContainsIn(Constants.lowerCase)
                .not()
        ) {
            newString = newString.replaceFor(Constants.lowerCase, excludeIndex)
        }

        if (permissions.upperCaseRequired() && newString.somethingContainsIn(Constants.upperCase)
                .not()
        ) {
            newString = newString.replaceFor(Constants.upperCase, excludeIndex)
        }

        if (permissions.numberRequired() && newString.somethingContainsIn(Constants.numbers)
                .not()
        ) {
            newString = newString.replaceFor(Constants.numbers, excludeIndex)
        }

        if (permissions.specialRequired() && newString.somethingContainsIn(Constants.special)
                .not()
        ) {
            newString = newString.replaceFor(Constants.special, excludeIndex)
        }

        if (permissions.accentsRequired() && newString.somethingContainsIn(Constants.accents)
                .not()
        ) {
            newString =
                newString.replaceFor(Constants.accents, excludeIndex)
        }

        return newString
    }

    /**
     * For now, I'll use the constants, because is more easy to test
     * */
    private fun randomAll(length: Int): String {
        val generator = Random()
        val randomStringBuilder = StringBuilder()
        var tempChar: Char
        for (i in 0 until length) {
            tempChar = ((generator.nextInt(96) + 32).toChar())
            randomStringBuilder.append(tempChar)
        }
        return randomStringBuilder.toString()
    }

    private fun random(length: Int, allowedChars: List<Char>): String {
        return (1..length).joinToString("") { allowedChars.random().toString() }
    }
}