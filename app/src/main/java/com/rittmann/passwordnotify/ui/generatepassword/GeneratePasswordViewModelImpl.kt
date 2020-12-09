package com.rittmann.passwordnotify.ui.generatepassword

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rittmann.baselifecycle.base.BaseViewModel
import com.rittmann.baselifecycle.livedata.SingleLiveEvent
import com.rittmann.passwordnotify.data.basic.RandomPermissions
import com.rittmann.passwordnotify.data.Constants
import com.rittmann.passwordnotify.data.TAG
import com.rittmann.passwordnotify.data.extensions.parseToInt
import com.rittmann.passwordnotify.data.extensions.replaceFor
import com.rittmann.passwordnotify.data.extensions.somethingContainsIn
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface GeneratePasswordViewModel {
    fun getGeneratedPassword(): LiveData<String>
    fun invalidLength(): LiveData<Boolean>
    fun generatePassword(randomPermissions: RandomPermissions)
}

class GeneratePasswordViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GeneratePasswordViewModelImpl() as T
    }
}

class GeneratePasswordViewModelImpl : BaseViewModel(), GeneratePasswordViewModel {

    private val _password: MutableLiveData<String> = MutableLiveData()
    private val _invalidLength = SingleLiveEvent<Boolean>()

    override fun getGeneratedPassword(): LiveData<String> = _password
    override fun invalidLength(): LiveData<Boolean> = _invalidLength

    override fun generatePassword(randomPermissions: RandomPermissions) {
        randomPermissions.length.parseToInt({ length ->
            if (length <= 0 || length > 1000)
                _invalidLength.call()
            else {
                viewModelScope.launch {
                    withContext(Dispatchers.Default) {
                        _password.postValue(randomPassword(length, randomPermissions))
                    }
                }
            }
        }) {
            _invalidLength.call()
        }
    }

    private fun randomPassword(length: Int, permissions: RandomPermissions): String? {
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
        permissions: RandomPermissions
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