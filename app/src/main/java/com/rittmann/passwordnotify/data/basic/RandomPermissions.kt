package com.rittmann.passwordnotify.data.basic

/**
 * First: can be used
 * Second: required
 * */
data class RandomPermissions(
    val length: String?,
    val numbers: Pair<Boolean, Boolean>,
    val upperCase: Pair<Boolean, Boolean>,
    val lowerCase: Pair<Boolean, Boolean>,
    val accents: Pair<Boolean, Boolean>,
    val special: Pair<Boolean, Boolean>
) {
    fun lowerCaseRequired(): Boolean = lowerCase.first && lowerCase.second
    fun upperCaseRequired(): Boolean = upperCase.first && upperCase.second
    fun numberRequired(): Boolean = numbers.first && numbers.second
    fun accentsRequired(): Boolean = accents.first && accents.second
    fun specialRequired(): Boolean = special.first && special.second
}