package com.rittmann.passwordnotify.ui.base

import android.os.Build
import android.view.MenuItem
import com.rittmann.baselifecycle.base.BaseActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

open class BaseAppActivity : BaseActivity(), KodeinAware {

    override val kodein by kodein()

    protected fun canUseKeyguard() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    private var hasToolbar: Boolean = false

    fun addBackButton() {
        // add back arrow to toolbar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            hasToolbar = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (hasToolbar && item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)

    }
}