package com.rittmann.passwordnotify.data.extensions

import java.util.*

fun String?.somethingContainsIn(list: List<Any>): Boolean {
    if (isNullOrEmpty() || list.isEmpty()) return false

    var something = false
    for (c in this) {
        if (list.contains(c)) {
            something = true
            break
        }
    }

    return something
}

fun String?.containsIn(it: String?, list: List<Any>): Boolean {
    if (isNullOrEmpty() || list.isEmpty()) return false

    this.forEach { c ->
        if (list.contains(c).not())
            return false
    }

    return true
}

 fun String.replaceFor(
    lowerCase: ArrayList<Char>,
    excludeIndex: ArrayList<Int>
): String {
    val c = lowerCase.random()
    var r = Random().nextInt(length)

    while (excludeIndex.contains(r))
        r = Random().nextInt(length)

    val nw = substring(0, r) + c + substring(r + 1)
    excludeIndex.add(r)

    return nw
}