package com.rittmann.passwordnotify.ui.managerpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rittmann.baselifecycle.livedata.SingleLiveEvent
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.extensions.isPositiveNumber
import com.rittmann.passwordnotify.ui.generatepassword.GeneratePasswordViewModelImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface ManagerPasswordViewModel {
    fun setManager(managerPassword: ManagerPassword)

    // Observers
    fun getManagerPasswordData(): LiveData<ManagerPassword>
    fun nameIsInvalid(): LiveData<Void>
    fun isInvalidLength(): LiveData<Void>
    fun isUpdateFailed(): LiveData<Void>

    // Functions
    fun updateManager(managerPassword: ManagerPassword)
}

class ManagerPasswordViewModelFactory(private val repository: ManagerPasswordRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ManagerPasswordViewModelImpl(repository) as T
    }
}

class ManagerPasswordViewModelImpl(private val repository: ManagerPasswordRepository) :
    GeneratePasswordViewModelImpl(repository), ManagerPasswordViewModel {

    private val _managerPassword: MutableLiveData<ManagerPassword> = MutableLiveData()
    private val _invalidName: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _updateInvalid: SingleLiveEvent<Void> = SingleLiveEvent()

    override fun setManager(managerPassword: ManagerPassword) {
        _managerPassword.value = managerPassword
    }

    override fun getManagerPasswordData(): LiveData<ManagerPassword> = _managerPassword
    override fun nameIsInvalid(): LiveData<Void> = _invalidName
    override fun getGeneratedPassword(): LiveData<String> = password
    override fun isInvalidLength(): LiveData<Void> = invalidLength
    override fun isUpdateFailed(): LiveData<Void> = _updateInvalid

    override fun updateManager(managerPassword: ManagerPassword) {
        if (isValidFields(managerPassword)) {
            viewModelScope.launch {

                withContext(Dispatchers.IO) {
                    repository.update(managerPassword).also { res ->

                        withContext(Dispatchers.Main) {
                            if (res != null && res > 0) {
                                _managerPassword.value = managerPassword
                            } else {
                                _updateInvalid.call()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isValidFields(managerPassword: ManagerPassword): Boolean {
        var isValid = true
        if (managerPassword.name.isEmpty()) {
            _invalidName.call()
            isValid = false
        }

        if (managerPassword.length.isNullOrEmpty() || managerPassword.length.isPositiveNumber().not()) {
            invalidLength.call()
            isValid = false
        }

        return isValid
    }

    override fun generatePassword(randomPermissions: ManagerPassword) {
    }
}