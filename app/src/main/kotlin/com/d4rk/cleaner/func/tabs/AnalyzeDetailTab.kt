package com.d4rk.cleaner.func.tabs

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.func.holder.DocumentHolder
import com.d4rk.cleaner.func.tabs.FilesTab.FileOptionsDialog
import com.d4rk.cleaner.utils.extension.emptyString

class AnalyzeDetailTab(val sourceList: MutableList<DocumentHolder>) : Tab() {
    override val id = AppCoreManager.instance.generateUid()
    override val title: String = emptyString
    override val subtitle: String = emptyString
    override val header: String = emptyString


    var isLoading by mutableStateOf(false) // 是否是加载中
    var activeListState by mutableStateOf(LazyGridState())

    val selectedFiles = linkedMapOf<String, DocumentHolder>() // 选中的Tab
    var lastSelectedFileIndex = -1 // 最后选中的index

    var highlightedFiles = arrayListOf<String>()
    val fileOptionsDialog = FileOptionsDialog

    fun quickReloadFiles() {
        if (isLoading) return
    }
}