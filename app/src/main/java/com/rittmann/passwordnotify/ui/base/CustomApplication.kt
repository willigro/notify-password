package com.rittmann.passwordnotify.ui.base

import android.app.Application
import android.content.Context
import com.rittmann.passwordnotify.ui.generatepassword.GeneratePasswordViewModel
import com.rittmann.passwordnotify.ui.generatepassword.GeneratePasswordViewModelFactory
import com.rittmann.passwordnotify.ui.generatepassword.GeneratePasswordViewModelImpl
import com.rittmann.passwordnotify.ui.listpasswords.ListPasswordViewModel
import com.rittmann.passwordnotify.ui.listpasswords.ListPasswordViewModelFactory
import com.rittmann.passwordnotify.ui.listpasswords.ListPasswordViewModelImpl
import com.rittmann.passwordnotify.ui.login.LoginRepositoryImpl
import com.rittmann.passwordnotify.ui.login.LoginViewModel
import com.rittmann.passwordnotify.ui.login.LoginViewModelFactory
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordRepositoryImpl
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordViewModel
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordViewModelFactory
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordViewModelImpl
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.erased.*

fun Context.asApp() = this.applicationContext as CustomApplication

class CustomApplication : Application(), KodeinAware {

    var testModule: Kodein.Module? = null

    override val kodein = Kodein.lazy {
        bindRepositories()

        bindViewModelFactories()

        bindModels()

        testModule?.also {
            import(testModule!!, allowOverride = true)
        }
    }

    private fun Kodein.MainBuilder.bindModels() {
        bind<GeneratePasswordViewModel>() with provider { GeneratePasswordViewModelImpl(instance()) }
        bind<ManagerPasswordViewModel>() with provider { ManagerPasswordViewModelImpl(instance()) }
        bind<ListPasswordViewModel>() with provider { ListPasswordViewModelImpl(instance()) }
    }

    private fun Kodein.MainBuilder.bindRepositories() {
        bind() from provider { ManagerPasswordRepositoryImpl(applicationContext) }
        bind() from provider { LoginRepositoryImpl(applicationContext) }
    }

    private fun Kodein.MainBuilder.bindViewModelFactories() {
        bind() from provider { GeneratePasswordViewModelFactory(instance()) }
        bind() from provider { ManagerPasswordViewModelFactory(instance()) }
        bind() from provider { ListPasswordViewModelFactory(instance()) }
        bind() from provider { LoginViewModelFactory(instance()) }
    }
}