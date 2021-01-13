package com.rittmann.passwordnotify.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rittmann.baselifecycle.livedata.SingleLiveEvent
import com.rittmann.passwordnotify.ui.base.BaseAppViewModel

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(private val repository: LoginRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(repository) as T
    }
}

class LoginViewModel(private val repository: LoginRepository) : BaseAppViewModel() {

    private val _hasLoginRegistered: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _loginNotFound: SingleLiveEvent<Void> = SingleLiveEvent()

    val hasLoginRegistered: LiveData<Void> = _hasLoginRegistered
    val loginNotFound: LiveData<Void> = _loginNotFound

    fun hasLoginRegistered() {
        showProgress()
        executeAsync {
            val has = repository.hasLogin()
            executeMain {
                if (has)
                    _hasLoginRegistered.call()
                else
                    _loginNotFound.call()
            }
        }
    }
}