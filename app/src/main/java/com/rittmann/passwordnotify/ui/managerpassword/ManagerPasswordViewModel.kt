package com.rittmann.passwordnotify.ui.managerpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rittmann.baselifecycle.base.BaseViewModel
import com.rittmann.passwordnotify.data.basic.ManagerPassword

interface ManagerPasswordViewModel {
    fun setManager(managerPassword: ManagerPassword)
    fun getManagerPasswordData(): LiveData<ManagerPassword>
}

class ManagerPasswordViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ManagerPasswordViewModelImpl() as T
    }
}

class ManagerPasswordViewModelImpl : BaseViewModel(), ManagerPasswordViewModel {

    private val _managerPassword: MutableLiveData<ManagerPassword> = MutableLiveData()

    override fun setManager(managerPassword: ManagerPassword) {
        _managerPassword.value = managerPassword
    }

    override fun getManagerPasswordData(): LiveData<ManagerPassword> = _managerPassword
}