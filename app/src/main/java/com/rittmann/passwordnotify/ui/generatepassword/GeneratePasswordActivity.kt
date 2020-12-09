package com.rittmann.passwordnotify.ui.generatepassword

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.RandomPermissions
import com.rittmann.passwordnotify.ui.base.BaseAppActivity
import kotlinx.android.synthetic.main.activity_generate_password.btnGeneratePassword
import kotlinx.android.synthetic.main.activity_generate_password.checkAccent
import kotlinx.android.synthetic.main.activity_generate_password.checkLowerCase
import kotlinx.android.synthetic.main.activity_generate_password.checkNumbers
import kotlinx.android.synthetic.main.activity_generate_password.checkRequiredAccent
import kotlinx.android.synthetic.main.activity_generate_password.checkRequiredLowerCase
import kotlinx.android.synthetic.main.activity_generate_password.checkRequiredNumbers
import kotlinx.android.synthetic.main.activity_generate_password.checkRequiredSpecial
import kotlinx.android.synthetic.main.activity_generate_password.checkRequiredUpperCase
import kotlinx.android.synthetic.main.activity_generate_password.checkSpecial
import kotlinx.android.synthetic.main.activity_generate_password.checkUpperCase
import kotlinx.android.synthetic.main.activity_generate_password.edtLength
import kotlinx.android.synthetic.main.activity_generate_password.txtPassword
import org.kodein.di.erased.instance

class GeneratePasswordActivity : BaseAppActivity() {

    override var resIdViewReference: Int = R.id.content

    private val generatePasswordViewModelFactory: GeneratePasswordViewModelFactory by instance()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var viewModel: GeneratePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_password)

        viewModel =
            ViewModelProvider(this, generatePasswordViewModelFactory).get(
                GeneratePasswordViewModelImpl::class.java
            )

        initView()
        initObservers()
    }

    private fun initView() {
        btnGeneratePassword.setOnClickListener {
            showProgress()
            val permissions = RandomPermissions(
                edtLength.text.toString(),
                Pair(checkNumbers.isChecked, checkRequiredNumbers.isChecked),
                Pair(checkUpperCase.isChecked, checkRequiredUpperCase.isChecked),
                Pair(checkLowerCase.isChecked, checkRequiredLowerCase.isChecked),
                Pair(checkAccent.isChecked, checkRequiredAccent.isChecked),
                Pair(checkSpecial.isChecked, checkRequiredSpecial.isChecked)
            )

            viewModel.generatePassword(permissions)
        }
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