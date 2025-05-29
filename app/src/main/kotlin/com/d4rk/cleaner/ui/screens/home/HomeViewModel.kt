package com.d4rk.cleaner.ui.screens.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.utils.helpers.logI
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.screens.home.repository.HomeRepository
import com.d4rk.cleaner.func.holder.DocumentHolder
import com.d4rk.cleaner.ui.viewmodel.BaseViewModel
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import com.d4rk.cleaner.utils.constants.cleaning.ExtensionsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HomeViewModel(application : Application) : BaseViewModel(application) {
    private val repository : HomeRepository = HomeRepository(dataStore = AppCoreManager.dataStore , application = application)
    private val _uiState : MutableStateFlow<UiHomeModel> = MutableStateFlow(UiHomeModel())
    val uiState : StateFlow<UiHomeModel> = _uiState

    init {
        getStorageInfo() // 存储空间
        getFileTypes() // 文件类型
        loadCleanedSpace() // 已清理空间
    }

    fun analyze() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val fileTypesData : FileTypesData = _uiState.value.analyzeState.fileTypesData
            val preferences : Map<String , Boolean> = repository.getPreferences()
            val knownExtensions : Set<String> = (fileTypesData.imageExtensions + fileTypesData.videoExtensions + fileTypesData.audioExtensions + fileTypesData.officeExtensions + fileTypesData.archiveExtensions + fileTypesData.apkExtensions + fileTypesData.fontExtensions + fileTypesData.windowsExtensions).toSet()

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
            val bigFilesSize: Long = 0L // 大文件大小

            val newFiles = mutableListOf<DocumentHolder>() // 新文件
            val newFilesSize: Long = 0L // 新文件大小

            val emptyFolders = mutableListOf<DocumentHolder>() // 空文件夹
            val emptyFiles = mutableListOf<DocumentHolder>() // 已扫描文件

            var totalDirectoryCount = 0L
            var totalFileCount = 0L

            fun addTypeFile(mutableFile: MutableList<DocumentHolder>, documentHolder: DocumentHolder) {
                mutableFile.add(documentHolder)
                if (preferences[ExtensionsConstants.BIG_FILE_EXTENSION] == true && documentHolder.isBigFile()) {
                    bigFiles.add(documentHolder)
                }
                if (preferences[ExtensionsConstants.NEW_FILE_EXTENSION] == true && documentHolder.isNewFile()) {
                    newFiles.add(documentHolder)
                }
            }

            repository.analyze { documentHolder: DocumentHolder ->
                val extension = documentHolder.extension()
                logI { "analyze --> size=${extension} ${documentHolder.fileName()}" }
                if (documentHolder.isDirectory()) {
                    totalDirectoryCount += 1
                } else {
                    totalFileCount += 1
                }
                when {
                    documentHolder.isEmptyDirectory -> if (preferences[ExtensionsConstants.EMPTY_FOLDERS] == true) addTypeFile(emptyFolders, documentHolder)
                    documentHolder.isEmptyFile -> if (preferences[ExtensionsConstants.EMPTY_FILE] == true) addTypeFile(emptyFiles, documentHolder)

                    extension in fileTypesData.imageExtensions -> if (preferences[ExtensionsConstants.IMAGE_EXTENSIONS] == true) {
                        addTypeFile(imageFiles, documentHolder)
                        imageFilesSize += documentHolder.fileSize()
                    }
                    extension in fileTypesData.videoExtensions -> if (preferences[ExtensionsConstants.VIDEO_EXTENSIONS] == true) {
                        addTypeFile(videoFiles, documentHolder)
                        videoFilesSize += documentHolder.fileSize()
                    }
                    extension in fileTypesData.audioExtensions -> if (preferences[ExtensionsConstants.AUDIO_EXTENSIONS] == true) {
                        addTypeFile(audioFiles, documentHolder)
                        audioFilesSize += documentHolder.fileSize()
                    }
                    extension in fileTypesData.officeExtensions -> if (preferences[ExtensionsConstants.OFFICE_EXTENSIONS] == true) {
                        addTypeFile(officeFiles, documentHolder)
                        officeFilesSize += documentHolder.fileSize()
                    }
                    extension in fileTypesData.archiveExtensions -> if (preferences[ExtensionsConstants.ARCHIVE_EXTENSIONS] == true) {
                        addTypeFile(archiveFiles, documentHolder)
                        archiveFilesSize += documentHolder.fileSize()
                    }
                    extension in fileTypesData.apkExtensions -> if (preferences[ExtensionsConstants.APK_EXTENSIONS] == true) {
                        addTypeFile(apkFiles, documentHolder)
                        apkFilesSize += documentHolder.fileSize()
                    }
                    extension in fileTypesData.fontExtensions -> if (preferences[ExtensionsConstants.FONT_EXTENSIONS] == true) {
                        addTypeFile(fontFiles, documentHolder)
                        fontFilesSize += documentHolder.fileSize()
                    }
                    extension in fileTypesData.windowsExtensions -> if (preferences[ExtensionsConstants.WINDOWS_EXTENSIONS] == true) {
                        addTypeFile(windowsFiles, documentHolder)
                        windowsFilesSize += documentHolder.fileSize()
                    }
                    extension in fileTypesData.genericExtensions -> if (preferences[ExtensionsConstants.GENERIC_EXTENSIONS] == true) {
                        addTypeFile(genericFiles, documentHolder)
                        genericFilesSize += documentHolder.fileSize()
                    }
                    else -> if (! knownExtensions.contains(extension) && preferences[ExtensionsConstants.OTHER_EXTENSIONS] == true) {
                        addTypeFile(otherFiles, documentHolder)
                        otherFilesSize += documentHolder.fileSize()
                    }
                }
                _uiState.update { state ->
                    state.copy(
                        displayProcessText = documentHolder.absolutePath(),
                        analyzedFiles = state.analyzedFiles.copy(
                            totalDirCount = totalDirectoryCount, // 总目录大小
                            totalFileCount = totalFileCount, // 总文件数量
                            emptyFolders = emptyFolders, // 空文件夹
                            emptyFiles = emptyFiles, // 空文件
                            genericFiles = genericFiles, // 冗余文件
                            genericFilesSize = genericFilesSize, // 冗余文件大小,

                            archiveFiles = archiveFiles, // 压缩包
                            archiveFilesSize = archiveFilesSize, // 压缩包文件大小,

                            apkFiles = apkFiles, // 安装包
                            apkFilesSize = apkFilesSize, // 安装包文件大小,

                            imageFiles = imageFiles, // 图片
                            imageFilesSize = imageFilesSize, // 图片文件大小,

                            audioFiles = audioFiles, // 音频
                            audioFilesSize = audioFilesSize, // 音频文件大小,

                            videoFiles = videoFiles, // 视频
                            videoFilesSize = videoFilesSize, // 视频文件大小,

                            windowsFiles = windowsFiles, // windows下执行文件
                            windowsFilesSize = windowsFilesSize, // windows下执行文件大小,

                            officeFiles = officeFiles, // 文档
                            officeFilesSize = officeFilesSize, // 文档文件大小,

                            fontFiles = fontFiles, // 字体
                            fontFilesSize = fontFilesSize, // 字体文件大小,

                            otherFiles = otherFiles, // 其他
                            otherFilesSize = otherFilesSize, // 其他文件大小,

                            bigFiles = bigFiles, // 大文件
                            bigFilesSize = bigFilesSize, // 大文件大小,

                            newFiles = newFiles, // 新文件
                            newFilesSize = newFilesSize, // 新文件大小,
                        )
                    )
                }
            }
        }
    }

    fun analyze2() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            showLoading()
            repository.analyzeFiles { result ->
                val (scannedFiles : List<File> , emptyFolders : List<File>) = result
                val currentFileTypesData : FileTypesData = _uiState.value.analyzeState.fileTypesData

                viewModelScope.launch(context = coroutineExceptionHandler + Dispatchers.IO) {
                    val prefs : Map<String , Boolean> = repository.getPreferences()
                    val groupedFiles : Map<String, List<File>> = withContext(Dispatchers.Default) {
                        computeGroupedFiles(scannedFiles = scannedFiles , emptyFolders = emptyFolders , fileTypesData = currentFileTypesData , preferences = prefs)
                    }
                    _uiState.update { state ->
                        state.copy(analyzeState = state.analyzeState.copy(scannedFileList = scannedFiles , emptyFolderList = emptyFolders , isAnalyzeScreenVisible = true , groupedFiles = groupedFiles))
                    }
                }
            }
            hideLoading()
        }
    }

    fun showInternal(showInternalStorage: Boolean) {
        _uiState.update { state ->
            state.copy(showInternalStorage = showInternalStorage)
         }
    }

    private fun computeGroupedFiles(
        scannedFiles : List<File> , emptyFolders : List<File> , fileTypesData : FileTypesData , preferences : Map<String , Boolean>
    ) : Map<String , List<File>> {
        val knownExtensions : Set<String> = (fileTypesData.imageExtensions + fileTypesData.videoExtensions + fileTypesData.audioExtensions + fileTypesData.officeExtensions + fileTypesData.archiveExtensions + fileTypesData.apkExtensions + fileTypesData.fontExtensions + fileTypesData.windowsExtensions).toSet()

        val filesMap : LinkedHashMap<String , MutableList<File>> = linkedMapOf()
        filesMap.putAll(fileTypesData.fileTypesTitles.associateWith { mutableListOf() })

        scannedFiles.forEach { file ->
            val extension : String = file.extension.lowercase()
            val category : String? = when (extension) {
                in fileTypesData.imageExtensions -> if (preferences[ExtensionsConstants.IMAGE_EXTENSIONS] == true) fileTypesData.fileTypesTitles[0] else null
                in fileTypesData.videoExtensions -> if (preferences[ExtensionsConstants.VIDEO_EXTENSIONS] == true) fileTypesData.fileTypesTitles[1] else null
                in fileTypesData.audioExtensions -> if (preferences[ExtensionsConstants.AUDIO_EXTENSIONS] == true) fileTypesData.fileTypesTitles[2] else null
                in fileTypesData.officeExtensions -> if (preferences[ExtensionsConstants.OFFICE_EXTENSIONS] == true) fileTypesData.fileTypesTitles[3] else null
                in fileTypesData.archiveExtensions -> if (preferences[ExtensionsConstants.ARCHIVE_EXTENSIONS] == true) fileTypesData.fileTypesTitles[4] else null
                in fileTypesData.apkExtensions -> if (preferences[ExtensionsConstants.APK_EXTENSIONS] == true) fileTypesData.fileTypesTitles[5] else null
                in fileTypesData.fontExtensions -> if (preferences[ExtensionsConstants.FONT_EXTENSIONS] == true) fileTypesData.fileTypesTitles[6] else null
                in fileTypesData.windowsExtensions -> if (preferences[ExtensionsConstants.WINDOWS_EXTENSIONS] == true) fileTypesData.fileTypesTitles[7] else null
                else -> if (! knownExtensions.contains(extension) && preferences[ExtensionsConstants.OTHER_EXTENSIONS] == true) fileTypesData.fileTypesTitles[9] else null
            }
            category?.let { filesMap[it]?.add(file) }
        }

        if (emptyFolders.isNotEmpty() && preferences[ExtensionsConstants.EMPTY_FOLDERS] == true) {
            filesMap[fileTypesData.fileTypesTitles[8]] = emptyFolders.toMutableList()
        }

        return filesMap.filter { it.value.isNotEmpty() }
    }

    fun onCloseAnalyzeComposable() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(
                    isAnalyzeScreenVisible = false,
                    scannedFileList = emptyList(),
                    emptyFolderList = emptyList(),
                    groupedFiles = emptyMap(),
                    fileSelectionMap = emptyMap(),
                    selectedFilesCount = 0,
                    areAllFilesSelected = false
                ))
            }
        }
    }

    fun onFileSelectionChange(file : File , isChecked : Boolean) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val updatedFileSelectionStates : Map<File , Boolean> = _uiState.value.analyzeState.fileSelectionMap + (file to isChecked)
            val visibleFiles : List<File> = _uiState.value.analyzeState.groupedFiles.values.flatten()
            val selectedVisibleCount : Int = updatedFileSelectionStates.filterKeys { it in visibleFiles }.count { it.value }

            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(fileSelectionMap = updatedFileSelectionStates , selectedFilesCount = selectedVisibleCount , areAllFilesSelected = selectedVisibleCount == visibleFiles.size && visibleFiles.isNotEmpty()))
            }
        }
    }

    fun toggleSelectAllFiles() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            val newState : Boolean = ! _uiState.value.analyzeState.areAllFilesSelected
            val visibleFiles : List<File> = _uiState.value.analyzeState.groupedFiles.values.flatten()

            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(areAllFilesSelected = newState , fileSelectionMap = if (newState) visibleFiles.associateWith { true } else emptyMap() , selectedFilesCount = if (newState) visibleFiles.size else 0))
            }
        }
    }

    fun toggleSelectFilesForCategory(category : String) {
        viewModelScope.launch(context = Dispatchers.Default + coroutineExceptionHandler) {
            val currentState : UiHomeModel = _uiState.value
            val filesInCategory : List<File> = currentState.analyzeState.groupedFiles[category] ?: emptyList()
            val currentSelectionMap : Map<File , Boolean> = currentState.analyzeState.fileSelectionMap
            val allSelected : Boolean = filesInCategory.all { currentSelectionMap[it] == true }
            val updatedSelectionMap : MutableMap<File , Boolean> = currentSelectionMap.toMutableMap().apply {
                filesInCategory.forEach { file ->
                    this[file] = ! allSelected
                }
            }

            val selectedVisibleCount : Int = updatedSelectionMap.filterKeys { it in currentState.analyzeState.groupedFiles.values.flatten() }.count { it.value }

            _uiState.update { state ->
                state.copy(
                    analyzeState = state.analyzeState.copy(
                        fileSelectionMap = updatedSelectionMap , selectedFilesCount = selectedVisibleCount , areAllFilesSelected = selectedVisibleCount == currentState.analyzeState.groupedFiles.values.flatten().size
                    )
                )
            }
        }
    }

    fun clean() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            showLoading()
            val filesToDelete : Set<File> = _uiState.value.analyzeState.fileSelectionMap.filter { it.value }.keys
            val clearedSpaceTotalSize : Long = filesToDelete.sumOf { it.length() }
            with(repository) {
                deleteFilesRepository(filesToDelete = filesToDelete) {
                    _uiState.update { state ->
                        state.copy(analyzeState = state.analyzeState.copy(scannedFileList = state.analyzeState.scannedFileList.filterNot { filesToDelete.contains(it) } , selectedFilesCount = 0 , areAllFilesSelected = false , fileSelectionMap = emptyMap() , isAnalyzeScreenVisible = false))
                    }
                    getStorageInfo()
                }
                with(dataStore) {
                    addCleanedSpace(space = clearedSpaceTotalSize)
                    saveLastScanTimestamp(timestamp = System.currentTimeMillis())
                }
            }
            hideLoading()
        }
    }

    fun moveToTrash() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            showLoading()
            val filesToMove : List<File> = _uiState.value.analyzeState.fileSelectionMap.filter { it.value }.keys.toList()
            val totalFileSizeToMove : Long = filesToMove.sumOf { it.length() }
            with(repository) {
                moveToTrashRepository(filesToMove = filesToMove) {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(analyzeState = currentUiState.analyzeState.copy(scannedFileList = currentUiState.analyzeState.scannedFileList.filterNot { existingFile ->
                            filesToMove.any { movedFile ->
                                existingFile.absolutePath == movedFile.absolutePath
                            }
                        } , selectedFilesCount = 0 , areAllFilesSelected = false , isAnalyzeScreenVisible = false , fileSelectionMap = emptyMap()))
                    }
                    getStorageInfo()
                }
                addTrashSize(size = totalFileSizeToMove)
            }
            hideLoading()
        }
    }

    private fun getFileTypes() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.getFileTypesRepository { fileTypesData ->
                _uiState.update { state ->
                    state.copy(analyzeState = state.analyzeState.copy(fileTypesData = fileTypesData))
                }
            }
        }
    }

    fun setDeleteForeverConfirmationDialogVisibility(isVisible : Boolean) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(isDeleteForeverConfirmationDialogVisible = isVisible))
            }
        }
    }

    fun setMoveToTrashConfirmationDialogVisibility(isVisible : Boolean) {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            _uiState.update { state ->
                state.copy(analyzeState = state.analyzeState.copy(isMoveToTrashConfirmationDialogVisible = isVisible))
            }
        }
    }

    private fun getStorageInfo() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            repository.getStorageInfoRepository { uiHomeModel ->
                _uiState.update { state ->
                    state.copy(storageInfo = state.storageInfo.copy(storageUsageProgress = uiHomeModel.storageInfo.storageUsageProgress , freeSpacePercentage = uiHomeModel.storageInfo.freeSpacePercentage))
                }
            }
        }
    }

    private fun loadCleanedSpace() {
        viewModelScope.launch(context = coroutineExceptionHandler) {
            with(repository) {
                dataStore.cleanedSpace.collect { cleanedSpace ->
                    _uiState.update { state ->
                        state.copy(storageInfo = state.storageInfo.copy(cleanedSpace = StorageUtils.formatSize(cleanedSpace)))
                    }
                }
            }
        }
    }
}