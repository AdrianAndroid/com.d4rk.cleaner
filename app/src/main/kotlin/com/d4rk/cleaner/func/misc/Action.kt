package com.d4rk.cleaner.func.misc

open class Action(
    var due: Boolean = true,
    var autoDismiss: Boolean = true,
    var isDone: Boolean = false,
    var action: () -> Unit = {}
)

class UpdateAction(due: Boolean) : Action(due)