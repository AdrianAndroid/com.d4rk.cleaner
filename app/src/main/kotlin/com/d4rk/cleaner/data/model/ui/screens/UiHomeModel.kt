package com.d4rk.cleaner.data.model.ui.screens

import com.d4rk.cleaner.data.model.ui.memorymanager.StorageInfo
import com.d4rk.cleaner.func.holder.DocumentHolder
import java.io.File

data class UiHomeModel(
    val storageInfo : StorageInfo = StorageInfo(), // 存储信息
    var analyzeState : UiAnalyzeModel = UiAnalyzeModel(), //
    var daysFromLastScan : Int = 0, // 最近扫描天数
    var isRescanDialogVisible : Boolean = false, // 重新扫描对话框
    var displayProcessText : String = "点击开始扫描", // 显示进度文本
    val analyzedFiles : AnalyzeModel = AnalyzeModel(), // 已分析文件列表

    var showInternalStorage: Boolean = false,
)


data class AnalyzeModel(
    val totalDirCount: Long = 0L, // 总目录大小
    val totalFileCount: Long = 0L, // 总文件数量

    val emptyFolders: List<DocumentHolder> = emptyList(), // 空文件夹
    val emptyFiles: List<DocumentHolder> = emptyList(), // 空文件
    val genericFiles: List<DocumentHolder> = emptyList(), // 冗余文件
    val genericFilesSize: Long = 0L, // 冗余文件大小

    val archiveFiles: List<DocumentHolder> = emptyList(), // 压缩包
    val archiveFilesSize: Long = 0L, // 压缩包文件大小

    val apkFiles: List<DocumentHolder> = emptyList(), // 安装包
    val apkFilesSize: Long = 0L, // 安装包文件大小

    val imageFiles: List<DocumentHolder> = emptyList(), // 图片
    val imageFilesSize: Long = 0L, // 图片文件大小

    val audioFiles: List<DocumentHolder> = emptyList(), // 音频
    val audioFilesSize: Long = 0L, // 音频文件大小

    val videoFiles: List<DocumentHolder> = emptyList(), // 视频
    val videoFilesSize : Long = 0L, // 视频文件大小

    val windowsFiles: List<DocumentHolder> = emptyList(), // windows下执行文件
    val windowsFilesSize: Long = 0L, // windows下执行文件大小

    val officeFiles: List<DocumentHolder> = emptyList(), // 文档
    val officeFilesSize: Long = 0L, // 文档文件大小

    val fontFiles: List<DocumentHolder> = emptyList(), // 字体
    val fontFilesSize: Long = 0L, // 字体文件大小

    val otherFiles: List<DocumentHolder> = emptyList(), // 其他
    val otherFilesSize: Long = 0L, // 其他文件大小

    val bigFiles: List<DocumentHolder> = emptyList(), // 大文件
    val bigFilesSize: Long = 0L, // 大文件大小

    val newFiles: List<DocumentHolder> = emptyList(), // 新文件
    val newFilesSize: Long = 0L, // 新文件大小
)

data class UiAnalyzeModel(
    var isAnalyzeScreenVisible : Boolean = false ,
    var scannedFileList : List<File> = emptyList() ,
    var emptyFolderList : List<File> = emptyList() ,
    var areAllFilesSelected : Boolean = false ,
    var fileSelectionMap : Map<File , Boolean> = emptyMap() ,
    var selectedFilesCount : Int = 0 ,
    var groupedFiles : Map<String , List<File>> = emptyMap() ,
    var fileTypesData : FileTypesData = FileTypesData() ,
    var isDeleteForeverConfirmationDialogVisible : Boolean = false ,
    var isMoveToTrashConfirmationDialogVisible : Boolean = false ,
)

data class FileTypesData(
    var fileTypesTitles : List<String> = emptyList() ,
    var genericExtensions: List<String> = emptyList(),
    var apkExtensions : List<String> = emptyList() ,
    var imageExtensions : List<String> = emptyList() ,
    var videoExtensions : List<String> = emptyList() ,
    var audioExtensions : List<String> = emptyList() ,
    var archiveExtensions : List<String> = emptyList() ,
    var fontExtensions : List<String> = emptyList() ,
    var windowsExtensions : List<String> = emptyList() ,
    var officeExtensions : List<String> = emptyList() ,
    var otherExtensions : List<String> = emptyList() ,
)