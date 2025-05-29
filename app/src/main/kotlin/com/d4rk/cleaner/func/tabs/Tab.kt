package com.d4rk.cleaner.func.tabs

abstract class Tab {
    var isCreated = false

    abstract val id: Int
    abstract val title: String
    abstract val subtitle: String
    abstract val header: String

    open fun onTabRemoved() {}
    open fun onTabStopped() {}
    open fun onTabResumed() {}
    open fun onTabStarted() {
        isCreated = true
    }

    open fun onBackPressed(): Boolean = false

    fun requestHomeToolbarUpdate() {
//        CoroutineScope(Dispatchers.Main).launch {
//            globalClass.mainActivityManager.title = title
//            globalClass.mainActivityManager.subtitle = subtitle
//        }
    }
}