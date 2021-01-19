package com.rittmann.passwordnotify.generatepassword

import com.rittmann.passwordnotify.R
import com.rittmann.passwordnotify.data.Constants
import com.rittmann.passwordnotify.data.extensions.somethingContainsIn
import com.rittmann.passwordnotify.support.ActivityTest
import com.rittmann.passwordnotify.support.ExpressoUtil.checkValueError
import com.rittmann.passwordnotify.support.ExpressoUtil.performClick
import com.rittmann.passwordnotify.support.ExpressoUtil.putValue
import com.rittmann.passwordnotify.ui.generatepassword.GeneratePasswordActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class GeneratePasswordViewTest : ActivityTest() {

    @Test
    fun generateRandomStringPasswordWithFourDigits() {
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(4, it.length)
            }
        }

        putValue(R.id.edtLength, "4")

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun generateRandomStringPasswordWithSixDigits() {
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(6, it.length)
            }
        }

        putValue(R.id.edtLength, "6")

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun showInvalidMessageErrorWhenLengthIsZero() {
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                fail()
            }
        }

        putValue(R.id.edtLength, "0")

        performClick(R.id.btnGeneratePassword)

        checkValueError(R.id.edtLength, targetContext.getString(R.string.message_invalid_length))
    }

    @Test
    fun showInvalidMessageErrorWhenLengthIsNegative() {
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                fail()
            }
        }

        putValue(R.id.edtLength, "-10")

        performClick(R.id.btnGeneratePassword)

        checkValueError(R.id.edtLength, targetContext.getString(R.string.message_invalid_length))
    }

    @Test
    fun generateRandomNumericPasswordWithSevenDigits() {
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(7, it.length)
                containsIn(it, true, Constants.numbers)
            }
        }

        putValue(R.id.edtLength, "7")

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun generateRandomNumericPasswordWithTwoDigits() {
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(2, it.length)
                containsIn(it, true, Constants.numbers)
            }
        }

        putValue(R.id.edtLength, "2")

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun generateRandomLowerCasePasswordWithTwentyDigits() {
        val digits = 20
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(digits, it.length)
                containsIn(it, true, Constants.lowerCase)

                containsIn(
                    it,
                    false,
                    Constants.upperCase + Constants.special + Constants.numbers + Constants.accents
                )
            }
        }

        putValue(R.id.edtLength, digits.toString())

        performClick(R.id.checkNumbers) // remove numbers
        performClick(R.id.checkLowerCase) // add lowerCase

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun generateRandomUpperCasePasswordWithTwentyDigits() {
        val digits = 20
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(digits, it.length)
                containsIn(it, true, Constants.upperCase)

                containsIn(
                    it,
                    false,
                    Constants.lowerCase + Constants.special + Constants.numbers + Constants.accents
                )
            }
        }

        putValue(R.id.edtLength, digits.toString())

        performClick(R.id.checkNumbers) // remove numbers
        performClick(R.id.checkUpperCase) // add upperCase

        performClick(R.id.btnGeneratePassword)
    }

    // this
    @Test
    fun generateRandomSpecialPasswordWithTwentyDigits() {
        val digits = 20
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(digits, it.length)
                containsIn(it, true, Constants.special)

                containsIn(
                    it,
                    false,
                    Constants.lowerCase + Constants.upperCase + Constants.numbers + Constants.accents
                )
            }
        }

        putValue(R.id.edtLength, digits.toString())

        performClick(R.id.checkNumbers) // remove numbers
        performClick(R.id.checkSpecial) // add special

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun generateRandomAccentPasswordWithTwentyDigits() {
        val digits = 20
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(digits, it.length)
                containsIn(it, true, Constants.accents)

                containsIn(
                    it,
                    false,
                    Constants.lowerCase + Constants.special + Constants.numbers + Constants.upperCase
                )
            }
        }

        putValue(R.id.edtLength, digits.toString())

        performClick(R.id.checkNumbers) // remove numbers
        performClick(R.id.checkAccent) // add accent

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun generateRandomLowerAndUpperCasePasswordWithTwentyDigits() {
        val digits = 20
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(digits, it.length)
                assertEquals(true, it.somethingContainsIn(Constants.lowerCase))
                assertEquals(true, it.somethingContainsIn(Constants.upperCase))

                containsIn(
                    it,
                    false,
                    Constants.special + Constants.numbers + Constants.accents
                )
            }
        }

        putValue(R.id.edtLength, digits.toString())

        performClick(R.id.checkNumbers) // remove numbers
        performClick(R.id.checkLowerCase)
        performClick(R.id.checkUpperCase)

        performClick(R.id.checkRequiredLowerCase)
        performClick(R.id.checkRequiredUpperCase)

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun generateRandomNumbersLowerAndUpperCasePasswordWithTwentyDigits() {
        val digits = 20
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(digits, it.length)
                assertEquals(true, it.somethingContainsIn(Constants.lowerCase))
                assertEquals(true, it.somethingContainsIn(Constants.upperCase))
                assertEquals(true, it.somethingContainsIn(Constants.numbers))

                containsIn(
                    it,
                    false,
                    Constants.special + Constants.accents
                )
            }
        }

        putValue(R.id.edtLength, digits.toString())

        performClick(R.id.checkLowerCase)
        performClick(R.id.checkUpperCase)

        performClick(R.id.checkRequiredLowerCase)
        performClick(R.id.checkRequiredUpperCase)

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun generateRandomSpecialAndNumbersLowerAndUpperCasePasswordWithTwentyDigits() {
        val digits = 20
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(digits, it.length)
                assertEquals(true, it.somethingContainsIn(Constants.lowerCase))
                assertEquals(true, it.somethingContainsIn(Constants.upperCase))
                assertEquals(true, it.somethingContainsIn(Constants.numbers))
                assertEquals(true, it.somethingContainsIn(Constants.special))


                containsIn(
                    it,
                    false,
                    Constants.accents
                )
            }
        }

        putValue(R.id.edtLength, digits.toString())

        performClick(R.id.checkLowerCase)
        performClick(R.id.checkUpperCase)
        performClick(R.id.checkSpecial)

        performClick(R.id.checkRequiredLowerCase)
        performClick(R.id.checkRequiredUpperCase)
        performClick(R.id.checkRequiredSpecial)

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun generateRandomAllPasswordWithTwentyDigits() {
        val digits = 20
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(digits, it.length)
                assertEquals(true, it.somethingContainsIn(Constants.lowerCase))
                assertEquals(true, it.somethingContainsIn(Constants.upperCase))
                assertEquals(true, it.somethingContainsIn(Constants.numbers))
                assertEquals(true, it.somethingContainsIn(Constants.special))
                assertEquals(true, it.somethingContainsIn(Constants.accents))
            }
        }

        putValue(R.id.edtLength, digits.toString())

        performClick(R.id.checkLowerCase)
        performClick(R.id.checkUpperCase)
        performClick(R.id.checkSpecial)
        performClick(R.id.checkAccent)

        performClick(R.id.checkRequiredLowerCase)
        performClick(R.id.checkRequiredUpperCase)
        performClick(R.id.checkRequiredSpecial)
        performClick(R.id.checkRequiredAccent)

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun uncheckAllCheckBoxAndGenerateRandomAllWithThirtyDigits() {
        val digits = 30
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(digits, it.length)
                assertEquals(true, it.somethingContainsIn(Constants.lowerCase))
                assertEquals(true, it.somethingContainsIn(Constants.upperCase))
                assertEquals(true, it.somethingContainsIn(Constants.numbers))
                assertEquals(true, it.somethingContainsIn(Constants.special))
                assertEquals(true, it.somethingContainsIn(Constants.accents))
            }
        }

        putValue(R.id.edtLength, digits.toString())

        performClick(R.id.checkNumbers) // remove number

        performClick(R.id.btnGeneratePassword)
    }

    /**
     * With a size less than the types amount, some list can be out, so I can't test with somethingContains
     * */
    @Test
    fun uncheckAllCheckBoxAndGenerateRandomAllWithFourDigits() {
        val digits = 4
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(digits, it.length)
            }
        }

        putValue(R.id.edtLength, digits.toString())

        performClick(R.id.checkNumbers) // remove number

        performClick(R.id.btnGeneratePassword)
    }

    @Test
    fun checkAllCheckBoxAndGenerateRandomAllWithFiveDigits() {
        val digits = 5
        getActivity<GeneratePasswordActivity>().onActivity { activity ->
            activity.viewModel.getGeneratedPassword().observeForever {
                assertEquals(digits, it.length)
                assertEquals(true, it.somethingContainsIn(Constants.lowerCase))
                assertEquals(true, it.somethingContainsIn(Constants.upperCase))
                assertEquals(true, it.somethingContainsIn(Constants.numbers))
                assertEquals(true, it.somethingContainsIn(Constants.special))
                assertEquals(true, it.somethingContainsIn(Constants.accents))
            }
        }

        putValue(R.id.edtLength, digits.toString())

        performClick(R.id.checkLowerCase)
        performClick(R.id.checkUpperCase)
        performClick(R.id.checkSpecial)
        performClick(R.id.checkAccent)

        performClick(R.id.checkRequiredLowerCase)
        performClick(R.id.checkRequiredUpperCase)
        performClick(R.id.checkRequiredSpecial)
        performClick(R.id.checkRequiredAccent)

        performClick(R.id.btnGeneratePassword)
    }

    private fun containsIn(it: String?, contains: Boolean, list: List<Any>) {
        if (it == null) fail()

        it!!.forEach { c ->
            assertEquals(contains, list.contains(c))
        }
    }
}