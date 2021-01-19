package com.rittmann.passwordnotify.ui.generatepassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rittmann.baselifecycle.livedata.SingleLiveEvent
import com.rittmann.passwordnotify.data.Constants
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.extensions.parseToInt
import com.rittmann.passwordnotify.generate.GenerateRandomPassword
import com.rittmann.passwordnotify.ui.base.BaseAppViewModel
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface GeneratePasswordViewModel {
    fun getGeneratedPassword(): LiveData<String>
    fun isInvalidLength(): LiveData<Void>
    fun getRegisteredManager(): LiveData<ManagerPassword>
    fun isFailedToRegisterManager(): LiveData<Void>
    fun registerManager(manager: ManagerPassword)
    fun generatePassword(randomPermissions: ManagerPassword)
}

class GeneratePasswordViewModelFactory(private val repository: ManagerPasswordRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GeneratePasswordViewModelImpl(repository) as T
    }
}

open class GeneratePasswordViewModelImpl(private val repository: ManagerPasswordRepository) :
    BaseAppViewModel(), GeneratePasswordViewModel {

    val password: MutableLiveData<String> = MutableLiveData()
    val invalidLength: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _failedToRegisterManager: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _registeredManager: SingleLiveEvent<ManagerPassword> = SingleLiveEvent()

    override fun getGeneratedPassword(): LiveData<String> = password
    override fun isInvalidLength(): LiveData<Void> = invalidLength

    override fun getRegisteredManager(): LiveData<ManagerPassword> = _registeredManager
    override fun isFailedToRegisterManager(): LiveData<Void> = _failedToRegisterManager

    override fun registerManager(manager: ManagerPassword) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                repository.register(manager).also { id ->

                    withContext(Dispatchers.Main) {
                        if (id == null || id <= 0L) {
                            _failedToRegisterManager.call()
                        } else {
                            manager.id = id
                            _registeredManager.postValue(manager)
                        }
                    }
                }
            }
        }
    }

    override fun generatePassword(randomPermissions: ManagerPassword) {
        randomPermissions.length.parseToInt({ length ->
            if (length <= 0 || length > Constants.MAX_PASSWORD_LENGTH)
                invalidLength.call()
            else {
                viewModelScope.launch {
                    withContext(Dispatchers.Default) {
                        password.postValue(
                            GenerateRandomPassword.randomPassword(
                                length,
                                randomPermissions
                            )
                        )
                    }
                }
            }
        }) {
            invalidLength.call()
        }
    }
}