package com.rittmann.passwordnotify.ui.base

import com.rittmann.baselifecycle.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseAppViewModel : BaseViewModel() {

    protected var viewModelScopeGen: CoroutineScope? = null

    fun executeAsync(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        blockOnMain: Boolean = false,
        scope: CoroutineScope = GlobalScope,
        block: suspend () -> Unit
    ) {
        val s = viewModelScopeGen ?: scope
        s.launch {
            withContext(dispatcher) {
                if (blockOnMain)
                    withContext(Dispatchers.Main) {
                        block()
                    }
                else
                    block()
            }
        }
    }

    fun executeMain(
        scope: CoroutineScope = GlobalScope,
        block: suspend () -> Unit
    ) {
        val s = viewModelScopeGen ?: scope
        s.launch {
            withContext(Dispatchers.Main) {
                block()
            }
        }
    }
}