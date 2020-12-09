package com.rittmann.passwordnotify.ui.base

import com.rittmann.baselifecycle.base.BaseActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

open class BaseAppActivity : BaseActivity(), KodeinAware {

    override val kodein by kodein()
}