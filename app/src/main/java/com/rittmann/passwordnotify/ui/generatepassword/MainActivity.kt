package com.rittmann.passwordnotify.ui.generatepassword

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.RandomPermissions
import com.rittmann.passwordnotify.ui.base.BaseAppActivity
import kotlinx.android.synthetic.main.activity_main.btnGeneratePassword
import kotlinx.android.synthetic.main.activity_main.checkAccent
import kotlinx.android.synthetic.main.activity_main.checkLowerCase
import kotlinx.android.synthetic.main.activity_main.checkNumbers
import kotlinx.android.synthetic.main.activity_main.checkRequiredAccent
import kotlinx.android.synthetic.main.activity_main.checkRequiredLowerCase
import kotlinx.android.synthetic.main.activity_main.checkRequiredNumbers
import kotlinx.android.synthetic.main.activity_main.checkRequiredSpecial
import kotlinx.android.synthetic.main.activity_main.checkRequiredUpperCase
import kotlinx.android.synthetic.main.activity_main.checkSpecial
import kotlinx.android.synthetic.main.activity_main.checkUpperCase
import kotlinx.android.synthetic.main.activity_main.edtLength
import kotlinx.android.synthetic.main.activity_main.txtPassword
import org.kodein.di.erased.instance

class MainActivity : BaseAppActivity() {

    override var resIdViewReference: Int = R.id.content

    private val generatePasswordViewModelFactory: GeneratePasswordViewModelFactory by instance()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var viewModel: GeneratePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

            invalidLength().observe(this@MainActivity, {
                edtLength.error = getString(R.string.message_invalid_length)
                hideProgress()
            })

            getGeneratedPassword().observe(this@MainActivity, {
                txtPassword.text = it
                hideProgress()
            })
        }
    }
}