package com.d4rk.android.libs.apptoolkit.utils.helpers

import android.util.Log
import com.d4rk.android.libs.apptoolkit.BuildConfig

fun logI(tag: String = "TAG", msg: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.i(tag, msg.invoke())
    }
}

fun logE(tag: String = "TAG", msg: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg.invoke())
    }
}

fun logD(tag: String = "TAG", msg: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, msg.invoke())
    }
}

fun logW(tag: String = "TAG", msg: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.w(tag, msg.invoke())
    }
}

fun logV(tag: String = "TAG", msg: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.v(tag, msg.invoke())
    }
}

fun logWtf(tag: String = "TAG", msg: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.wtf(tag, msg.invoke())
    }
}

