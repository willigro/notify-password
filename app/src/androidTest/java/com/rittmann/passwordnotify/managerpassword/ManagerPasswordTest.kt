package com.rittmann.passwordnotify.managerpassword

import androidx.test.core.app.ActivityScenario
import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import com.rittmann.passwordnotify.support.ActivityTest
import com.rittmann.passwordnotify.support.ExpressoUtil.checkValue
import com.rittmann.passwordnotify.support.ExpressoUtil.viewIsChecked
import com.rittmann.passwordnotify.support.ExpressoUtil.viewIsNotChecked
import com.rittmann.passwordnotify.ui.managerpassword.ManagerPasswordActivity
import org.junit.Test

class ManagerPasswordTest : ActivityTest() {

    @Test
    fun checkStartShowingNumbersAndNumberRequired() {
        val name = "JustNumbers"
        val length = "3"

        val manager = ManagerPassword(
            0L,
            name,
            length,
            numbers = Pair(first = true, second = true),
            upperCase = Pair(first = false, second = false),
            lowerCase = Pair(first = false, second = false),
            accents = Pair(first = false, second = false),
            special = Pair(first = false, second = false)
        )

        ActivityScenario.launch<ManagerPasswordActivity>(
            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                context,
                manager
            )
        )

        checkValue(R.id.edtName, name)
        checkValue(R.id.edtLength, length)

        // Checked
        viewIsChecked(R.id.checkNumbers)
        viewIsChecked(R.id.checkRequiredNumbers)

        // Unchecked
        viewIsNotChecked(R.id.checkUpperCase)
        viewIsNotChecked(R.id.checkRequiredUpperCase)

        viewIsNotChecked(R.id.checkLowerCase)
        viewIsNotChecked(R.id.checkRequiredLowerCase)

        viewIsNotChecked(R.id.checkAccent)
        viewIsNotChecked(R.id.checkRequiredAccent)

        viewIsNotChecked(R.id.checkSpecial)
        viewIsNotChecked(R.id.checkRequiredSpecial)
    }

    @Test
    fun checkStartShowingAllCheckedButNotRequired() {
        val name = "NotRequired"
        val length = "5"

        val manager = ManagerPassword(
            0L,
            name,
            length,
            numbers = Pair(first = true, second = false),
            upperCase = Pair(first = true, second = false),
            lowerCase = Pair(first = true, second = false),
            accents = Pair(first = true, second = false),
            special = Pair(first = true, second = false)
        )

        ActivityScenario.launch<ManagerPasswordActivity>(
            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                context,
                manager
            )
        )

        checkValue(R.id.edtName, name)
        checkValue(R.id.edtLength, length)

        // Checked
        viewIsChecked(R.id.checkNumbers)
        viewIsChecked(R.id.checkUpperCase)
        viewIsChecked(R.id.checkLowerCase)
        viewIsChecked(R.id.checkAccent)
        viewIsChecked(R.id.checkSpecial)

        // Unchecked
        viewIsNotChecked(R.id.checkRequiredNumbers)
        viewIsNotChecked(R.id.checkRequiredUpperCase)
        viewIsNotChecked(R.id.checkRequiredLowerCase)
        viewIsNotChecked(R.id.checkRequiredAccent)
        viewIsNotChecked(R.id.checkRequiredSpecial)
    }

    @Test
    fun checkStartShowingAllUnchecked() {
        val name = "NotRequired"
        val length = "5"

        val manager = ManagerPassword(
            0L,
            name,
            length,
            numbers = Pair(first = false, second = false),
            upperCase = Pair(first = false, second = false),
            lowerCase = Pair(first = false, second = false),
            accents = Pair(first = false, second = false),
            special = Pair(first = false, second = false)
        )

        ActivityScenario.launch<ManagerPasswordActivity>(
            ManagerPasswordActivity.getIntentManagerPasswordActivity(
                context,
                manager
            )
        )

        checkValue(R.id.edtName, name)
        checkValue(R.id.edtLength, length)

        // Checked
        viewIsNotChecked(R.id.checkNumbers)
        viewIsNotChecked(R.id.checkUpperCase)
        viewIsNotChecked(R.id.checkLowerCase)
        viewIsNotChecked(R.id.checkAccent)
        viewIsNotChecked(R.id.checkSpecial)

        // Unchecked
        viewIsNotChecked(R.id.checkRequiredNumbers)
        viewIsNotChecked(R.id.checkRequiredUpperCase)
        viewIsNotChecked(R.id.checkRequiredLowerCase)
        viewIsNotChecked(R.id.checkRequiredAccent)
        viewIsNotChecked(R.id.checkRequiredSpecial)
    }
}