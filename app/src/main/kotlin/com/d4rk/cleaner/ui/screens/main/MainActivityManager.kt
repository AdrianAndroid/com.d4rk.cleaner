package com.d4rk.cleaner.ui.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.d4rk.cleaner.data.core.AppCoreManager
import androidx.compose.runtime.setValue
import com.d4rk.cleaner.apptoolkit.utils.helpers.logI
import com.d4rk.cleaner.func.holder.DocumentHolder
import com.d4rk.cleaner.func.misc.FileMimeType
import com.d4rk.cleaner.func.tabs.Tab
import com.d4rk.cleaner.utils.extension.emptyString

class MainActivityManager {
    val globalClass = AppCoreManager.instance
    var title by mutableStateOf(globalClass.getString(com.d4rk.cleaner.R.string.app_name))
    var subtitle by mutableStateOf(emptyString)

    // 文件扫描结果管理
    var totalDirectoryCount = 0L // 文件数量
    var totalFileCount = 0L // 文件大小
    var totalFileSize = 0L

    val imageFiles = mutableListOf<DocumentHolder>() // 图片
    var imageFilesSize: Long = 0L // 图片文件大小
    val videoFiles = mutableListOf<DocumentHolder>() // 视频
    var videoFilesSize: Long = 0L // 视频文件大小
    val genericFiles = mutableListOf<DocumentHolder>() // 冗余文件
    var genericFilesSize: Long = 0L // 冗余文件大小
    val archiveFiles = mutableListOf<DocumentHolder>() // 压缩包
    var archiveFilesSize: Long = 0L // 压缩包文件大小
    val apkFiles = mutableListOf<DocumentHolder>() // 安装包
    var apkFilesSize: Long = 0L // 安装包文件大小
    val audioFiles = mutableListOf<DocumentHolder>() // 音频
    var audioFilesSize: Long = 0L // 音频文件大小
    val windowsFiles = mutableListOf<DocumentHolder>() // windows下执行文件
    var windowsFilesSize: Long = 0L // windows下执行文件大小
    val officeFiles = mutableListOf<DocumentHolder>() // 文档
    var officeFilesSize: Long = 0L // 文档文件大小
    val fontFiles: MutableList<DocumentHolder> = mutableListOf<DocumentHolder>() // 字体
    var fontFilesSize: Long = 0L // 字体文件大小
    val otherFiles = mutableListOf<DocumentHolder>() // 其他
    var otherFilesSize: Long = 0L // 其他文件大小
    val bigFiles = mutableListOf<DocumentHolder>() // 大文件
    var bigFilesSize: Long = 0L // 大文件大小
    val newFiles = mutableListOf<DocumentHolder>() // 新文件
    var newFilesSize: Long = 0L // 新文件大小
    val emptyFolders = mutableListOf<DocumentHolder>() // 空文件夹
    val emptyFiles = mutableListOf<DocumentHolder>() // 已扫描文件

    val dirSizes = mutableMapOf<String, DirInfo>() // 所有文件夹的容量大小

    fun clearAnalyzeBefore() {
        imageFiles.clear()
        videoFiles.clear()
        genericFiles.clear()
        archiveFiles.clear()
        apkFiles.clear()
        audioFiles.clear()
        windowsFiles.clear()
        officeFiles.clear()
        fontFiles.clear()
        otherFiles.clear()
        bigFiles.clear()
        newFiles.clear()
        emptyFolders.clear()
        emptyFiles.clear()

        imageFilesSize = 0L
        videoFilesSize = 0L
        genericFilesSize = 0L
        archiveFilesSize = 0L
        apkFilesSize = 0L
        audioFilesSize = 0L
        windowsFilesSize = 0L
        officeFilesSize = 0L
        fontFilesSize = 0L
        otherFilesSize = 0L
        bigFilesSize = 0L
        newFilesSize = 0L
        totalDirectoryCount = 0L
        totalFileCount = 0L

        dirSizes.clear()
    }

    private fun addTypeFile(mutableFile: MutableList<DocumentHolder>, documentHolder: DocumentHolder) {
        mutableFile.add(documentHolder)
        if (documentHolder.isBigFile()) {
            bigFiles.add(documentHolder)
            bigFilesSize += documentHolder.fileSize
        }
        if (documentHolder.isNewFile()) {
            newFiles.add(documentHolder)
            newFilesSize += documentHolder.fileSize
        }
    }

    fun analyzeCleanFile(documentHolder: DocumentHolder) {
        val extension = documentHolder.extension()
//        logI { "analyze --> size=${extension} ${documentHolder.fileName()}" }
        if (documentHolder.isDirectory()) {
            totalDirectoryCount += 1
        } else {
            totalFileCount += 1
        }
        when {
            documentHolder.isEmptyDirectory -> addTypeFile(emptyFolders, documentHolder)
            documentHolder.isEmptyFile -> addTypeFile(emptyFiles, documentHolder)

            extension in FileMimeType.imageExtensions -> {
                addTypeFile(imageFiles, documentHolder)
                imageFilesSize += documentHolder.fileSize
                totalFileSize += documentHolder.fileSize
            }
            extension in FileMimeType.videoExtensions -> {
                addTypeFile(videoFiles, documentHolder)
                videoFilesSize += documentHolder.fileSize
                totalFileSize += documentHolder.fileSize
            }
            extension in FileMimeType.audioExtensions -> {
                addTypeFile(audioFiles, documentHolder)
                audioFilesSize += documentHolder.fileSize
                totalFileSize += documentHolder.fileSize
            }
            extension in FileMimeType.officeExtensions ->  {
                addTypeFile(officeFiles, documentHolder)
                officeFilesSize += documentHolder.fileSize
                totalFileSize += documentHolder.fileSize
            }
            extension in FileMimeType.archiveExtensions ->  {
                addTypeFile(archiveFiles, documentHolder)
                archiveFilesSize += documentHolder.fileSize
                totalFileSize += documentHolder.fileSize
            }
            extension in FileMimeType.apkExtensions -> {
                addTypeFile(apkFiles, documentHolder)
                apkFilesSize += documentHolder.fileSize
                totalFileSize += documentHolder.fileSize
            }
            extension in FileMimeType.fontExtensions -> {
                addTypeFile(fontFiles, documentHolder)
                fontFilesSize += documentHolder.fileSize
                totalFileSize += documentHolder.fileSize
            }
            extension in FileMimeType.windowsExtensions ->  {
                addTypeFile(windowsFiles, documentHolder)
                windowsFilesSize += documentHolder.fileSize
                totalFileSize += documentHolder.fileSize
            }
            extension in FileMimeType.genericExtensions -> {
                addTypeFile(genericFiles, documentHolder)
                genericFilesSize += documentHolder.fileSize
                totalFileSize += documentHolder.fileSize
            }
            else -> if (!FileMimeType.knownExtensions.contains(extension)){
                addTypeFile(otherFiles, documentHolder)
                otherFilesSize += documentHolder.fileSize
                totalFileSize += documentHolder.fileSize
            }
        }
    }


    // tab显示

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

data class DirInfo(
    var totalSize: Long = 0L,
    var filesCount: Int = 0,
    var dirsCount: Int = 0,
)