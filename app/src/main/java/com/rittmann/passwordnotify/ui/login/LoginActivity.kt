package com.rittmann.passwordnotify.ui.login

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.extensions.toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            if (km.isKeyguardSecure) {
                val authIntent = km.createConfirmDeviceCredentialIntent(
                    getString(R.string.auth_title_keyguard_secure),
                    getString(R.string.auth_message_keyguard_secure)
                )
                startActivityForResult(authIntent, OPEN_AUTH)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_AUTH) {
            if (resultCode == RESULT_OK) {
                toast("OK")
            } else {
                toast("No")
            }
        } else
            toast("ONo")
    }

    companion object {
        const val OPEN_AUTH = 2
    }
}