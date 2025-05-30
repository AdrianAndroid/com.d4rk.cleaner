package com.d4rk.cleaner.utils.extension

import com.d4rk.cleaner.data.core.AppCoreManager

fun Int.toRes(): String {
    return AppCoreManager.instance.getString(this)
}

fun Int.toResArgs(vararg args: Any): String {
    return AppCoreManager.instance.getString(this, args)
}