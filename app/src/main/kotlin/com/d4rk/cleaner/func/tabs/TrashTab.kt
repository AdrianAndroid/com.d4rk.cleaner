package com.d4rk.cleaner.func.tabs

import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.utils.extension.emptyString

class TrashTab : Tab() {
    override val id = AppCoreManager.instance.generateUid()
    override val title: String = emptyString
    override val subtitle: String = emptyString
    override val header: String = emptyString
}