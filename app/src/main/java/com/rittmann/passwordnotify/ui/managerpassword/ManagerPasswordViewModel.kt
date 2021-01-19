package com.rittmann.passwordnotify.ui.managerpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rittmann.androidtools.dateutil.DateUtilImpl
import com.rittmann.baselifecycle.livedata.SingleLiveEvent
import com.rittmann.passwordnotify.data.Constants
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.extensions.isPositiveNumber
import com.rittmann.passwordnotify.data.extensions.parseToInt
import com.rittmann.passwordnotify.generate.GenerateRandomPassword
import com.rittmann.passwordnotify.ui.generatepassword.GeneratePasswordViewModelImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/***
 * Ps: i don't like it, this interface
 */
interface ManagerPasswordViewModel {
    fun setManager(managerPassword: ManagerPassword)

    // Observers
    fun getManagerPasswordData(): LiveData<ManagerPassword>
    fun getManagerPasswordToScheduleNotificationData(): LiveData<ManagerPassword>
    fun nameIsInvalid(): LiveData<Void>
    fun isInvalidLength(): LiveData<Void>
    fun isUpdateFailed(): LiveData<Void>
    fun isUpdateToScheduleNotificationFailed(): LiveData<Void>
    fun deleteResult(): LiveData<Boolean>
    fun cancelNotification(): LiveData<Boolean>
    fun isUpdated(): LiveData<Boolean>
    fun generatePassword(randomPermissions: ManagerPassword)
    fun getGeneratedPassword(): LiveData<String>

    // Functions
    fun updateManager(managerPassword: ManagerPassword)
    fun deleteManager()
    fun scheduleNotification(days: Int)
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
    private val _managerPasswordToScheduleNotification: MutableLiveData<ManagerPassword> =
        MutableLiveData()
    private val _invalidName: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _updateInvalid: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _updateInvalidToScheduleNotification: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _deleteResult: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val _cancelNotification: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val _isUpdated: SingleLiveEvent<Boolean> = SingleLiveEvent()

    override fun setManager(managerPassword: ManagerPassword) {
        _managerPassword.value = managerPassword
    }

    override fun getManagerPasswordData(): LiveData<ManagerPassword> = _managerPassword
    override fun getManagerPasswordToScheduleNotificationData(): LiveData<ManagerPassword> =
        _managerPasswordToScheduleNotification

    override fun nameIsInvalid(): LiveData<Void> = _invalidName
    override fun getGeneratedPassword(): LiveData<String> = password
    override fun isInvalidLength(): LiveData<Void> = invalidLength
    override fun isUpdateFailed(): LiveData<Void> = _updateInvalid
    override fun isUpdateToScheduleNotificationFailed(): LiveData<Void> =
        _updateInvalidToScheduleNotification

    override fun deleteResult(): LiveData<Boolean> = _deleteResult
    override fun cancelNotification(): LiveData<Boolean> = _cancelNotification
    override fun isUpdated(): LiveData<Boolean> = _isUpdated

    override fun updateManager(managerPassword: ManagerPassword) {
        showProgress()
        if (isValidFields(managerPassword)) {
            executeAsync {
                managerPassword.apply {
                    _managerPassword.value?.also {
                        id = it.id
                        notificationDateFrom = it.notificationDateFrom
                        eachDaysToNotify = it.eachDaysToNotify
                    }
                }

                repository.update(managerPassword).also { res ->

                    withContext(Dispatchers.Main) {
                        if (res != null && res > 0) {
                            _managerPassword.value = managerPassword
                            _isUpdated.call()
                        } else {
                            _updateInvalid.call()
                        }

                        hideProgress()
                    }
                }
            }
        }
    }

    override fun deleteManager() {
        _managerPassword.value?.apply {
            executeAsync {
                repository.delete(this@apply).also { res ->

                    withContext(Dispatchers.Main) {
                        _deleteResult.value = res != null && res > 0
                    }
                }
            }
        }
    }

    override fun scheduleNotification(days: Int) {
        _managerPassword.value!!.apply {
            eachDaysToNotify = days

            notificationDateFrom = if (days > 0) {
                DateUtilImpl.today()
            } else {
                null
            }

            executeAsync {
                repository.update(this@apply).also { res ->

                    withContext(Dispatchers.Main) {
                        if (res != null && res > 0) {
                            _managerPassword.value = this@apply
                            if (days > 0)
                                _managerPasswordToScheduleNotification.value = this@apply
                            else
                                _cancelNotification.call()

                            _isUpdated.call()
                        } else {
                            _updateInvalidToScheduleNotification.call()
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

        if (managerPassword.length.isNullOrEmpty() || managerPassword.length.isPositiveNumber()
                .not()
        ) {
            invalidLength.call()
            isValid = false
        }

        return isValid
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