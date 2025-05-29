package com.d4rk.cleaner.func.holder

import com.d4rk.cleaner.data.core.AppCoreManager

abstract class ContentHolder {
    val uid = AppCoreManager.instance.generateUid()

    abstract fun getName(): String
    abstract fun getContent(): Any
}