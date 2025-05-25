package com.d4rk.cleaner.ui.screens.home.repository

import java.io.File

data class WrapFile(
    val file: File,
) {

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
}
