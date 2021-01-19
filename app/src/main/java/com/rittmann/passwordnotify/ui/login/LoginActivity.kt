package com.rittmann.passwordnotify.ui.login

import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.preferences.SharedPreferencesModel
import com.rittmann.passwordnotify.ui.base.BaseAppActivity
import com.rittmann.passwordnotify.ui.listpasswords.ListPasswordsActivity
import com.rittmann.widgets.dialog.dialog
import com.rittmann.widgets.extensions.isVisible
import com.rittmann.widgets.extensions.visible
import kotlinx.android.synthetic.main.activity_login.btnDoLogin
import kotlinx.android.synthetic.main.activity_login.edtPassword
import kotlinx.android.synthetic.main.activity_login.edtPasswordConfirmation
import kotlinx.android.synthetic.main.activity_login.labelLoginConfirmation
import org.kodein.di.erased.instance

class LoginActivity : BaseAppActivity() {

    override var resIdViewReference: Int = R.id.content

    private val viewModelFactory: LoginViewModelFactory by instance()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this, viewModelFactory).get(
            LoginViewModel::class.java
        )

        initViews()
        initObservers()
        checkKeyguard()

        viewModel.hasLoginRegistered()
    }

    private fun initViews() {
        btnDoLogin.setOnClickListener {
            viewModel.doLogin(
                edtPassword.text.toString(),
                edtPasswordConfirmation.text.toString(),
                edtPasswordConfirmation.isVisible()
            )
        }
    }

    private fun initObservers() {
        viewModel.apply {
            hasLoginRegistered.observe(this@LoginActivity, {
            })

            loginNotFound.observe(this@LoginActivity, {
                edtPasswordConfirmation.visible()
                labelLoginConfirmation.visible()
            })

            passwordNotFound.observe(this@LoginActivity, {
                edtPassword.error = getString(R.string.error_password_not_found)
            })

            passwordConfirmationNotFound.observe(this@LoginActivity, {
                edtPasswordConfirmation.error = getString(R.string.error_confirmation_not_found)
            })

            passwordDoesNotMatchWithConfirmation.observe(this@LoginActivity, {
                edtPasswordConfirmation.error = getString(R.string.error_confirmation_not_match)
            })

            passwordRegistered.observe(this@LoginActivity, {
                openListPasswordScreen()
            })

            passwordNotRegistered.observe(this@LoginActivity, {
            })

            passwordIsValid.observe(this@LoginActivity, {
                openListPasswordScreen()
            })

            passwordIsNotValid.observe(this@LoginActivity, {
                dialog(
                    message = getString(R.string.error_password_not_match_on_base),
                    ok = true,
                    show = true
                )
            })

            observeLoading(this)
        }
    }

    private fun openListPasswordScreen() {
        startActivity(ListPasswordsActivity.getIntent(this@LoginActivity))
        finish()
    }

    private fun checkKeyguard() {
        if (SharedPreferencesModel(this).isUsingKeyguard()) {
            if (canUseKeyguard()) {
                val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
                if (km.isKeyguardSecure) {
                    val authIntent = km.createConfirmDeviceCredentialIntent(
                        getString(R.string.app_name),
                        getString(R.string.auth_message_keyguard_secure)
                    )
                    startActivityForResult(authIntent, OPEN_AUTH)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_AUTH) {
            if (resultCode == RESULT_OK) {
                openListPasswordScreen()
            }
        }
    }

    companion object {
        const val OPEN_AUTH = 2
    }
}