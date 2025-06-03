package com.d4rk.cleaner.ui.screens.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.d4rk.cleaner.apptoolkit.utils.helpers.logI
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.data.model.ui.screens.FileTypesData
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.screens.home.repository.HomeRepository
import com.d4rk.cleaner.func.holder.DocumentHolder
import com.d4rk.cleaner.ui.screens.main.MainActivityManager
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
            if (isLoading.value) {
                return@launch
            }
            val mainActivityManager: MainActivityManager = AppCoreManager.instance.mainActivityManager
            mainActivityManager.clearAnalyzeBefore()
            repository.analyze(
                onStart = { showLoading() },
                onProgress = { documentHolder: DocumentHolder ->
                    mainActivityManager.analyzeCleanFile(documentHolder = documentHolder)
                    _uiState.update { state ->
                        state.copy(
                            displayProcessText = documentHolder.absolutePath(),
                        )
                    }
                },
                onEnd = {
                    hideLoading()
                    _uiState.update { state ->
                        state.copy(
                            displayProcessText = "分析完成",
                        )
                    }
                }
            )
        }
    }

    fun showInternal(showInternalStorage: Boolean) {
        if (_uiState.value.showInternalStorage != showInternalStorage) {
            _uiState.update { state ->
                state.copy(showInternalStorage = showInternalStorage)
            }
        }
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