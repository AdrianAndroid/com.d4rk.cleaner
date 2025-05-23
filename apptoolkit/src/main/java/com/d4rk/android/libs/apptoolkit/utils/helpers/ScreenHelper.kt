package com.d4rk.android.libs.apptoolkit.utils.helpers

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object ScreenHelper {

    /**
     * Checks if the device is in landscape orientation.
     *
     * @param context The context to access resources.
     * @return True if the device is in landscape, false otherwise.
     */
    fun isLandscape(context : Context) : Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * Checks if the device is a tablet.
     *
     * @param context The context to access resources.
     * @return True if the device is considered a tablet, false otherwise.
     */
    fun isTablet(context : Context) : Boolean {
        val screenWidthDp = context.resources.configuration.screenWidthDp
        return screenWidthDp >= 600 // Common threshold for tablets
    }

    /**
     * Combines checks for landscape orientation and tablet.
     *
     * @param context The context to access resources.
     * @return True if the device is either in landscape or is a tablet.
     */
    fun isLandscapeOrTablet(context : Context) : Boolean {
        return isLandscape(context) || isTablet(context)
    }
}

@Composable
fun Int.nsp():TextUnit {
    return getRealDp(LocalContext.current,this.toDouble(),isDp = false).sp
}

@Composable
fun Float.nsp():TextUnit {
    return getRealDp(LocalContext.current,this.toDouble(),isDp = false).sp
}

@Composable
fun Double.nsp():TextUnit {
    return getRealDp(LocalContext.current,this,isDp = false).sp
}

@Composable
fun Int.ndp():Dp {
    return getRealDp(LocalContext.current,this.toDouble()).dp
}

@Composable
fun Float.ndp():Dp {
    return getRealDp(LocalContext.current,this.toDouble()).dp
}

@Composable
fun Double.ndp():Dp {
    return getRealDp(LocalContext.current,this).dp
}

private fun getRealDp(context: Context, value: Double, isDp: Boolean = true): Double {
    val density =
        if (isDp) context.resources.displayMetrics.density else context.resources.displayMetrics.scaledDensity
    val screenWidth = context.resources.displayMetrics.widthPixels
    return ((screenWidth / 1080.0) * value / density)
}
