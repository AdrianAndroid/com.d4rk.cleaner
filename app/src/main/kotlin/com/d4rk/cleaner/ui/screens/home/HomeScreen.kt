package com.d4rk.cleaner.ui.screens.home

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

private const val MARGIN = 50
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

    LaunchedEffect(key1 = Unit) {
        if (! PermissionsHelper.hasStoragePermissions(context = context)) {
            PermissionsHelper.requestStoragePermissions(activity = context as Activity)
        }
    }

    if (uiErrorModel.showErrorDialog) {
        ErrorAlertDialog(errorMessage = uiErrorModel.errorMessage , onDismiss = { viewModel.dismissErrorDialog() })
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

        OutlinedCard(modifier = Modifier.width(300.ndp()).wrapContentHeight()) {
            IconButton(
                modifier = Modifier.fillMaxSize().bounceClick(),
                onClick = {
                    viewModel.analyze()
                },
                content = {
//                    Icon(Icons.Outlined.MoreVert, contentDescription = null)
                    Text(text = "hello World!")
                }
            )
        }
//        Row(modifier = Modifier
//            .fillMaxWidth()
//            .height(300.ndp())
//            .padding(horizontal = MARGIN.ndp())) {
//            Column(modifier = Modifier
//                .weight(1f)
//                .fillMaxHeight()
//                .border(BORDER_WIDTH.ndp(), Color.Black, RoundedCornerShape(BORDER_RADIUS.ndp()))
//                .padding(24.ndp())
//                , verticalArrangement = Arrangement.SpaceAround) {
//                val textStyle = TextStyle.Default.copy(fontSize = 41.nsp())
//                Text(text = "已用：111111112.41GB", style = textStyle)
//                Text(text = "总共：223.89GB", style = textStyle)
//                Text(text = "已清理：469.51MB", style = textStyle)
//            }
//            Spacer(modifier = Modifier.width(34.ndp()))
//            StorageProgressButton(
//                progress = uiState.storageInfo.storageUsageProgress,
//                modifier = Modifier
//                    .offset(y = 0.dp),
//                onClick = {
////                    viewModel.analyze()
//                }
//            )
//
//        }

        Spacer(modifier = Modifier.height(84.ndp()))
        BorderCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(518.ndp())
                .padding(horizontal = MARGIN.ndp())
        ) {

        }

        Spacer(modifier = Modifier.height(68.ndp()))
        TwoBorderCard(
            leftContent = {},
            rightContent = {}
        )

        Spacer(modifier = Modifier.height(68.ndp()))
        TwoBorderCard(
            leftContent = {},
            rightContent = {}
        )

        Spacer(modifier = Modifier.height(68.ndp()))
        TwoBorderCard(
            leftContent = {},
            rightContent = {}
        )

        Spacer(modifier = Modifier.height(68.ndp()))
        TwoBorderCard(
            leftContent = {},
            rightContent = {}
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            if (! uiState.analyzeState.isAnalyzeScreenVisible) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(300.ndp())
                    .padding(horizontal = 90.ndp())) {
                    Column(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(
                            BORDER_WIDTH.ndp(),
                            Color.Black,
                            RoundedCornerShape(BORDER_RADIUS.ndp())
                        )
                        .padding(24.ndp())
                        , verticalArrangement = Arrangement.SpaceAround) {
                        val textStyle = TextStyle.Default.copy(fontSize = 41.nsp())
                        Text(text = "已用：111111112.41GB", style = textStyle)
                        Text(text = "总共：223.89GB", style = textStyle)
                        Text(text = "已清理：469.51MB", style = textStyle)
                    }
                    Spacer(modifier = Modifier.width(34.ndp()))
                    StorageProgressButton(
                        progress = uiState.storageInfo.storageUsageProgress,
                        modifier = Modifier
                            .offset(y = 0.dp),
                        onClick = {
                            viewModel.analyze()
                        }
                    )

                }

                ExtraStorageInfo(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp) ,
                    cleanedSpace = uiState.storageInfo.cleanedSpace ,
                    freeSpace = "${uiState.storageInfo.freeSpacePercentage} %" ,
                )
            }

            Crossfade(
                targetState = uiState.analyzeState.isAnalyzeScreenVisible , animationSpec = tween(durationMillis = 300) , label = ""
            ) { showCleaningComposable ->
                if (showCleaningComposable) {
                    key(uiState.analyzeState.fileTypesData) {
                        AnalyzeScreen(
                            imageLoader = imageLoader , view = view , viewModel = viewModel , data = uiState
                        )
                    }
                }
            }
        }
    }
}

/*
@androidx.compose.runtime.Composable @androidx.compose.runtime.ComposableInferredTarget
public fun Card(
    modifier: androidx.compose.ui.Modifier = COMPILED_CODE,
    shape: androidx.compose.ui.graphics.Shape = COMPILED_CODE,
    colors: androidx.compose.material3.CardColors = COMPILED_CODE,
    elevation: androidx.compose.material3.CardElevation = COMPILED_CODE,
    border: androidx.compose.foundation.BorderStroke? = COMPILED_CODE,
    content: @androidx.compose.runtime.Composable() (androidx.compose.foundation.layout.ColumnScope.() -> kotlin.Unit)
): kotlin.Unit {
*/

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
    leftContent: @Composable() (androidx.compose.foundation.layout.ColumnScope.() -> Unit),
    rightContent: @Composable() (androidx.compose.foundation.layout.ColumnScope.() -> Unit),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(477.ndp())
            .padding(horizontal = MARGIN.ndp())
    ) {
        BorderCard(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            content = leftContent
        )
        Spacer(modifier = Modifier.width(45.ndp()))
        BorderCard(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            content = rightContent
        )
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