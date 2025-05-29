package com.d4rk.cleaner.func.misc

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.d4rk.cleaner.data.core.AppCoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

inline fun <reified A> prefMutableState(
    keyName: String,
    defaultValue: A,
    getPreferencesKey: (keyName: String) -> Preferences.Key<A>,
): MutableState<A> {
    val key: Preferences.Key<A> = getPreferencesKey(keyName)
    val snapshotMutableState: MutableState<A> = mutableStateOf(
        runBlocking {
            AppCoreManager.dataStore.dataStore.data.first()[key] ?: defaultValue
        }
    )

    return object : MutableState<A> {
        override var value: A
            get() = snapshotMutableState.value
            set(value) {
                val rollbackValue = snapshotMutableState.value
                snapshotMutableState.value = value
                runBlocking {
                    try {
                        AppCoreManager.dataStore.dataStore.edit {
                            if (value != null) {
                                it[key] = value as A
                            } else {
                                it.remove(key)
                            }
                        }
                    } catch (e: Exception) {
                        snapshotMutableState.value = rollbackValue
                    }
                }
            }

        override fun component1() = value
        override fun component2(): (A) -> Unit = { value = it }
    }
}