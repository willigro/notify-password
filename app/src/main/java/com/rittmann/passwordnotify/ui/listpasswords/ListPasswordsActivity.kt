package com.rittmann.passwordnotify.ui.listpasswords

import android.os.Bundle
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.ui.base.BaseAppActivity

class ListPasswordsActivity : BaseAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_passwords)
    }
}