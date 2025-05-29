package com.d4rk.cleaner.func.tabs

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.AssetManager
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider.getUriForFile
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.func.holder.DocumentHolder
import com.d4rk.cleaner.func.misc.Action
import com.d4rk.cleaner.func.misc.FileMimeType
import com.d4rk.cleaner.func.misc.SortingMethod
import com.d4rk.cleaner.func.misc.UpdateAction
import com.d4rk.cleaner.func.misc.sortFoldersFirst
import com.d4rk.cleaner.func.misc.sortLargerFirst
import com.d4rk.cleaner.func.misc.sortName
import com.d4rk.cleaner.func.misc.sortNameRev
import com.d4rk.cleaner.func.misc.sortNewerFirst
import com.d4rk.cleaner.func.misc.sortOlderFirst
import com.d4rk.cleaner.func.misc.sortSmallerFirst
import com.d4rk.cleaner.func.tabs.task.CompressTask
import com.d4rk.cleaner.func.tabs.task.DeleteTask
import com.d4rk.cleaner.func.tabs.task.FilesTabTaskCallback
import com.d4rk.cleaner.func.tabs.task.FilesTabTaskDetails
import com.d4rk.cleaner.ui.screens.main.MainActivity
import com.d4rk.cleaner.utils.extension.addIfAbsent
import com.d4rk.cleaner.utils.extension.emptyString
import com.d4rk.cleaner.utils.extension.getIndexIf
import com.d4rk.cleaner.utils.extension.isNot
import com.d4rk.cleaner.utils.extension.orIf
import com.d4rk.cleaner.utils.extension.removeIf
import com.raival.compose.file.explorer.screen.main.tab.files.provider.StorageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilesTab(val source: DocumentHolder) : Tab() {
    private val globalClass: AppCoreManager by lazy { AppCoreManager.instance }
    override val id = AppCoreManager.instance.generateUid()

    companion object {
        fun isValidPath(path: String) = DocumentHolder.fromFullPath(path) isNot  null
    }

    val search = Search
    val taskDialog = TaskDialog
    val apkDialog = ApkDialog
    val compressDialog = CompressDialog
    val renameDialog = RenameDialog
    val fileOptionsDialog = FileOptionsDialog
    val openWithDialog = OpenWithDialog

    val homeDir: DocumentHolder =
        if (isSpecialDirectory(source) || source.isFolder) source else source.parent
            ?: StorageProvider.getPrimaryInternalStorage(globalClass).documentHolder
    var activeFolder: DocumentHolder = homeDir // 当前显示的文件夹

    val actions = arrayListOf<Action>()

    val activeFolderContent = mutableStateListOf<DocumentHolder>()
    val contentListStates = hashMapOf<String, LazyGridState>()
    var activeListState by mutableStateOf(LazyGridState())

    val currentPathSegments = mutableStateListOf<DocumentHolder>()
    val currentPathSegmentsListState = LazyListState()

    val assetManager: AssetManager = globalClass.assets

    val selectedFiles = linkedMapOf<String, DocumentHolder>()
    var lastSelectedFileIndex = -1

    var highlightedFiles = arrayListOf<String>()

    var showSortingMenu by mutableStateOf(false)
    var showCreateNewFileDialog by mutableStateOf(false)
    var showConfirmDeleteDialog by mutableStateOf(false)
    var showFileProperties by mutableStateOf(false)
    var showTasksPanel by mutableStateOf(false)
    var showSearchPenal by mutableStateOf(false)
    var showBookmarkDialog by mutableStateOf(false)
    var showMoreOptionsButton by mutableStateOf(false)
    var showEmptyRecycleBin by mutableStateOf(false)
    var handleBackGesture by mutableStateOf(activeFolder.canAccessParent || selectedFiles.isNotEmpty())
    var tabViewLabel by mutableStateOf(emptyString)

    var isLoading by mutableStateOf(false)

    var foldersCount = 0
    var filesCount = 0

    override fun onTabStarted() {
        super.onTabStarted()
        if (source.isFile) {
            source.parent?.let { parent ->
                if (parent.exists()) {
                    openFolder(parent) {
                        CoroutineScope(Dispatchers.Main).launch {
                            getFileListState().scrollToItem(
                                activeFolderContent.getIndexIf { path == source.path },
                                0
                            )
                        }
                    }
                }
            } ?: also {
                openFolder(homeDir)
            }

            highlightedFiles.apply {
                clear()
                add(source.path)
            }
        } else {
            openFolder(homeDir)
        }
    }

    override fun onTabResumed() {
        requestHomeToolbarUpdate()

        actions.forEach { action ->
            if (action is UpdateAction) {
                reloadFiles()
                if (action.autoDismiss) {
                    action.due = false
                    action.isDone = true
                }
            }

            actions.removeIf { it.isDone }
        }
    }

    override fun onTabStopped() {
        super.onTabStopped()
    }

    override val title: String
        get() = createTitle()

    override val subtitle: String
        get() = createSubtitle()

    override val header: String
        get() = tabViewLabel

//    init {
//        if (source.isFile) {
//            globalClass.let { openFile(context, source) }
//        }
//    }

    private fun createSubtitle(): String {
        var selectedFolders = 0
        var selectedFiles = 0

        this.selectedFiles.values.forEach {
            if (it.isFolder) selectedFolders++
            else selectedFiles++
        }

        return buildString {
            if (foldersCount + filesCount == 0) {
                append(globalClass.getString(R.string.empty_folder))
                return@buildString
            }

            if (foldersCount > 0) {
                append(activeFolder.getFormattedFileCount(0, foldersCount))
                if (selectedFolders > 0) {
                    append(globalClass.getString(R.string.files_selected).format(selectedFolders))
                }
            }

            if (filesCount > 0 && foldersCount > 0) append(" | ")

            if (filesCount > 0) {
                append(activeFolder.getFormattedFileCount(filesCount, 0))
                if (selectedFiles > 0) {
                    append(globalClass.getString(R.string.files_selected).format(selectedFiles))
                }
            }
        }
    }

    private fun createTitle() = globalClass.getString(R.string.files_tab_title)

    fun addNewAction(action: Action): Action {
        return action.apply { actions.add(this) }
    }

    override fun onBackPressed(): Boolean {
        if (unselectAnySelectedFiles()) {
            return true
        } else if (handleBackGesture) {
            highlightedFiles.apply {
                clear()
                add(activeFolder.path)
            }
            openFolder(activeFolder.parent!!)

            return true
        }

        return false
    }

    /**
     * Unselects all the selected files.
     * returns true if any files were selected
     */
    fun unselectAnySelectedFiles(): Boolean {
        if (selectedFiles.isNotEmpty()) {
            unselectAllFiles()
            return true
        }
        return false
    }

    fun openFile(context: Context, item: DocumentHolder) {
        if (item.isApk) {
            ApkDialog.show(item)
        } else {
            // item.openFile(context, anonymous = false, skipSupportedExtensions = false)
        }
    }

    fun openFolder(
        item: DocumentHolder,
        rememberListState: Boolean = true,
        rememberSelectedFiles: Boolean = false,
        postEvent: () -> Unit = {}
    ) {
        if (isLoading) return

        if (!rememberSelectedFiles) {
            selectedFiles.clear()
            lastSelectedFileIndex = -1
        } else {
            selectedFiles.removeIf { key, value -> !value.exists() }
            if (selectedFiles.isEmpty()) lastSelectedFileIndex = -1
        }

        activeFolder = item

//        showEmptyRecycleBin = activeFolder.hasParent(globalClass.recycleBinDir)
//                || activeFolder.path == globalClass.recycleBinDir.path

        handleBackGesture = activeFolder.canAccessParent || selectedFiles.isNotEmpty()

        updatePathList()

        listFiles { newContent ->
            requestHomeToolbarUpdate()

            activeFolderContent.clear()
            activeFolderContent.addAll(newContent)

            if (!rememberListState) {
                contentListStates[item.path] = LazyGridState(0, 0)
            }

            activeListState = contentListStates[item.path] ?: LazyGridState()
                .also { contentListStates[item.path] = it }

            postEvent()
        }
    }

    fun quickReloadFiles() {
        if (isLoading) return

        val temp = arrayListOf<DocumentHolder>().apply { addAll(activeFolderContent) }

        activeFolderContent.clear()
        activeFolderContent.addAll(temp)

        handleBackGesture = activeFolder.canAccessParent || selectedFiles.isNotEmpty()

        requestHomeToolbarUpdate()

        showMoreOptionsButton = selectedFiles.size > 0

//        showEmptyRecycleBin = activeFolder.hasParent(globalClass.recycleBinDir)
//                || activeFolder.path == globalClass.recycleBinDir.path
    }

    fun reloadFiles(postEvent: () -> Unit = {}) {
        openFolder(activeFolder) { postEvent() }
    }

    private fun listFiles(onReady: (ArrayList<DocumentHolder>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            isLoading = true

            foldersCount = 0
            filesCount = 0

            val result = if (isSpecialDirectory()) {
                when (activeFolder) {
                    StorageProvider.audios -> StorageProvider.getAudioFiles()
                    StorageProvider.videos -> StorageProvider.getVideoFiles()
                    StorageProvider.images -> StorageProvider.getImageFiles()
                    StorageProvider.archives -> StorageProvider.getArchiveFiles()
                    StorageProvider.documents -> StorageProvider.getDocumentFiles()
                    //StorageProvider.bookmarks -> StorageProvider.getBookmarks()
                    else -> StorageProvider.getRecentFiles()
                }.apply {
                    if (activeFolder != StorageProvider.recentFiles) {
                        val sortingPrefs = globalClass.preferencesManager.filesSortingPrefs.getSortingPrefsFor(activeFolder)
                        when (sortingPrefs.sortMethod) {
                            SortingMethod.SORT_BY_NAME -> {
                                sortWith(if (sortingPrefs.reverseSorting) sortNameRev else sortName)
                            }

                            SortingMethod.SORT_BY_DATE -> {
                                sortWith(if (sortingPrefs.reverseSorting) sortOlderFirst else sortNewerFirst)
                            }

                            SortingMethod.SORT_BY_SIZE -> {
                                sortWith(if (sortingPrefs.reverseSorting) sortLargerFirst else sortSmallerFirst)
                            }
                        }

                        if (sortingPrefs.showFoldersFirst) sortWith(sortFoldersFirst)
                    }
                }.also {
                    filesCount = it.size
                }
            } else {
                activeFolder.listContent(
                    sortingPrefs = globalClass.preferencesManager.filesSortingPrefs.getSortingPrefsFor(
                        activeFolder
                    )
                ) {
                    if (it.isFile) filesCount++
                    else foldersCount++
                }.apply {
                    if (!globalClass.preferencesManager.displayPrefs.showHiddenFiles) {
                        removeIf { it.isHidden }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                onReady(result)
                isLoading = false
            }
        }
    }

    fun unselectAllFiles(quickReload: Boolean = true) {
        selectedFiles.clear()
        lastSelectedFileIndex = -1
        if (quickReload) quickReloadFiles()
    }

    fun onNewFileCreated(fileName: String, openFolder: Boolean = false) {
        val newFile = activeFolder.findFile(fileName)

        newFile?.let {
            highlightedFiles.apply {
                clear()
                add(it.path)
            }

            reloadFiles {
                CoroutineScope(Dispatchers.Main).launch {
                    val newItemIndex = activeFolderContent.getIndexIf { path == newFile.path }
                    if (newItemIndex > -1) {
                        getFileListState().scrollToItem(newItemIndex, 0)
                    }

                    if (openFolder) {
                        openFolder(newFile)
                    }
                }
            }
        }
    }

//    private fun getBookmarks() = globalClass.filesTabManager.bookmarks
//        .map { DocumentHolder.fromFullPath(it) }
//        .takeWhile { it != null } as ArrayList<DocumentHolder>

    fun getFileListState() = contentListStates[activeFolder.path] ?: LazyGridState().also {
        contentListStates[activeFolder.path] = it
    }

    private fun updateTabViewLabel() {
        val fullName = activeFolder.getName().orIf(AppCoreManager.instance.getString(R.string.internal_storage)) {
                activeFolder.path == Environment.getExternalStorageDirectory().absolutePath
            }
        tabViewLabel = if (fullName.length > 18) fullName.substring(0, 15) + "..." else fullName
    }

    private fun updatePathList() {
        currentPathSegments.apply {
            clear()
            add(activeFolder)
            if (activeFolder.canAccessParent) {
                var parentDir = activeFolder.parent!!
                add(parentDir)
                while (parentDir.canAccessParent) {
                    parentDir = parentDir.parent!!
                    add(parentDir)
                }
            }
            reverse()
        }.also { updateTabViewLabel() }
    }

//    fun requestNewTab(tab: Tab) {
//        globalClass.mainActivityManager.addTabAndSelect(tab)
//    }

//    fun addNewTask(task: FilesTabTask) {
//        globalClass.filesTabManager.filesTabTasks.add(task)
//        globalClass.showMsg(R.string.new_task_has_been_added)
//    }

    fun hideDocumentOptionsMenu() {
        FileOptionsDialog.hide()
    }

    fun deleteFiles(
        targetFiles: List<DocumentHolder>,
        taskCallback: FilesTabTaskCallback,
        moveToRecycleBin: Boolean = true
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            DeleteTask(targetFiles, moveToRecycleBin).execute(activeFolder, taskCallback)
        }
    }

    fun share(
        context: Context,
        targetDocumentHolder: DocumentHolder
    ) {
        val uris = arrayListOf<Uri>()
        selectedFiles.forEach {
            val file = it.component2().toFile()
            if (file != null) {
                uris.add(
                    getUriForFile(
                        context,
                        globalClass.packageName + ".provider",
                        file
                    )
                )
            } else {
                uris.add(it.component2().uri)
            }
        }

        val builder = ShareCompat.IntentBuilder(globalClass)
            .setType(if (uris.size == 1) targetDocumentHolder.mimeType else FileMimeType.anyFileType)
        uris.forEach {
            builder.addStream(it)
        }

        context.startActivity(
            builder.intent.apply {
                if (uris.size > 1) action = Intent.ACTION_SEND_MULTIPLE

                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        )
    }

    fun addToHomeScreen(context: Context, file: DocumentHolder) {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
        val pinShortcutInfo = ShortcutInfo
            .Builder(context, file.path)
            .setIntent(
                Intent(context, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("filePath", file.path)
                    flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                }
            )
            .setIcon(
                android.graphics.drawable.Icon.createWithResource(
                    context,
                    if (file.isFile) R.mipmap.default_shortcut_icon else R.mipmap.folder_shortcut_icon
                )
            )
            .setShortLabel(file.getName())
            .build()
        val pinnedShortcutCallbackIntent =
            shortcutManager.createShortcutResultIntent(pinShortcutInfo)
        shortcutManager.requestPinShortcut(
            pinShortcutInfo,
            PendingIntent.getBroadcast(
                context,
                0,
                pinnedShortcutCallbackIntent,
                PendingIntent.FLAG_IMMUTABLE
            ).intentSender
        )
    }

    fun isSpecialDirectory(source: DocumentHolder = activeFolder) = when (source) {
        StorageProvider.archives,
        StorageProvider.recentFiles,
        StorageProvider.images,
        StorageProvider.videos,
        StorageProvider.audios,
        StorageProvider.bookmarks,
        StorageProvider.documents -> true

        else -> false
    }

    fun canCreateNewFile() = !isSpecialDirectory()

    fun canRunTasks() = !isSpecialDirectory()

    object TaskDialog {
        var showTaskDialog by mutableStateOf(false)
        var taskDialogTitle by mutableStateOf(emptyString)
        var taskDialogSubtitle by mutableStateOf(emptyString)
        var taskDialogInfo by mutableStateOf(emptyString)
        var showTaskDialogProgressbar by mutableStateOf(true)
        var taskDialogProgress by mutableFloatStateOf(-1f)
    }

    object ApkDialog {
        var showApkDialog by mutableStateOf(false)
            private set
        var apkFile: DocumentHolder? = null
            private set
        var ApksArchive = false
            private  set

        fun show(file: DocumentHolder) {
            apkFile = file
            showApkDialog = true
            ApksArchive = file.isApk
        }

        fun hide() {
            showApkDialog = false
            apkFile = null
        }
    }

    object CompressDialog {
        var showCompressDialog by mutableStateOf(false)
            private set
        var task: CompressTask? = null
            private set

        fun show(task: CompressTask) {
            CompressDialog.task = task
            showCompressDialog = true
        }

        fun hide() {
            showCompressDialog = false
        }
    }

    object RenameDialog {
        var showRenameFileDialog by mutableStateOf(false)
            private set
        var targetFile: DocumentHolder? = null
            private set

        fun show(file: DocumentHolder) {
            targetFile = file
            showRenameFileDialog = true
        }

        fun hide() {
            showRenameFileDialog = false
            targetFile = null
        }
    }

    object FileOptionsDialog {
        var showFileOptionsDialog by mutableStateOf(false)
            private set
        var targetFile: DocumentHolder? = null
            private set

        fun show(file: DocumentHolder) {
            targetFile = file
            showFileOptionsDialog = true
        }

        fun hide() {
            showFileOptionsDialog = false
            targetFile = null
        }
    }

    object OpenWithDialog {
        var showOpenWithDialog by mutableStateOf(false)
            private set
        var targetFile: DocumentHolder? = null
            private set

        fun show(file: DocumentHolder) {
            targetFile = file
            showOpenWithDialog = true
        }

        fun hide() {
            showOpenWithDialog = false
            targetFile = null
        }
    }

    object Search {
        var searchQuery by mutableStateOf(emptyString)
        var searchResults = mutableStateListOf<DocumentHolder>()
    }

    val taskCallback = object : FilesTabTaskCallback(CoroutineScope(Dispatchers.IO)) {
        override fun onPrepare(details: FilesTabTaskDetails) {
            taskDialog.apply {
                showTaskDialog = true
                taskDialogTitle = details.title
                taskDialogSubtitle = details.subtitle
                showTaskDialogProgressbar = true
                taskDialogProgress = details.progress
                taskDialogInfo = details.info
            }
        }

        override fun onReport(details: FilesTabTaskDetails) {
            taskDialog.apply {
                taskDialogTitle = details.title
                taskDialogSubtitle = details.subtitle
                taskDialogProgress = details.progress
                taskDialogInfo = details.info
            }
        }

        override fun onComplete(details: FilesTabTaskDetails) {
            highlightedFiles.clear()

            details.task.getSourceFiles().forEach {
                activeFolder.findFile(it.getName())?.let { file ->
                    highlightedFiles.addIfAbsent(file.path)
                }
            }

            globalClass.showMsg(buildString {
                append(details.subtitle)
            })

            TaskDialog.showTaskDialog = false
            showTasksPanel = false

            reloadFiles()

            globalClass.filesTabManager.filesTabTasks.removeIf { it.id == details.task.id }
        }

        override fun onFailed(details: FilesTabTaskDetails) {
            globalClass.showMsg(details.subtitle)
            TaskDialog.showTaskDialog = false
            showTasksPanel = false
            reloadFiles()
        }
    }
}