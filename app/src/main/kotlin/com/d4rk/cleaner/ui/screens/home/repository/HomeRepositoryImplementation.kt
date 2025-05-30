package com.d4rk.cleaner.ui.screens.home.repository

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.hasParent
import com.anggrayudi.storage.file.toRawFile
import com.d4rk.android.libs.apptoolkit.utils.helpers.logI
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.data.model.ui.memorymanager.StorageInfo
import com.d4rk.cleaner.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.func.holder.DocumentHolder
import com.d4rk.cleaner.ui.screens.main.DirInfo
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import com.raival.compose.file.explorer.screen.main.tab.files.provider.StorageProvider
import kotlinx.coroutines.flow.first
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class HomeRepositoryImplementation(val application : Application , val dataStore : DataStore) {
    private val trashDir : File = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash")

    suspend fun getStorageInfoImplementation() : UiHomeModel {
        return suspendCoroutine { continuation ->
            StorageUtils.getStorageInfo(context = application) { _ , _ , _ , usageProgress , freeSpacePercentage ->
                continuation.resume(
                    UiHomeModel(storageInfo = StorageInfo(storageUsageProgress = usageProgress , freeSpacePercentage = freeSpacePercentage))
                )
            }
        }
    }

    /**
     * 遍历根目录下的所有文件
     */
    fun iterateFiles(onFile: (DocumentHolder) -> Unit) {
        val mainActivityManager = AppCoreManager.instance.mainActivityManager
        val stack: ArrayDeque<File> = ArrayDeque() // 栈，存储文件和其大小
        val root: File = Environment.getExternalStorageDirectory() // 根目录
        stack.addFirst(root)
        
        // 用于临时存储文件夹大小的映射
        val dirSizes: MutableMap<String, DirInfo> = mainActivityManager.dirSizes

        fun refreshDirInfo(docFileHolder: DocumentHolder, fileSize: Long, filesCount: Int, dirsCount: Int) {
            var currentFileHolder: DocumentHolder = docFileHolder
            while (currentFileHolder.path != root.absolutePath) {
                val path: String = currentFileHolder.path
                val dirInfo = dirSizes[path] ?: DirInfo()
                dirInfo.filesCount += filesCount
                dirInfo.dirsCount += dirsCount
                dirInfo.totalSize += fileSize
                dirSizes[path] = dirInfo
                currentFileHolder = currentFileHolder.parent ?: break
            }
            // 更新根目录信息
            val rootInfo = dirSizes[root.absolutePath] ?: DirInfo()
            rootInfo.filesCount += filesCount
            rootInfo.dirsCount += dirsCount
            rootInfo.totalSize += fileSize
            dirSizes[root.absolutePath] = rootInfo
        }
        
        while (stack.isNotEmpty()) {
            val currentFile = stack.removeFirst()
            var currentSize = 0L
            var filesCount = 0
            var dirsCount = 0
            val documentHolder = DocumentHolder(DocumentFile.fromFile(currentFile))
            
            if (currentFile.isDirectory) {
                currentFile.listFiles()?.let { children ->
                    if (children.isEmpty()) {
                        onFile(documentHolder)
                    } else {
                        children.forEach { child ->
                            if (child.isDirectory) {
                                stack.addLast(child)
                                ++dirsCount
                            } else {
                                val fileSize = child.length()
                                currentSize += fileSize
                                ++filesCount
                                onFile(DocumentHolder(DocumentFile.fromFile(child)))
                            }
                        }
                        refreshDirInfo(documentHolder, currentSize, filesCount, dirsCount)
                        // 回调当前文件夹的总大小
                        onFile(documentHolder)
                    }
                }
//                ++filesCount
            } else {
//                ++dirsCount
                onFile(documentHolder)
            }
        }
        logI {
            "iterateFiles done!"
        }
    }

//    fun iterateFiles2(onFile: (DocumentHolder, Long) -> Unit) {
//        val stack: ArrayDeque<Pair<File, Long>> = ArrayDeque() // 栈，存储文件和其大小
//        val root: File = Environment.getExternalStorageDirectory() // 根目录
//        stack.addFirst(Pair(root, 0L))
//
//        // 用于临时存储文件夹大小的映射
//        val dirSizes = mutableMapOf<String, Long>()
//
//        while (stack.isNotEmpty()) {
//            val (currentFile, parentSize) = stack.removeFirst()
//            var currentSize = 0L
//            val documentHolder = DocumentHolder(DocumentFile.fromFile(currentFile))
//
//            if (currentFile.isDirectory) {
//                currentFile.listFiles()?.let { children ->
//                    if (children.isEmpty()) {
//                        onFile(documentHolder, parentSize)
//                    } else {
//                        children.forEach { child ->
//                            if (child.isDirectory) {
//                                stack.addLast(Pair(child, currentSize))
//                            } else {
//                                val fileSize = child.length()
//                                currentSize += fileSize
//                                onFile(DocumentHolder(DocumentFile.fromFile(child)), fileSize)
//                            }
//                        }
//                        // 存储当前文件夹的大小
//                        dirSizes[currentFile.absolutePath] = currentSize
//                        // 更新父文件夹的大小
//                        currentFile.parentFile?.let { parent ->
//                            dirSizes[parent.absolutePath] = (dirSizes[parent.absolutePath] ?: 0L) + currentSize
//                        }
//                        // 回调当前文件夹的总大小
//                        onFile(documentHolder, currentSize)
//                    }
//                }
//            } else {
//                val fileSize = currentFile.length()
//                onFile(documentHolder, fileSize)
//            }
//        }
//    }


    fun getAllFilesImplementation() : Pair<List<File> , List<File>> {
        val files : MutableList<File> = mutableListOf() // 保存所有的文件
        val emptyFolders : MutableList<File> = mutableListOf() // 空文件夹
        val stack : ArrayDeque<File> = ArrayDeque() // 栈
        val root : File = Environment.getExternalStorageDirectory() // 根目录
        stack.addFirst(element = root)

        val trashDir = File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) , "Trash") // 垃圾篓

        while (stack.isNotEmpty()) {
            val currentFile : File = stack.removeFirst()
            if (currentFile.isDirectory) {
                if (! currentFile.absolutePath.startsWith(trashDir.absolutePath)) {
                    currentFile.listFiles()?.let { children ->
                        if (children.isEmpty()) {
                            emptyFolders.add(currentFile)
                        }
                        else {
                            children.forEach { child ->
                                if (child.isDirectory) {
                                    stack.addLast(child)
                                }
                                else {
                                    files.add(child)
                                }
                            }
                        }
                    }
                }
            } else {
                files.add(currentFile)
            }
        }
        return Pair(files , emptyFolders)
    }

    suspend fun getFileTypesImplementation() : FileTypesData {
        return suspendCoroutine { continuation ->
            val apkExtensions : List<String> = application.resources.getStringArray(R.array.apk_extensions).toList()
            val imageExtensions : List<String> = application.resources.getStringArray(R.array.image_extensions).toList()
            val videoExtensions : List<String> = application.resources.getStringArray(R.array.video_extensions).toList()
            val audioExtensions : List<String> = application.resources.getStringArray(R.array.audio_extensions).toList()
            val fontExtensions : List<String> = application.resources.getStringArray(R.array.font_extensions).toList()
            val windowsExtensions : List<String> = application.resources.getStringArray(R.array.windows_extensions).toList()
            val archiveExtensions : List<String> = application.resources.getStringArray(R.array.archive_extensions).toList()
            val officeExtensions : List<String> = application.resources.getStringArray(R.array.microsoft_office_extensions).toList()
            val genericExtensions : List<String> = application.resources.getStringArray(R.array.generic_extensions).toList()
            val fileTypesTitles : List<String> = application.resources.getStringArray(R.array.file_types_titles).toList()

            val knownExtensions = mutableSetOf<String>().apply {
                addAll(elements = apkExtensions.map { it.lowercase() })
                addAll(elements = imageExtensions.map { it.lowercase() })
                addAll(elements = videoExtensions.map { it.lowercase() })
                addAll(elements = audioExtensions.map { it.lowercase() })
                addAll(elements = fontExtensions.map { it.lowercase() })
                addAll(elements = windowsExtensions.map { it.lowercase() })
                addAll(elements = archiveExtensions.map { it.lowercase() })
                addAll(elements = officeExtensions.map { it.lowercase() })
                addAll(elements = genericExtensions.map { it.lowercase() })
            }

            val allFoundExtensions : MutableSet<String> = mutableSetOf()
//            fun scanDir(dir : File) {
//                dir.listFiles()?.forEach { file ->
//                    if (file.isDirectory) {
//                        scanDir(file)
//                    }
//                    else {
//                        val ext : String = file.extension.lowercase()
//                        if (ext.isNotEmpty()) {
//                            allFoundExtensions.add(element = ext)
//                        }
//                    }
//                }
//            }
//            scanDir(Environment.getExternalStorageDirectory())

            val otherExtensions : List<String> = (allFoundExtensions - knownExtensions).toList().sorted()

            val fileTypesData = FileTypesData(
                genericExtensions = genericExtensions,
                apkExtensions = apkExtensions ,
                imageExtensions = imageExtensions ,
                videoExtensions = videoExtensions ,
                audioExtensions = audioExtensions ,
                archiveExtensions = archiveExtensions ,
                fileTypesTitles = fileTypesTitles ,
                fontExtensions = fontExtensions ,
                windowsExtensions = windowsExtensions ,
                officeExtensions = officeExtensions ,
                otherExtensions = otherExtensions
            )
            continuation.resume(value = fileTypesData)
        }
    }

    suspend fun deleteFilesImplementation(filesToDelete : Set<File>) {
        val shouldClearClipboard : Boolean = dataStore.clipboardClean.first()

        filesToDelete.forEach { file ->
            if (file.exists()) {
                file.deleteRecursively()
            }
        }

        if (shouldClearClipboard) {
            clearClipboardImplementation()
        }
    }

    private fun clearClipboardImplementation() {
        val clipboardManager : ClipboardManager = application.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return

        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                clipboardManager.clearPrimaryClip()
            }
            else {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("" , ""))
            }
        }
    }

    suspend fun moveToTrashImplementation(filesToMove : List<File>) {
        if (! trashDir.exists()) {
            trashDir.mkdirs()
        }

        filesToMove.forEach { file ->
            if (file.exists()) {
                val originalPath : String = file.absolutePath
                val destination = File(trashDir , file.name)

                if (file.renameTo(destination)) {
                    dataStore.addTrashFileOriginalPath(originalPath = originalPath)
                    dataStore.addTrashFilePath(pathPair = originalPath to destination.absolutePath)
                    MediaScannerConnection.scanFile(
                        application , arrayOf(destination.absolutePath , file.absolutePath) , null , null
                    )
                }
            }
        }
    }

    suspend fun restoreFromTrashImplementation(filesToRestore : Set<File>) {
        val originalPaths : Set<String> = dataStore.trashFileOriginalPaths.first()
        filesToRestore.forEach { file ->
            if (file.exists()) {
                val originalPath : String? = originalPaths.firstOrNull { File(it).name == file.name }
                if (originalPath != null) {
                    val destinationFile = File(originalPath)
                    val destinationParent : File? = destinationFile.parentFile

                    if (destinationParent?.exists() == false) {
                        destinationParent.mkdirs()
                    }

                    if (file.renameTo(destinationFile)) {
                        dataStore.removeTrashFileOriginalPath(originalPath = originalPath)
                        dataStore.removeTrashFilePath(originalPath = originalPath)
                        MediaScannerConnection.scanFile(
                            application , arrayOf(destinationFile.absolutePath , file.absolutePath) , null , null
                        )
                    }
                }
                else {
                    val downloadsDir : File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val destinationFile = File(downloadsDir , file.name)

                    if (file.renameTo(destinationFile)) {
                        MediaScannerConnection.scanFile(
                            application , arrayOf(destinationFile.absolutePath , file.absolutePath) , null , null
                        )
                    }
                }
            }
        }
    }
}