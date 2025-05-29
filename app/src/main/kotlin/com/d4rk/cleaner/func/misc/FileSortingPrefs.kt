package com.d4rk.cleaner.func.misc


data class FileSortingPrefs(
    val sortMethod: Int = SortingMethod.SORT_BY_NAME,
    val showFoldersFirst: Boolean = true,
    val reverseSorting: Boolean = false,
    val applyForThisFileOnly: Boolean = false
)