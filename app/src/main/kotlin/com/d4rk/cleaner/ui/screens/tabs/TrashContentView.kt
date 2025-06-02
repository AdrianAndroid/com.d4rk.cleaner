package com.d4rk.cleaner.ui.screens.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.func.holder.DocumentHolder
import com.d4rk.cleaner.ui.components.texts.Marquee
import com.d4rk.cleaner.ui.components.texts.defaultMarqueeParams
import com.d4rk.cleaner.ui.components.ui.Isolate
import com.d4rk.cleaner.ui.components.ui.Space
import com.d4rk.cleaner.ui.screens.preferences.constant.FilesTabFileListSize
import com.d4rk.cleaner.ui.screens.preferences.constant.FilesTabFileListSizeMap
import com.d4rk.cleaner.utils.extension.emptyString

//    val imageFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.imageFiles,
//        filesSize = mainActivityManager.imageFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_image)
//    )}
//    val videoFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.videoFiles,
//        filesSize = mainActivityManager.videoFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_video)
//    )}
//    val genericFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.genericFiles,
//        filesSize = mainActivityManager.genericFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_generic)
//    )}
//    val archiveFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.archiveFiles,
//        filesSize = mainActivityManager.archiveFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_archive)
//    )}
//    val apkFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.apkFiles,
//        filesSize = mainActivityManager.apkFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_apk)
//    )}
//    val audioFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.audioFiles,
//        filesSize = mainActivityManager.audioFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_audio)
//    )}
//    val windowsFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.windowsFiles,
//        filesSize = mainActivityManager.windowsFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_windows)
//    )}
//    val officeFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.officeFiles,
//        filesSize = mainActivityManager.officeFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_office)
//    )}
//    val fontFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.fontFiles,
//        filesSize = mainActivityManager.fontFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_font)
//    )}
//    val otherFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.otherFiles,
//        filesSize = mainActivityManager.otherFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_other)
//    )}
//    val bigFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.bigFiles,
//        filesSize = mainActivityManager.bigFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_big_file)
//    )}
//    val newFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.newFiles,
//        filesSize = mainActivityManager.newFilesSize,
//        title = AppCoreManager.instance.getString(R.string.title_new_file)
//    )}
//    val emptyFolders = remember {  TrashHolder(
//        files = mainActivityManager.emptyFolders,
//        filesSize = -1,
//        title = AppCoreManager.instance.getString(R.string.title_empty_folders)
//    )}
//    val emptyFilesTrash = remember {  TrashHolder(
//        files = mainActivityManager.emptyFiles,
//        filesSize = -1,
//        title = AppCoreManager.instance.getString(R.string.title_empty_files)
//    )}
@Composable
fun TrashContentView() {
    val mainActivityManager = AppCoreManager.instance.mainActivityManager
    val trashHolders = remember {
        mutableStateListOf(
            TrashHolder(
                files = mainActivityManager.imageFiles,
                filesSize = mainActivityManager.imageFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_image)
            ),
            TrashHolder(
                files = mainActivityManager.videoFiles,
                filesSize = mainActivityManager.videoFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_video)
            ),
            TrashHolder(
                files = mainActivityManager.genericFiles,
                filesSize = mainActivityManager.genericFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_generic)
            ),
            TrashHolder(
                files = mainActivityManager.archiveFiles,
                filesSize = mainActivityManager.archiveFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_archive)
            ),
            TrashHolder(
                files = mainActivityManager.apkFiles,
                filesSize = mainActivityManager.apkFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_apk)
            ),
            TrashHolder(
                files = mainActivityManager.audioFiles,
                filesSize = mainActivityManager.audioFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_audio)
            ),
            TrashHolder(
                files = mainActivityManager.windowsFiles,
                filesSize = mainActivityManager.windowsFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_windows)
            ),
            TrashHolder(
                files = mainActivityManager.officeFiles,
                filesSize = mainActivityManager.officeFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_office)
            ),
            TrashHolder(
                files = mainActivityManager.fontFiles,
                filesSize = mainActivityManager.fontFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_font)
            ),
            TrashHolder(
                files = mainActivityManager.otherFiles,
                filesSize = mainActivityManager.otherFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_other)
            ),
            TrashHolder(
                files = mainActivityManager.bigFiles,
                filesSize = mainActivityManager.bigFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_big_file)
            ),
            TrashHolder(
                files = mainActivityManager.newFiles,
                filesSize = mainActivityManager.newFilesSize,
                title = AppCoreManager.instance.getString(R.string.title_new_file)
            ),
            TrashHolder(
                files = mainActivityManager.emptyFolders,
                filesSize = -1,
                title = AppCoreManager.instance.getString(R.string.title_empty_folders)
            ),
            TrashHolder(
                files = mainActivityManager.emptyFiles,
                filesSize = -1,
                title = AppCoreManager.instance.getString(R.string.title_empty_files)
            )
        )
    }


    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            trashHolders.forEach { trashHolders: TrashHolder ->
                if (trashHolders.isShowList()) {
                    item(key = trashHolders.title) {
                        LevelTitle(
                            trashHolder = trashHolders,
                            onTabChanged = { isTabChange ->
                                trashHolders.isItemSelect = isTabChange
                            },
                            onRadioCheckChanged = { isRadioCheckChange ->
                                trashHolders.selectAll(isRadioCheckChange)
                            }
                        )
                    }
                    if (trashHolders.isItemSelect) {
                        items(
                            count = trashHolders.files.size,
                            key = { index ->
                                trashHolders.files[index].uid
                            }
                        ) { index ->
                            LevelSubTitle(
                                documentHolder = trashHolders.files[index],
                                isFileSelected = trashHolders.isFileSelected(trashHolders.files[index]),
                                onSelectionChanged = { documentHolder ->
                                    trashHolders.selectFile(documentHolder)
                                }
                            )
                        }
                    }
                }
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            onClick = {}
        ) {
            Text(text = "一键清理")
        }
    }
}


