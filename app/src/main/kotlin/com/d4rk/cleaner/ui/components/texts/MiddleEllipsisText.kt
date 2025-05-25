package com.d4rk.cleaner.ui.components.texts

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun MiddleEllipsisText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = style.color.copy(alpha = style.alpha),
    softWrap: Boolean = false, // 强制单行
    overflow: TextOverflow = TextOverflow.Clip, // 默认为裁剪，实际会被中间省略替代
    maxLines: Int = 1, // 强制单行
) {
    // 快速处理短文本或空文本
    if (text.isEmpty()) {
        Text(
            text = "",
            modifier = modifier,
            style = style,
            color = color,
            softWrap = softWrap,
            overflow = overflow,
            maxLines = maxLines
        )
        return
    }

    // 创建 TextMeasurer 实例
    val textMeasurer = rememberTextMeasurer()

    // 使用 SubcomposeLayout 精确测量和布局文本
    SubcomposeLayout(modifier) { constraints ->
        val ellipsis = "..."

        // 测量完整文本
        val fullTextLayoutResult = textMeasurer.measure(
            text = text,
            style = style.copy(color = color)
        )

        // 如果文本宽度小于可用宽度，直接显示完整文本
        if (fullTextLayoutResult.size.width <= constraints.maxWidth) {
            val placeable = subcompose("fullText") {
                Text(
                    text = text,
                    style = style,
                    color = color,
                    softWrap = softWrap,
                    overflow = overflow,
                    maxLines = maxLines
                )
            }[0].measure(constraints)

            return@SubcomposeLayout layout(placeable.width, placeable.height) {
                placeable.place(0, 0)
            }
        }

        // 测量省略号宽度
        val ellipsisLayoutResult = textMeasurer.measure(
            text = ellipsis,
            style = style.copy(color = color)
        )
        val ellipsisWidth = ellipsisLayoutResult.size.width
        val availableWidth = constraints.maxWidth - ellipsisWidth

        // 快速处理：如果可用宽度不足，只显示省略号
        if (availableWidth <= 0) {
            val placeable = subcompose("ellipsisOnly") {
                Text(
                    text = ellipsis,
                    style = style,
                    color = color,
                    softWrap = softWrap,
                    overflow = overflow,
                    maxLines = maxLines
                )
            }[0].measure(constraints)

            return@SubcomposeLayout layout(placeable.width, placeable.height) {
                placeable.place(0, 0)
            }
        }

        // 二分查找最佳截断位置
        var low = 0
        var high = text.length / 2
        var bestLeftLength = 0
        var bestRightLength = 0
        var bestWidth = 0f

        while (low <= high) {
            val mid = (low + high) / 2
            if (mid == 0) break

            val leftText = text.take(mid)
            val rightText = text.takeLast(mid)

            val leftLayoutResult = textMeasurer.measure(
                text = leftText,
                style = style.copy(color = color)
            )
            val rightLayoutResult = textMeasurer.measure(
                text = rightText,
                style = style.copy(color = color)
            )

            val totalWidth = leftLayoutResult.size.width + rightLayoutResult.size.width

            if (totalWidth <= availableWidth) {
                bestLeftLength = mid
                bestRightLength = mid
                bestWidth = totalWidth.toFloat()
                low = mid + 1
            } else {
                high = mid - 1
            }
        }

        // 构建最终文本
        val leftText = text.take(bestLeftLength)
        val rightText = text.takeLast(bestRightLength)
        val truncatedText = buildString {
            append(leftText)
            append(ellipsis)
            append(rightText)
        }

        val placeable = subcompose("truncatedText") {
            Text(
                text = truncatedText,
                style = style,
                color = color,
                softWrap = softWrap,
                overflow = overflow,
                maxLines = maxLines
            )
        }[0].measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

//@Composable
//fun MiddleEllipsisText(
//    text: String,
//    modifier: Modifier = Modifier,
//    maxLines: Int = 1,
//    style: TextStyle = TextStyle.Default,
//) {
//    SubcomposeLayout(modifier) { constraints ->
//        if (text.isEmpty()) {
//            return@SubcomposeLayout layout(0, 0) {}
//        }
//
//        // 测量省略号的宽度
//        val ellipsisText = "..."
//        val ellipsisPlaceable = subcompose("ellipsis") {
//            Text(text = ellipsisText, style = style)
//        }[0].measure(constraints)
//
//        // 尝试完整显示文本
//        val fullTextPlaceable = subcompose("fullText") {
//            Text(text = text, style = style)
//        }[0].measure(constraints)
//
//        // 如果完整文本能放下，直接返回
//        if (fullTextPlaceable.width <= constraints.maxWidth) {
//            return@SubcomposeLayout layout(fullTextPlaceable.width, fullTextPlaceable.height) {
//                fullTextPlaceable.place(0, 0)
//            }
//        }
//
//        // 计算可用于左右文本的剩余宽度
//        val availableWidth = constraints.maxWidth - ellipsisPlaceable.width
//        if (availableWidth <= 0) {
//            // 空间不足，只显示省略号
//            return@SubcomposeLayout layout(ellipsisPlaceable.width, ellipsisPlaceable.height) {
//                ellipsisPlaceable.place(0, 0)
//            }
//        }
//
//        // 尝试寻找最佳截断点
//        var leftChars = 0
//        var rightChars = 0
//        var bestWidth = 0
//
//        // 简单的二分法查找
//        var low = 0
//        var high = text.length / 2
//
//        while (low <= high) {
//            val mid = (low + high) / 2
//            if (mid == 0) {
//                break
//            }
//
//            val leftText = text.take(mid)
//            val rightText = text.takeLast(mid)
//
//            val leftPlaceable = subcompose("left") {
//                Text(text = leftText, style = style)
//            }[0].measure(constraints)
//
//            val rightPlaceable = subcompose("right") {
//                Text(text = rightText, style = style)
//            }[0].measure(constraints)
//
//            val totalWidth = leftPlaceable.width + rightPlaceable.width
//
//            if (totalWidth <= availableWidth) {
//                leftChars = mid
//                rightChars = mid
//                bestWidth = totalWidth
//                low = mid + 1
//            } else {
//                high = mid - 1
//            }
//        }
//
//        // 如果找不到合适的截断点，显示部分左侧文本和省略号
//        if (leftChars == 0) {
//            leftChars = 1
//            val leftText = text.take(leftChars)
//            val leftPlaceable = subcompose("left") {
//                Text(text = leftText, style = style)
//            }[0].measure(constraints)
//
//            return@SubcomposeLayout layout(
//                leftPlaceable.width + ellipsisPlaceable.width,
//                maxOf(leftPlaceable.height, ellipsisPlaceable.height)
//            ) {
//                leftPlaceable.place(0, 0)
//                ellipsisPlaceable.place(leftPlaceable.width, 0)
//            }
//        }
//
//        // 构建最终布局
//        val leftText = text.take(leftChars)
//        val rightText = text.takeLast(rightChars)
//
//        val leftPlaceable = subcompose("left") {
//            Text(text = leftText, style = style)
//        }[0].measure(constraints)
//
//        val rightPlaceable = subcompose("right") {
//            Text(text = rightText, style = style)
//        }[0].measure(constraints)
//
//        val height = maxOf(leftPlaceable.height, rightPlaceable.height, ellipsisPlaceable.height)
//        val width = leftPlaceable.width + ellipsisPlaceable.width + rightPlaceable.width
//
//        layout(width, height) {
//            leftPlaceable.place(0, 0)
//            ellipsisPlaceable.place(leftPlaceable.width, 0)
//            rightPlaceable.place(leftPlaceable.width + ellipsisPlaceable.width, 0)
//        }
//    }
//}