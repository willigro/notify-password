package com.rittmann.passwordnotify.ui.listpasswords

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.preferences.SharedPreferencesModel
import com.rittmann.passwordnotify.ui.base.BaseAppActivity
import com.rittmann.passwordnotify.ui.generatepassword.GeneratePasswordActivity
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordActivity
import com.rittmann.widgets.dialog.DialogUtil
import com.rittmann.widgets.dialog.dialog
import kotlinx.android.synthetic.main.activity_list_passwords.btnNewPassword
import kotlinx.android.synthetic.main.activity_list_passwords.content
import kotlinx.android.synthetic.main.activity_list_passwords.recyclerPassword
import org.kodein.di.erased.instance


class ListPasswordsActivity : BaseAppActivity() {

    private var modal: DialogUtil? = null
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

        initViews()
        initObservers()

        viewModel.getAllPasswords()
        if (canUseKeyguard() && SharedPreferencesModel(this).isUsingKeyguard().not()) {
            requestToUseKeyguard()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_MANAGER && resultCode == Activity.RESULT_OK) {
            viewModel.getAllPasswords()
        }
    }

    private fun initViews() {
        btnNewPassword.setOnClickListener {
            startActivity(GeneratePasswordActivity.getIntent(this@ListPasswordsActivity))
        }
    }

    private fun initObservers() {
        viewModel.apply {
            passwordsResult().observe(this@ListPasswordsActivity, {
                it?.also {
                    recyclerPassword.apply {
                        layoutManager = LinearLayoutManager(this@ListPasswordsActivity)
                        adapter = RecyclerViewPasswords(this@ListPasswordsActivity, it) { any ->
                            startActivityForResult(
                                ManagerPasswordActivity.getIntentManagerPasswordActivity(
                                    this@ListPasswordsActivity,
                                    any as ManagerPassword
                                ),
                                OPEN_MANAGER
                            )
                        }
                    }
                }
            })
        }
    }

    private fun requestToUseKeyguard() {
        content.post {
            dialog(
                message = getString(R.string.do_you_wish_use_your_keyguard_secure)
            ).apply {
                modal = this

                handleShow({
                    SharedPreferencesModel(this@ListPasswordsActivity).setUsingKeyguard(true)
                    dismiss()
                })
            }
        }
    }

    companion object {
        const val OPEN_MANAGER = 1

        fun getIntent(context: Context) = Intent(context, ListPasswordsActivity::class.java)
    }
}