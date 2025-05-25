package com.d4rk.cleaner.ui.screens.home.repository

import com.d4rk.cleaner.utils.helpers.FileSizeHelper
import java.io.File

data class WrapFile(
    val file: File,
) {

    fun fileSize() : Long {
        return FileSizeHelper.getFileSize(file)
    }

    fun extension(): String {
        return file.extension
    }

    fun absolutePath(): String {
        return file.absolutePath
    }

    fun isDirectory(): Boolean {
        return file.isDirectory
    }

    fun isEmptyFile(): Boolean {
        return file.isDirectory.not() && file.length() == 0L
    }

    fun isEmptyDirectory(): Boolean {
        return file.isDirectory && file.listFiles().isNullOrEmpty()
    }

    fun isBigFile(): Boolean {
        return FileSizeHelper.getFileSize(file) > 1024 * 1024 * 10
    }

    fun isNewFile(): Boolean {
        return file.lastModified() > System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30
    }
}
