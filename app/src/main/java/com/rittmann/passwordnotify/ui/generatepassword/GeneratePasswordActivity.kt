package com.rittmann.passwordnotify.ui.generatepassword

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.extensions.toast
import com.rittmann.passwordnotify.ui.base.BaseAppActivity
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordActivity
import com.rittmann.widgets.dialog.DialogUtil
import com.rittmann.widgets.dialog.dialog
import kotlinx.android.synthetic.main.activity_generate_password.btnRegister
import kotlinx.android.synthetic.main.generate_password_input.btnGeneratePassword
import kotlinx.android.synthetic.main.generate_password_input.edtGeneratedPassword
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
import org.kodein.di.erased.instance

class GeneratePasswordActivity : BaseAppActivity() {

    private var modal: DialogUtil? = null
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
        addBackButton()

        btnGeneratePassword.setOnClickListener {
            showProgress()
            viewModel.generatePassword(generateManagerPermission())
        }

        btnRegister.setOnClickListener {
            if (edtGeneratedPassword.text.isNullOrEmpty()) {
                toast(R.string.message_error_generate_the_password)
                return@setOnClickListener
            }
            modal = dialog(
                message = getString(R.string.inform_a_identify_name),
                cancelable = true,
                resId = R.layout.dialog_with_input
            )
            modal?.handleShow({
                showProgress()

                val edt = modal!!.dialogView.findViewById<EditText>(R.id.edt_dialog)
                val name = edt.text.toString()
                if (name.isEmpty()) {
                    edt.error = getString(R.string.message_invalid_name)
                    hideProgress()
                } else {
                    val manager = generateManagerPermission().apply {
                        this.name = name
                    }

                    viewModel.registerManager(manager)

                    modal?.dismiss()
                }
            })
        }
    }

    private fun generateManagerPermission(): ManagerPassword {
        return ManagerPassword(
            0L,
            "",
            edtLength.text.toString(),
            Pair(checkNumbers.isChecked, checkRequiredNumbers.isChecked),
            Pair(checkUpperCase.isChecked, checkRequiredUpperCase.isChecked),
            Pair(checkLowerCase.isChecked, checkRequiredLowerCase.isChecked),
            Pair(checkAccent.isChecked, checkRequiredAccent.isChecked),
            Pair(checkSpecial.isChecked, checkRequiredSpecial.isChecked),
            password = edtGeneratedPassword.text.toString()
        )
    }

    private fun initObservers() {
        viewModel.apply {

            isInvalidLength().observe(this@GeneratePasswordActivity, {
                edtLength.error = getString(R.string.message_invalid_length)
                hideProgress()
            })

            getGeneratedPassword().observe(this@GeneratePasswordActivity, {
                edtGeneratedPassword.setText(it)
                hideProgress()
            })

            getRegisteredManager().observe(this@GeneratePasswordActivity, { manager ->
                hideProgress()
                manager?.also {
                    // to update the list when return
                    setResult(Activity.RESULT_OK)

                    startActivity(
                        ManagerPasswordActivity.getIntentManagerPasswordActivity(
                            this@GeneratePasswordActivity,
                            manager
                        )
                    )

                    finish()
                }
            })

            isFailedToRegisterManager().observe(this@GeneratePasswordActivity, {
                toast("Error to register") // todo change it
                hideProgress()
            })
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, GeneratePasswordActivity::class.java)
    }
}