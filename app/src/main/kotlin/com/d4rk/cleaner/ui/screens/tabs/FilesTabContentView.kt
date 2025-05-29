package com.d4rk.cleaner.ui.screens.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.func.tabs.FilesTab
import com.d4rk.cleaner.ui.screens.tabs.components.PathListRow

@Composable
fun FilesTabContentView() {
    val mainActivityManager = AppCoreManager.instance.mainActivityManager
    val tab = mainActivityManager.curTab
    if (tab is FilesTab) {
        Column(modifier = Modifier.fillMaxSize()) {
            PathListRow(tab)
        }
    }
}