package com.rittmann.passwordnotify.ui.managerpassword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.ui.base.BaseAppActivity
import kotlinx.android.synthetic.main.activity_manager_password.btnUpdaterManager
import kotlinx.android.synthetic.main.activity_manager_password.checkEnableNotifications
import kotlinx.android.synthetic.main.activity_manager_password.edtAmountToNotify
import kotlinx.android.synthetic.main.activity_manager_password.edtName
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

class ManagerPasswordActivity : BaseAppActivity() {

    override var resIdViewReference: Int = R.id.content

    private val viewModelFactory: ManagerPasswordViewModelFactory by instance()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var viewModel: ManagerPasswordViewModel

    private var managerPassword: ManagerPassword? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_password)

        viewModel = ViewModelProvider(this, viewModelFactory).get(
            ManagerPasswordViewModelImpl::class.java
        )

        viewModel.setManager(getManagerPassword())

        initViews()
        initObservers()
    }

    private fun initViews() {
        checkEnableNotifications.setOnCheckedChangeListener { _, b ->
            if (b && edtAmountToNotify.text.isNullOrEmpty())
                edtAmountToNotify.setText("1")
        }

        btnUpdaterManager.setOnClickListener {
//            managerPassword?.also {
//                NotificationWorkerManager.scheduleNextNotification(
//                    this@ManagerPasswordActivity,
//                    1,
//                    "a",
//                    it
//                ) // todo mocked
//            }

            val manager = generateManagerPermission()
            viewModel.updateManager(manager)
        }
    }

    private fun initObservers() {
        viewModel.apply {
            getManagerPasswordData().observe(this@ManagerPasswordActivity, {
                it?.also {
                    managerPassword = it

                    edtName.setText(it.name)
                    edtLength.setText(it.length)

                    with(it) {
                        checkNumbers.isChecked = numbers.first
                        checkRequiredNumbers.isChecked = numbers.second

                        checkUpperCase.isChecked = upperCase.first
                        checkRequiredUpperCase.isChecked = upperCase.second

                        checkLowerCase.isChecked = lowerCase.first
                        checkRequiredLowerCase.isChecked = lowerCase.second

                        checkSpecial.isChecked = special.first
                        checkRequiredSpecial.isChecked = special.second

                        checkAccent.isChecked = accents.first
                        checkRequiredAccent.isChecked = accents.second
                    }
                }
            })

            nameIsInvalid().observe(this@ManagerPasswordActivity, {
                edtName.error = getString(R.string.message_invalid_name)
                hideProgress()
            })

            isInvalidLength().observe(this@ManagerPasswordActivity, {
                edtLength.error = getString(R.string.message_invalid_length)
                hideProgress()
            })

            isUpdateFailed().observe(this@ManagerPasswordActivity, {
                // todo implement
                hideProgress()
            })
        }
    }

    private fun generateManagerPermission(): ManagerPassword {
        return ManagerPassword(
            id = managerPassword?.id ?: 0L,
            edtName.text.toString(),
            edtLength.text.toString(),
            Pair(checkNumbers.isChecked, checkRequiredNumbers.isChecked),
            Pair(checkUpperCase.isChecked, checkRequiredUpperCase.isChecked),
            Pair(checkLowerCase.isChecked, checkRequiredLowerCase.isChecked),
            Pair(checkAccent.isChecked, checkRequiredAccent.isChecked),
            Pair(checkSpecial.isChecked, checkRequiredSpecial.isChecked)
        )
    }

    private fun getManagerPassword() =
        intent!!.extras!!.getSerializable(MANAGER_ARGS) as ManagerPassword

    companion object {
        const val MANAGER_ARGS = "m"

        fun getIntentManagerPasswordActivity(context: Context, managerPassword: ManagerPassword) =
            Intent(context, ManagerPasswordActivity::class.java).apply {
                putExtra(MANAGER_ARGS, managerPassword)
            }
    }
}