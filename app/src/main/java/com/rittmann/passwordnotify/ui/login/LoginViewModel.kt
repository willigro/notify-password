package com.rittmann.passwordnotify.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rittmann.baselifecycle.livedata.SingleLiveEvent
import com.rittmann.passwordnotify.data.basic.Login
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
    private val _passwordNotFound: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _passwordConfirmationNotFound: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _passwordDoesNotMatchWithConfirmation: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _passwordRegistered: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _passwordNotRegistered: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _passwordIsValid: SingleLiveEvent<Void> = SingleLiveEvent()
    private val _passwordIsNotValid: SingleLiveEvent<Void> = SingleLiveEvent()

    val hasLoginRegistered: LiveData<Void> = _hasLoginRegistered
    val loginNotFound: LiveData<Void> = _loginNotFound
    val passwordNotFound: LiveData<Void> = _passwordNotFound
    val passwordConfirmationNotFound: LiveData<Void> = _passwordConfirmationNotFound
    val passwordDoesNotMatchWithConfirmation: LiveData<Void> = _passwordDoesNotMatchWithConfirmation
    val passwordRegistered: LiveData<Void> = _passwordRegistered
    val passwordNotRegistered: LiveData<Void> = _passwordNotRegistered
    val passwordIsValid: LiveData<Void> = _passwordIsValid
    val passwordIsNotValid: LiveData<Void> = _passwordIsNotValid

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

    fun doLogin(password: String?, confirmation: String?, withConfirmation: Boolean) {
        showProgress()

        var invalid = false

        if (password.isNullOrEmpty()) {
            invalid = true
            _passwordNotFound.call()
        }

        if (withConfirmation) {
            if (confirmation.isNullOrEmpty()) {
                invalid = true
                _passwordConfirmationNotFound.call()
            }

            if (invalid.not()) {
                if (password == confirmation) {
                    registerPassword(password!!)
                } else {
                    _passwordDoesNotMatchWithConfirmation.call()
                }
            }
        } else {
            if (invalid.not())
                executeAsync {
                    val res = repository.checkPassword(password!!)

                    executeMain {
                        if (res) {
                            _passwordIsValid.call()
                        } else
                            _passwordIsNotValid.call()
                    }
                }
        }
    }

    private fun registerPassword(password: String) {
        executeAsync {
            val id = repository.registerPassword(Login(password = password))

            executeMain {
                if (id ?: 0L > 0L) {
                    _passwordRegistered.call()
                } else
                    _passwordNotRegistered.call()
            }
        }
    }
}