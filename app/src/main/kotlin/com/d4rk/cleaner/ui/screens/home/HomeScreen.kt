package com.d4rk.cleaner.ui.screens.home

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material.icons.outlined.SnippetFolder
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import com.d4rk.android.libs.apptoolkit.data.model.ui.error.UiErrorModel
import com.d4rk.android.libs.apptoolkit.ui.components.dialogs.ErrorAlertDialog
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.model.ui.screens.UiHomeModel
import com.d4rk.cleaner.ui.components.progressbars.StorageProgressButton
import com.d4rk.cleaner.ui.screens.analyze.AnalyzeScreen
import com.d4rk.cleaner.utils.helpers.PermissionsHelper
import com.d4rk.android.libs.apptoolkit.utils.helpers.ndp
import com.d4rk.android.libs.apptoolkit.utils.helpers.nsp
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.func.holder.StorageDeviceHolder
import com.d4rk.cleaner.func.tabs.FilesTab
import com.d4rk.cleaner.ui.components.texts.MiddleEllipsisText
import com.d4rk.cleaner.ui.screens.tabs.FilesTabContentView
import com.d4rk.cleaner.utils.cleaning.StorageUtils
import com.raival.compose.file.explorer.screen.main.tab.files.provider.StorageProvider

private const val MARGIN = 50
private const val MARGIN_BOTTOM = 25
private const val CARD_HEIGHT = 518
private const val BORDER_WIDTH = 2
private const val BORDER_RADIUS = 74

@Composable
fun HomeScreen() {
    val context : Context = LocalContext.current
    val view : View = LocalView.current
    val viewModel : HomeViewModel = viewModel()
    val uiState : UiHomeModel by viewModel.uiState.collectAsState()
    val uiErrorModel : UiErrorModel by viewModel.uiErrorModel.collectAsState()
    val imageLoader : ImageLoader = remember {
        ImageLoader.Builder(context = context).memoryCache {
            MemoryCache.Builder().maxSizePercent(context = context , percent = 0.24).build()
        }.diskCache {
            DiskCache.Builder().directory(directory = context.cacheDir.resolve(relative = "image_cache")).maxSizePercent(percent = 0.02).build()
        }.build()
    }
    val scrollState = rememberScrollState()
    val mainActivityManager = AppCoreManager.instance.mainActivityManager

    LaunchedEffect(key1 = Unit) {
        if (! PermissionsHelper.hasStoragePermissions(context = context)) {
            PermissionsHelper.requestStoragePermissions(activity = context as Activity)
        }
    }

    if (uiErrorModel.showErrorDialog) {
        ErrorAlertDialog(errorMessage = uiErrorModel.errorMessage , onDismiss = { viewModel.dismissErrorDialog() })
    }

    BackHandler {
        if (mainActivityManager.canExit().not()) {
            viewModel.showInternal(true)
        } else if (uiState.showInternalStorage) {
            viewModel.showInternal(false)
        } else if(context is Activity) {
            context.finish()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        StorageProgressButton(
            progress = uiState.storageInfo.storageUsageProgress,
            modifier = Modifier
                .size(600.ndp())
                .offset(y = 0.dp),
            onClick = {
//                    viewModel.analyze()
            }
        )

        OutlinedCard(modifier = Modifier
            .width(300.ndp())
            .wrapContentHeight()) {
            IconButton(
                modifier = Modifier
                    .fillMaxSize()
                    .bounceClick(),
                onClick = {
                    viewModel.analyze()
                },
                content = {
//                    Icon(Icons.Outlined.MoreVert, contentDescription = null)
                    Text(text = "开始扫描")
                }
            )
        }

        OutlinedCard(modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()) {
            MiddleEllipsisText(
                text = uiState.displayProcessText,
                style = TextStyle(fontSize = 14.sp, color = Color.Blue),
                modifier = Modifier
                    .width(250.dp)
                    .padding(top = 16.dp)
            )
        }

        TwoBorderCard(
            leftContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_empty),
                    subtitle = stringResource(R.string.item_subtitle_1, "${uiState.analyzedFiles.emptyFolders.size + uiState.analyzedFiles.emptyFiles.size}"),
                )
            },
            rightContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_big_file),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.bigFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.bigFilesSize)),
                )
            }
        )

        Spacer(modifier = Modifier.height(MARGIN_BOTTOM.ndp()))

        TwoBorderCard(
            leftContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_generic),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.genericFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.genericFilesSize)),
                )
            },
            rightContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_new_file),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.newFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.newFilesSize)),
                )
            }
        )

        Spacer(modifier = Modifier.height(MARGIN_BOTTOM.ndp()))

        TwoBorderCard(
            leftContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_apk),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.apkFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.apkFilesSize)),
                )
            },
            rightContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_image),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.imageFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.imageFilesSize)),
                )
            }
        )

        Spacer(modifier = Modifier.height(MARGIN_BOTTOM.ndp()))

        TwoBorderCard(
            leftContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_video),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.videoFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.videoFilesSize)),
                )
            },
            rightContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_audio),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.audioFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.audioFilesSize)),
                )
            }
        )

        Spacer(modifier = Modifier.height(MARGIN_BOTTOM.ndp()))

        TwoBorderCard(
            leftContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_office),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.officeFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.officeFilesSize)),
                )
            },
            rightContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_archive),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.archiveFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.archiveFilesSize)),
                )
            }
        )

        Spacer(modifier = Modifier.height(MARGIN_BOTTOM.ndp()))

        TwoBorderCard(
            leftContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_font),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.fontFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.fontFilesSize)),
                )
            },
            rightContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.windows_files),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.windowsFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.windowsFilesSize)),
                )
            }
        )

        Spacer(modifier = Modifier.height(MARGIN_BOTTOM.ndp()))

        TwoBorderCard(
            leftContent = {
                ItemCard(
                    imageVector = Icons.Outlined.Android,
                    title = stringResource(R.string.title_other),
                    subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.otherFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.otherFilesSize)),
                )
            },
            rightContent = null
        )

        Spacer(modifier = Modifier.height(MARGIN_BOTTOM.ndp()))

        StorageProvider.getStorageDevices(context).forEach { holder: StorageDeviceHolder ->
            TwoBorderCard(
                leftContent = {
                    ItemCard(
                        imageVector = Icons.Outlined.SdStorage,
                        title = holder.title,
                        //title = stringResource(R.string.internal_storage),
                        subtitle = stringResource(R.string.item_subtitle_2, "${uiState.analyzedFiles.otherFiles.size}", StorageUtils.formatSize(uiState.analyzedFiles.otherFilesSize)),
                    )
                },
                rightContent = null,
                onClickLeft = {
                    mainActivityManager.replaceCurrentTabWith(FilesTab(holder.documentHolder))
                    viewModel.showInternal(true)
                }
            )

            Spacer(modifier = Modifier.height(MARGIN_BOTTOM.ndp()))
//            HorizontalDivider()
        }
    }

    if (uiState.showInternalStorage) {
        FilesTabContentView()
    }
}


