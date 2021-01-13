package com.rittmann.passwordnotify.ui.base

import android.os.Build
import com.rittmann.baselifecycle.base.BaseActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

open class BaseAppActivity : BaseActivity(), KodeinAware {

    override val kodein by kodein()

    protected fun canUseKeyguard() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
}