@Composable
fun LevelTitle(
    trashHolder: TrashHolder,
    onTabChanged: (Boolean) -> Unit = {},
    onRadioCheckChanged: (Boolean) -> Unit = {}
) {
    var arrowUp by remember { mutableStateOf(trashHolder.isItemSelect) }
    var radioCheck by remember(trashHolder.isSelectAll()) { mutableStateOf(trashHolder.isSelectAll()) }
    val preferencesManager = AppCoreManager.instance.preferencesManager
    val fontSize = when (preferencesManager.displayPrefs.fileListSize) {
        FilesTabFileListSize.SMALL.ordinal -> FilesTabFileListSizeMap.FontSize.SMALL
        FilesTabFileListSize.MEDIUM.ordinal -> FilesTabFileListSizeMap.FontSize.MEDIUM
        FilesTabFileListSize.LARGE.ordinal -> FilesTabFileListSizeMap.FontSize.LARGE
        else -> FilesTabFileListSizeMap.FontSize.EXTRA_LARGE
    }
    val iconSize = when (preferencesManager.displayPrefs.fileListSize) {
        FilesTabFileListSize.SMALL.ordinal -> FilesTabFileListSizeMap.IconSize.SMALL.dp
        FilesTabFileListSize.MEDIUM.ordinal -> FilesTabFileListSizeMap.IconSize.MEDIUM.dp
        FilesTabFileListSize.LARGE.ordinal -> FilesTabFileListSizeMap.IconSize.LARGE.dp
        else -> FilesTabFileListSizeMap.IconSize.EXTRA_LARGE.dp
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable {
                arrowUp = arrowUp.not()
                onTabChanged(arrowUp)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.clip(RoundedCornerShape(4.dp)),
            imageVector = if (arrowUp) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
            contentDescription = null
        )
        Text(
            modifier = Modifier.weight(1f),
            text = trashHolder.title,
            fontSize = fontSize.sp,
            maxLines = 1,
            lineHeight = (fontSize + 2).sp,
            overflow = TextOverflow.Ellipsis,
            color = Color.Unspecified
        )
        Box(
            modifier = Modifier
                .size(iconSize)
                .clickable {
                    radioCheck = !radioCheck
                    trashHolder.selectAll(radioCheck)
                    onRadioCheckChanged(radioCheck)
                },
            content = {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    imageVector = if (radioCheck) {
                        Icons.Default.RadioButtonChecked
                    } else {
                        Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = "",
                )
            }
        )
    }
}

