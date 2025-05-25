package com.d4rk.cleaner.data.model.ui.screens

import com.d4rk.cleaner.data.model.ui.memorymanager.StorageInfo
import com.d4rk.cleaner.ui.screens.home.repository.WrapFile
import java.io.File

data class UiHomeModel(
    val storageInfo : StorageInfo = StorageInfo() , // 存储信息
    var analyzeState : UiAnalyzeModel = UiAnalyzeModel() , //
    var daysFromLastScan : Int = 0 , // 最近扫描天数
    var isRescanDialogVisible : Boolean = false , // 重新扫描对话框
    val analyzedFiles : AnalyzeModel = AnalyzeModel(), // 已分析文件列表
)


data class AnalyzeModel(
    val totalDirCount: Long = 0L, // 总目录大小
    val totalFileCount: Long = 0L, // 总文件数量

    val genericFiles: List<WrapFile> = emptyList(), // 冗余文件
    val archiveFiles: List<WrapFile> = emptyList(), // 压缩包
    val apkFiles: List<WrapFile> = emptyList(), // 安装包
    val imageFiles: List<WrapFile> = emptyList(), // 图片
    val audioFiles: List<WrapFile> = emptyList(), // 音频
    val videoFiles: List<WrapFile> = emptyList(), // 视频
    val windowsFiles: List<WrapFile> = emptyList(), // windows下执行文件
    val officeFiles: List<WrapFile> = emptyList(), // 文档
    val fontFiles: List<WrapFile> = emptyList(), // 字体
    val otherFiles: List<WrapFile> = emptyList(), // 其他
    val bigFiles: List<WrapFile> = emptyList(), // 大文件
    val newFiles: List<WrapFile> = emptyList(), // 新文件
    //    const val GENERIC_EXTENSIONS = "generic_extensions" // 冗余文件
    //    const val ARCHIVE_EXTENSIONS = "archive_extensions" // 压缩包
    //    const val APK_EXTENSIONS = "apk_extensions" // 安装包
    //    const val EMPTY_FOLDERS = "empty_folders" // 空文件夹
    //    const val IMAGE_EXTENSIONS = "image_extensions" // 图片
    //    const val AUDIO_EXTENSIONS = "audio_extensions" // 音频
    //    const val VIDEO_EXTENSIONS = "video_extensions" // 视频
    //    const val WINDOWS_EXTENSIONS = "windows_extensions" // windows下执行文件
    //    const val OFFICE_EXTENSIONS = "office_extensions" // 文档
    //    const val FONT_EXTENSIONS = "font_extensions" // 字体
    //    const val OTHER_EXTENSIONS = "other_extensions"
    //
    //    const val BIG_FILE_EXTENSION = "big_file_extension" // 大文件
    //    const val NEW_FILE_EXTENSION = "new_file_extension" // 新文件
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