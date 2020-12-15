package com.rittmann.passwordnotify.ui.listpasswords

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.ui.base.BaseAppActivity
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordActivity
import kotlinx.android.synthetic.main.activity_list_passwords.recyclerPassword
import org.kodein.di.erased.instance

class ListPasswordsActivity : BaseAppActivity() {

    override var resIdViewReference: Int = R.id.content

    private val viewModelFactory: ListPasswordViewModelFactory by instance()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var viewModel: ListPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_passwords)

        viewModel = ViewModelProvider(this, viewModelFactory).get(
            ListPasswordViewModelImpl::class.java
        )

        showProgress()
        viewModel.getAllPasswords()

        initViews()
        initObservers()
    }

    private fun initViews() {

    }

    private fun initObservers() {
        viewModel.apply {
            passwordsResult().observe(this@ListPasswordsActivity, {
                hideProgress()
                it?.also {
                    recyclerPassword.apply {
                        layoutManager = LinearLayoutManager(this@ListPasswordsActivity)
                        adapter = RecyclerViewPasswords(this@ListPasswordsActivity, it) { any ->
                            startActivity(
                                ManagerPasswordActivity.getIntentManagerPasswordActivity(
                                    this@ListPasswordsActivity,
                                    any as ManagerPassword
                                )
                            )
                        }
                    }
                }
            })
        }
    }
}