package com.d4rk.cleaner.ui.screens.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.func.tabs.AnalyzeDetailTab
import com.d4rk.cleaner.func.tabs.AnalyzeFilesTab
import com.d4rk.cleaner.func.tabs.FilesTab
import com.d4rk.cleaner.ui.screens.tabs.components.AnalyzeDetailList
import com.d4rk.cleaner.ui.screens.tabs.components.AnalyzeFilesList
import com.d4rk.cleaner.ui.screens.tabs.components.FilesList
import com.d4rk.cleaner.ui.screens.tabs.components.PathListRow

@Composable
fun FilesTabContentView() {
    val mainActivityManager = AppCoreManager.instance.mainActivityManager
    when (val tab = mainActivityManager.curTab) {
        is AnalyzeDetailTab -> {
            Column(modifier = Modifier.fillMaxSize()) {
                AnalyzeDetailList(tab)
            }
        }
        is AnalyzeFilesTab -> {
            Column(modifier = Modifier.fillMaxSize()) {
                PathListRow(tab)
                HorizontalDivider(modifier = Modifier, thickness = 1.dp)
                AnalyzeFilesList(tab)
            }
        }
        is FilesTab -> {
            Column(modifier = Modifier.fillMaxSize()) {
                PathListRow(tab)
                HorizontalDivider(modifier = Modifier, thickness = 1.dp)
                FilesList(tab)
            }
        }
    }
}