@Composable
private fun ItemCard(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = Icons.Outlined.SnippetFolder,
    title: String = "",
    subtitle: String = "",
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(48.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.bounceClick(),
                    imageVector = imageVector,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.basicMarquee(),
            )
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun TwoCardRow(icon: String, size: Long, modifier: Modifier = Modifier) {
    val storageIcons: Map<String, ImageVector> = mapOf(
        stringResource(id = R.string.installed_apps) to Icons.Outlined.Apps,
        stringResource(id = R.string.system) to Icons.Outlined.Android,
        stringResource(id = R.string.music) to Icons.Outlined.MusicNote,
        stringResource(id = R.string.images) to Icons.Outlined.Image,
        stringResource(id = R.string.documents) to Icons.Outlined.FolderOpen,
        stringResource(id = R.string.downloads) to Icons.Outlined.Download,
        stringResource(id = R.string.other_files) to Icons.Outlined.FolderOpen,
    )
    Card(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(48.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        modifier = Modifier.bounceClick(),
                        imageVector = storageIcons[icon] ?: Icons.Outlined.SnippetFolder,
                        contentDescription = icon,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Column {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.basicMarquee(),
                )
                Text(text = StorageUtils.formatSize(size), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun BorderCard(
    modifier: Modifier = Modifier,
    content: @Composable() (androidx.compose.foundation.layout.ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(BORDER_RADIUS.ndp()),
        content = content
    )
}

@Composable
private fun TwoBorderCard(
    leftContent: @Composable() (androidx.compose.foundation.layout.ColumnScope.() -> Unit)? = null,
    rightContent: @Composable() (androidx.compose.foundation.layout.ColumnScope.() -> Unit)? = null,
    onClickLeft: (() -> Unit)? = null,
    onClickRight: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = MARGIN.ndp())
    ) {
        if (leftContent != null) {
            val modifierLeft : Modifier = if (onClickLeft != null) {
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clip(shape = RoundedCornerShape(BORDER_RADIUS.ndp()))
                    .bounceClick()
                    .clickable(onClick = onClickLeft)
            } else {
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
            }
            BorderCard(
                modifier = modifierLeft,
                content = leftContent
            )
        }
        if (rightContent != null) {
            Spacer(modifier = Modifier.width(45.ndp()))
            val modifierRight: Modifier = if (onClickRight != null) {
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clip(shape = RoundedCornerShape(BORDER_RADIUS.ndp()))
                    .bounceClick()
                    .clickable(onClick = onClickRight)
            } else {
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
            }
            BorderCard(
                modifier = modifierRight,
                content = rightContent
            )
        }
    }
}

@Composable
fun ExtraStorageInfo(
    modifier : Modifier = Modifier ,
    cleanedSpace : String ,
    freeSpace : String ,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
            .padding(horizontal = 16.dp, vertical = 8.dp) , horizontalArrangement = Arrangement.SpaceAround , verticalAlignment = Alignment.CenterVertically
    ) {
        InfoColumn(
            title = stringResource(id = R.string.cleaned_space) , value = cleanedSpace , modifier = Modifier.weight(weight = 1f)
        )

        VerticalDivider()

        InfoColumn(
            title = stringResource(id = R.string.free_space) , value = freeSpace , modifier = Modifier.weight(weight = 1f)
        )
    }
}

@Composable
fun InfoColumn(
    title : String , value : String , modifier : Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally , modifier = modifier
    ) {
        Text(text = title , style = MaterialTheme.typography.bodySmall , modifier = Modifier.basicMarquee())
        Text(
            text = value , style = MaterialTheme.typography.bodyMedium , maxLines = 2 , overflow = TextOverflow.Ellipsis
        )
    }
}