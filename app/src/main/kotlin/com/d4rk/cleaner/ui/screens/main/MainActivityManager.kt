package com.d4rk.cleaner.ui.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.d4rk.cleaner.data.core.AppCoreManager
import androidx.compose.runtime.setValue
import com.d4rk.cleaner.func.holder.DocumentHolder
import com.d4rk.cleaner.func.tabs.FilesTab
import com.d4rk.cleaner.func.tabs.Tab
import com.d4rk.cleaner.utils.extension.emptyString

class MainActivityManager {
    val globalClass = AppCoreManager.instance
    var title by mutableStateOf(globalClass.getString(com.d4rk.cleaner.R.string.app_name))
    var subtitle by mutableStateOf(emptyString)

    var curTab: Tab? = null

    fun replaceCurrentTabWith(newTab: Tab) {
        if (this.curTab != null) {
            this.curTab?.onTabStopped()
        }
        this.curTab = newTab
        selectTabAt()
    }

    fun selectTabAt() {
        this.curTab?.apply {
            if (!isCreated) onTabStarted() else onTabResumed()
        }
    }

    fun canExit(): Boolean {
        if (curTab?.onBackPressed() == true) {
            return false
        }
        return true
    }
}