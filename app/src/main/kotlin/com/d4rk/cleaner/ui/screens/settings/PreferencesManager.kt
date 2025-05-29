package com.d4rk.cleaner.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.func.holder.DocumentHolder
import com.d4rk.cleaner.func.misc.FileSortingPrefs
import com.d4rk.cleaner.func.misc.SortingMethod
import com.d4rk.cleaner.func.misc.prefMutableState
import com.d4rk.cleaner.utils.extension.emptyString
import com.d4rk.cleaner.utils.extension.fromJson
import com.d4rk.cleaner.utils.extension.toJson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class PreferencesManager {
    object SingleChoiceDialog {
        var show by mutableStateOf(false)

        var title = emptyString
            private set
        var description = emptyString
            private set
        var choices = mutableListOf<String>()
            private set
        var onSelect: (choice: Int) -> Unit = {}
            private set
        var selectedChoice = -1

        fun dismiss() {
            show = false
            title = emptyString
            description = emptyString
            choices.clear()
            selectedChoice = -1
            onSelect = {}
        }

        fun show(
            title: String,
            description: String,
            choices: List<String>,
            selectedChoice: Int,
            onSelect: (choice: Int) -> Unit
        ) {
            SingleChoiceDialog.title = title
            SingleChoiceDialog.description = description
            SingleChoiceDialog.choices.clear()
            SingleChoiceDialog.choices.addAll(choices)
            SingleChoiceDialog.onSelect = onSelect
            SingleChoiceDialog.selectedChoice = selectedChoice
            show = true
        }
    }

    object DisplayPrefs {
//        var theme by prefMutableState(
//            keyName = "theme",
//            defaultValue = ThemePreference.SYSTEM.ordinal,
//            getPreferencesKey = { intPreferencesKey(it) }
//        )

//        var fileListSize by prefMutableState(
//            keyName = "fileListSize",
//            defaultValue = FilesTabFileListSize.LARGE.ordinal,
//            getPreferencesKey = { intPreferencesKey(it) }
//        )

        var showBottomBarLabels by prefMutableState(
            keyName = "showBottomBarLabels",
            defaultValue = true,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var fileListColumnCount by prefMutableState(
            keyName = "fileListColumnCount",
            defaultValue = 1,
            getPreferencesKey = { intPreferencesKey(it) }
        )

        var showFolderContentCount by prefMutableState(
            keyName = "showFolderContentCount",
            defaultValue = false,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var showHiddenFiles by prefMutableState(
            keyName = "showHiddenFiles",
            defaultValue = false,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )
    }

    object TextEditorPrefs {
        var pinLineNumber by prefMutableState(
            keyName = "pinLineNumber",
            defaultValue = true,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var symbolPairAutoCompletion by prefMutableState(
            keyName = "symbolPairAutoCompletion",
            defaultValue = false,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var autoIndent by prefMutableState(
            keyName = "autoIndent",
            defaultValue = true,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var enableMagnifier by prefMutableState(
            keyName = "enableMagnifier",
            defaultValue = true,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var useICULibToSelectWords by prefMutableState(
            keyName = "useICULibToSelectWords",
            defaultValue = false,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var deleteEmptyLineFast by prefMutableState(
            keyName = "deleteEmptyLineFast",
            defaultValue = false,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var deleteMultiSpaces by prefMutableState(
            keyName = "deleteMultiSpaces",
            defaultValue = true,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var recentFilesLimit by prefMutableState(
            keyName = "textEditorRecentFilesLimit",
            defaultValue = -1,
            getPreferencesKey = { intPreferencesKey(it) }
        )

        var readOnly by prefMutableState(
            keyName = "readOnly",
            defaultValue = false,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var wordWrap by prefMutableState(
            keyName = "wordWrap",
            defaultValue = false,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )
    }

    object GeneralPrefs {
        var searchInFilesLimit by prefMutableState(
            keyName = "searchInFilesLimit",
            defaultValue = 150,
            getPreferencesKey = { intPreferencesKey(it) }
        )

        var showFileOptionMenuOnLongClick by prefMutableState(
            keyName = "showFileOptionMenuOnLongClick",
            defaultValue = true,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var moveToRecycleBin by prefMutableState(
            keyName = "moveToRecycleBin",
            defaultValue = true,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var signApk by prefMutableState(
            keyName = "signApk",
            defaultValue = false,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )
    }

    object FilesSortingPrefs {
        var filesSortingMethod by prefMutableState(
            keyName = "filesSortingMethod",
            defaultValue = SortingMethod.SORT_BY_NAME,
            getPreferencesKey = { intPreferencesKey(it) }
        )

        var showFoldersFirst by prefMutableState(
            keyName = "showFoldersFirst",
            defaultValue = true,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        var reverseFilesSortingMethod by prefMutableState(
            keyName = "reverseFilesSortingMethod",
            defaultValue = false,
            getPreferencesKey = { booleanPreferencesKey(it) }
        )

        fun getSortingPrefsFor(doc: DocumentHolder): FileSortingPrefs {
            return runBlocking {
                fromJson(
                    AppCoreManager.dataStore.dataStore.data.first()[stringPreferencesKey("fileSortingPrefs_${doc.path}")]
                ) ?: FileSortingPrefs(
                    sortMethod = filesSortingMethod,
                    showFoldersFirst = showFoldersFirst,
                    reverseSorting = reverseFilesSortingMethod
                )
            }
        }

        fun setSortingPrefsFor(doc: DocumentHolder, prefs: FileSortingPrefs) {
            runBlocking {
                AppCoreManager.dataStore.dataStore.edit {
                    it[stringPreferencesKey("fileSortingPrefs_${doc.path}")] = prefs.toJson()
                }
            }
        }
    }

    val singleChoiceDialog = SingleChoiceDialog
    val displayPrefs = DisplayPrefs
    val textEditorPrefs = TextEditorPrefs
    val generalPrefs = GeneralPrefs
    val filesSortingPrefs = FilesSortingPrefs
}