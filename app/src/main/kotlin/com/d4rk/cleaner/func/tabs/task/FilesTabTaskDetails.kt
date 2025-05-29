package com.d4rk.cleaner.func.tabs.task

import com.d4rk.cleaner.utils.extension.emptyString


class FilesTabTaskDetails(
    var task: FilesTabTask,
    var type: Int = FilesTabTask.TASK_NONE,
    var title: String = emptyString,
    var subtitle: String = emptyString,
    var info: String = emptyString,
    var progress: Float = -1f
)