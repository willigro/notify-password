package com.rittmann.passwordnotify.ui.generatepassword

import android.os.Bundle
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.dao.room.config.AppDatabase
import com.rittmann.passwordnotify.ui.base.BaseAppActivity
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordActivity
import com.rittmann.widgets.dialog.DialogUtil
import kotlinx.android.synthetic.main.activity_generate_password.btnGeneratePassword
import kotlinx.android.synthetic.main.activity_generate_password.btnRegister
import kotlinx.android.synthetic.main.password_permissions.checkAccent
import kotlinx.android.synthetic.main.password_permissions.checkLowerCase
import kotlinx.android.synthetic.main.password_permissions.checkNumbers
import kotlinx.android.synthetic.main.password_permissions.checkRequiredAccent
import kotlinx.android.synthetic.main.password_permissions.checkRequiredLowerCase
import kotlinx.android.synthetic.main.password_permissions.checkRequiredNumbers
import kotlinx.android.synthetic.main.password_permissions.checkRequiredSpecial
import kotlinx.android.synthetic.main.password_permissions.checkRequiredUpperCase
import kotlinx.android.synthetic.main.password_permissions.checkSpecial
import kotlinx.android.synthetic.main.password_permissions.checkUpperCase
import kotlinx.android.synthetic.main.password_permissions.edtLength
import kotlinx.android.synthetic.main.password_permissions.txtPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.erased.instance

class GeneratePasswordActivity : BaseAppActivity() {

    override var resIdViewReference: Int = R.id.content

    private val viewModelFactory: GeneratePasswordViewModelFactory by instance()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var viewModel: GeneratePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_password)

        viewModel = ViewModelProvider(this, viewModelFactory).get(
            GeneratePasswordViewModelImpl::class.java
        )

        initView()
        initObservers()
    }

    private fun initView() {
        btnGeneratePassword.setOnClickListener {
            showProgress()
            viewModel.generatePassword(getPermissions())
        }

        btnRegister.setOnClickListener {
            // TODO: put a EditText on the Dialog and register from other dialog

            val name = "Mocked"

            val manager = getPermissions().apply {
                this.name = name
            }

            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    Log.i(com.rittmann.passwordnotify.data.TAG, "try")
                    manager.id =
                        AppDatabase.getDatabase(this@GeneratePasswordActivity)?.managerPasswordDao()
                            ?.insert(manager) ?: 0L

                    Log.i(com.rittmann.passwordnotify.data.TAG, manager.toString())
                }

                withContext(Dispatchers.Main) {
                    DialogUtil().init(
                        this@GeneratePasswordActivity,
                        "message", "title", true
                    ).handleShow({
                        startActivity(
                            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                                this@GeneratePasswordActivity,
                                manager
                            )
                        )
                    })
                }
            }
        }
    }

    private fun getPermissions(): ManagerPassword {
        return ManagerPassword(
            0L,
            "",
            edtLength.text.toString(),
            Pair(checkNumbers.isChecked, checkRequiredNumbers.isChecked),
            Pair(checkUpperCase.isChecked, checkRequiredUpperCase.isChecked),
            Pair(checkLowerCase.isChecked, checkRequiredLowerCase.isChecked),
            Pair(checkAccent.isChecked, checkRequiredAccent.isChecked),
            Pair(checkSpecial.isChecked, checkRequiredSpecial.isChecked)
        )
    }

    private fun initObservers() {
        viewModel.apply {

            invalidLength().observe(this@GeneratePasswordActivity, {
                edtLength.error = getString(R.string.message_invalid_length)
                hideProgress()
            })

            getGeneratedPassword().observe(this@GeneratePasswordActivity, {
                txtPassword.text = it
                hideProgress()
            })
        }
    }
}