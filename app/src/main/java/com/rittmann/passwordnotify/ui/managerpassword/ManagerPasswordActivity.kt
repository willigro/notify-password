package com.rittmann.passwordnotify.ui.managerpassword

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import com.rittmann.androidtools.dateutil.DateUtilImpl
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.data.extensions.toIntOrZero
import com.rittmann.passwordnotify.data.extensions.toast
import com.rittmann.passwordnotify.data.extensions.watcherAfter
import com.rittmann.passwordnotify.ui.base.BaseAppActivity
import com.rittmann.widgets.dialog.DialogUtil
import com.rittmann.widgets.dialog.dialog
import com.rittmann.widgets.extensions.gone
import com.rittmann.widgets.extensions.visible
import java.util.*
import kotlinx.android.synthetic.main.activity_manager_password.btnDelete
import kotlinx.android.synthetic.main.activity_manager_password.btnGeneratePassword
import kotlinx.android.synthetic.main.activity_manager_password.btnScheduleNotification
import kotlinx.android.synthetic.main.activity_manager_password.btnUpdaterManager
import kotlinx.android.synthetic.main.activity_manager_password.edtGeneratedPassword
import kotlinx.android.synthetic.main.activity_manager_password.edtName
import kotlinx.android.synthetic.main.activity_manager_password.labelNotificationDescription
import kotlinx.android.synthetic.main.activity_manager_password.labelNotificationsNotFound
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
    private var modal: DialogUtil? = null
    private var isUpdated = false

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

    override fun onBackPressed() {
        if (isUpdated)
            setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    private fun initViews() {
        btnGeneratePassword.setOnClickListener {
            viewModel.generatePassword(generateManagerPermission())
        }

        btnUpdaterManager.setOnClickListener {
            modal?.dismiss()
            dialog(
                message = getString(R.string.dialog_message_confirmation_to_update),
                resId = R.layout.dialog_confirm_cancel
            ).apply {
                modal = this
                handleShow({
                    val manager = generateManagerPermission()
                    viewModel.updateManager(manager)

                    dismiss()
                })
            }
        }

        btnScheduleNotification.setOnClickListener {
            modal?.dismiss()
            dialog(
                message = "message",
                cancelable = true,
                resId = R.layout.dialog_schedule_notification
            ).apply {
                modal = this
                dialogView.apply {
                    val label = findViewById<TextView>(R.id.txtScheduleNotification)
                    label.text = getString(R.string.schedule_a_notification_for_each).format(0)

                    findViewById<EditText>(R.id.edtEachDays).watcherAfter {
                        label.text = getString(R.string.schedule_a_notification_for_each).format(
                            it.toString().toIntOrZero()
                        )
                    }
                }

                handleShow({
                    val days =
                        modal!!.dialogView.findViewById<EditText>(R.id.edtEachDays).text.toString()
                            .toIntOrZero()

                    viewModel.scheduleNotification(days)
                })
            }
        }

        btnDelete.setOnClickListener {
            modal?.dismiss()
            dialog(
                message = getString(R.string.dialog_message_confirmation_to_delete),
                resId = R.layout.dialog_confirm_cancel
            ).apply {
                modal = this
                handleShow({
                    viewModel.deleteManager()

                    dismiss()
                })
            }
        }
    }

    private fun initObservers() {
        viewModel.apply {
            getGeneratedPassword().observe(this@ManagerPasswordActivity, {
                edtGeneratedPassword.setText(it)
            })

            getManagerPasswordData().observe(this@ManagerPasswordActivity, {
                it?.also {
                    managerPassword = it

                    edtGeneratedPassword.setText(it.password)

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

                    if (it.notificationDateFrom == null) {
                        labelNotificationsNotFound.visible()
                        labelNotificationDescription.gone()
                    } else {
                        labelNotificationsNotFound.gone()
                        labelNotificationDescription.apply {
                            visible()

                            val notificationDateTo: Calendar =
                                it.notificationDateFrom!!.clone() as Calendar
                            notificationDateTo.add(Calendar.DAY_OF_MONTH, it.eachDaysToNotify)

                            text = getString(R.string.notification_description).format(
                                DateUtilImpl.dateFormat(
                                    it.notificationDateFrom!!,
                                    DateUtilImpl.SIMPLE_DATE_FORMAT
                                ),
                                DateUtilImpl.dateFormat(
                                    notificationDateTo,
                                    DateUtilImpl.SIMPLE_DATE_FORMAT
                                )
                            )
                        }
                    }
                }
            })

            getManagerPasswordToScheduleNotificationData().observe(this@ManagerPasswordActivity, {
                modal?.dismiss()
                WorkManagerNotify().sendPeriodic(
                    this@ManagerPasswordActivity,
                    it.eachDaysToNotify.toLong(),
                    Notification(
                        managerPassword!!.id,
                        getString(R.string.notification_title_change_your_password),
                        getString(R.string.notification_message_change_your_password)
                    )
                )
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

            isUpdateToScheduleNotificationFailed().observe(this@ManagerPasswordActivity, {
                // todo implement
                modal?.dismiss()
                hideProgress()
            })

            deleteResult().observe(this@ManagerPasswordActivity, {
                hideProgress()
                if (it!!) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            })

            cancelNotification().observe(this@ManagerPasswordActivity, {
                WorkManagerNotify().cancel(this@ManagerPasswordActivity, managerPassword?.id ?: 0L)
                modal?.dismiss()
            })

            isUpdated().observe(this@ManagerPasswordActivity, {
                isUpdated = true
                toast(R.string.password_updated)
            })
        }
    }

    private fun generateManagerPermission(): ManagerPassword {
        return ManagerPassword(
            id = 0L,
            edtName.text.toString(),
            edtLength.text.toString(),
            Pair(checkNumbers.isChecked, checkRequiredNumbers.isChecked),
            Pair(checkUpperCase.isChecked, checkRequiredUpperCase.isChecked),
            Pair(checkLowerCase.isChecked, checkRequiredLowerCase.isChecked),
            Pair(checkAccent.isChecked, checkRequiredAccent.isChecked),
            Pair(checkSpecial.isChecked, checkRequiredSpecial.isChecked),
            password = edtGeneratedPassword.text.toString()
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