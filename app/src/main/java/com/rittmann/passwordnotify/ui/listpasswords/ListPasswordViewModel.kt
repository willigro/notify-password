package com.rittmann.passwordnotify.ui.listpasswords

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rittmann.baselifecycle.base.BaseViewModel
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface ListPasswordViewModel {
    fun passwordsResult(): LiveData<List<ManagerPassword>>
    fun getAllPasswords()
}

@Suppress("UNCHECKED_CAST")
class ListPasswordViewModelFactory(private val repository: ManagerPasswordRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListPasswordViewModelImpl(repository) as T
    }
}

class ListPasswordViewModelImpl(private val repository: ManagerPasswordRepository) :
    ListPasswordViewModel, BaseViewModel() {

    private val _passwords: MutableLiveData<List<ManagerPassword>> = MutableLiveData()

    override fun passwordsResult(): LiveData<List<ManagerPassword>> = _passwords

    override fun getAllPasswords() {
        viewModelScope.launch {
            val all = withContext(Dispatchers.IO) {
                repository.getAll()
            }

            _passwords.value = all
        }
    }
}