@Composable
fun LevelSubTitle(
    documentHolder: DocumentHolder,
    isFileSelected: Boolean,
    onSelectionChanged: (DocumentHolder) -> Unit = {}
) {
    val globalClass = AppCoreManager.instance
    val preferencesManager = globalClass.preferencesManager
    val currentItemPath = documentHolder.path
    var radioCheck by remember(isFileSelected) { mutableStateOf(isFileSelected) }
    val iconSize = when (preferencesManager.displayPrefs.fileListSize) {
        FilesTabFileListSize.SMALL.ordinal -> FilesTabFileListSizeMap.IconSize.SMALL.dp
        FilesTabFileListSize.MEDIUM.ordinal -> FilesTabFileListSizeMap.IconSize.MEDIUM.dp
        FilesTabFileListSize.LARGE.ordinal -> FilesTabFileListSizeMap.IconSize.LARGE.dp
        else -> FilesTabFileListSizeMap.IconSize.EXTRA_LARGE.dp
    }

    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Isolate {
            if (documentHolder.isFile) {
                AsyncImage(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            radioCheck = !radioCheck
                            onSelectionChanged(documentHolder)
                        },
                    model = ImageRequest.Builder(globalClass).data(documentHolder).build(),
                    filterQuality = FilterQuality.Low,
                    error = painterResource(id = documentHolder.getFileIconResource()),
                    contentScale = ContentScale.Fit,
                    alpha = if (documentHolder.isHidden) 0.4f else 1f,
                    contentDescription = null
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            radioCheck = !radioCheck
                            onSelectionChanged(documentHolder)
                        }
                        .alpha(if (documentHolder.isHidden) 0.4f else 1f),
                    imageVector = Icons.Rounded.Folder,
                    contentDescription = null
                )
            }
        }

        Space(size = 8.dp)

        Column(Modifier.weight(1f)) {
            val fontSize =
                when (preferencesManager.displayPrefs.fileListSize) {
                    FilesTabFileListSize.SMALL.ordinal -> FilesTabFileListSizeMap.FontSize.SMALL
                    FilesTabFileListSize.MEDIUM.ordinal -> FilesTabFileListSizeMap.FontSize.MEDIUM
                    FilesTabFileListSize.LARGE.ordinal -> FilesTabFileListSizeMap.FontSize.LARGE
                    else -> FilesTabFileListSizeMap.FontSize.EXTRA_LARGE
                }

            Text(
                text = documentHolder.getName(),
                fontSize = fontSize.sp,
                maxLines = 1,
                lineHeight = (fontSize + 2).sp,
                overflow = TextOverflow.Ellipsis,
                color = Color.Unspecified
            )

            Marquee(
                modifier = Modifier,
                params = defaultMarqueeParams(),
            ) {
                Text(
                    text = currentItemPath,
                    fontSize = fontSize.sp,
                    maxLines = 1,
                    lineHeight = (fontSize + 2).sp,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Unspecified
                )
            }
        }

        Box(
            modifier = Modifier
                .size(iconSize)
                .clickable {
                    radioCheck = !radioCheck
                    onSelectionChanged(documentHolder)
                },
            content = {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    imageVector = if (radioCheck) {
                        Icons.Default.RadioButtonChecked
                    } else {
                        Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = "",
                )
            }
        )
    }
}


class TrashHolder(
    val files: MutableList<DocumentHolder>,
    val filesSize: Long,
    val title: String = emptyString,
) {
    private val selectedFiles = mutableStateMapOf<String, DocumentHolder>()
    var isItemSelect by mutableStateOf(false)

    fun isShowList() : Boolean {
        return files.isNotEmpty()
    }

    fun isSelectAll() : Boolean {
        return selectedFiles.size == files.size && files.isNotEmpty()
    }

    fun selectAll(isRadioCheckChange: Boolean) {
        if (isRadioCheckChange) {
            files.forEach {
                selectedFiles[it.path] = it
            }
            isItemSelect = true
        } else {
            selectedFiles.clear()
        }
    }

    fun selectFile(documentHolder: DocumentHolder) {
        if (selectedFiles.contains(documentHolder.path)) {
            selectedFiles.remove(documentHolder.path)
        } else {
            selectedFiles[documentHolder.path] = documentHolder
        }
    }

    fun isFileSelected(documentHolder: DocumentHolder): Boolean {
        return selectedFiles.contains(documentHolder.path)
    }
}



@Preview
@Composable
fun TrashContentViewPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        TrashContentView()
    }
}