package com.d4rk.cleaner.func.tabs

import androidx.compose.runtime.mutableStateListOf
import com.d4rk.cleaner.func.tabs.task.FilesTabTask

class FilesTabManager {
    val filesTabTasks = mutableStateListOf<FilesTabTask>()

//    var bookmarks by prefMutableState(
//        keyName = "bookmarks",
//        defaultValue = emptySet(),
//        getPreferencesKey = { stringSetPreferencesKey(it) }
//    )
}