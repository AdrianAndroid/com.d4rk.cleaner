@file:Suppress("DEPRECATION")

package com.d4rk.cleaner.data.core

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.d4rk.android.libs.apptoolkit.data.core.BaseCoreManager
import com.d4rk.android.libs.apptoolkit.data.core.ads.AdsCoreManager
import com.d4rk.android.libs.apptoolkit.utils.error.ErrorHandler
import com.d4rk.cleaner.utils.constants.ads.AdsConstants
import com.d4rk.cleaner.data.datastore.DataStore
import com.d4rk.cleaner.func.holder.DocumentHolder
import com.d4rk.cleaner.func.tabs.FilesTabManager
import com.d4rk.cleaner.ui.screens.main.MainActivityManager
import com.d4rk.cleaner.ui.screens.settings.PreferencesManager
import com.d4rk.cleaner.utils.error.CrashlyticsErrorReporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.io.File

class AppCoreManager : BaseCoreManager() {

    private var currentActivity : Activity? = null
    private var uid = 0

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance : AppCoreManager
            private set

        lateinit var dataStore : DataStore
            private set

        val adsCoreManager : AdsCoreManager by lazy {
            AdsCoreManager(context = instance)
        }

        val isAppLoaded : Boolean
            get() = BaseCoreManager.isAppLoaded
    }


    val mainActivityManager: MainActivityManager by lazy { MainActivityManager() }
    val filesTabManager: FilesTabManager by lazy { FilesTabManager() }
    val preferencesManager: PreferencesManager by lazy { PreferencesManager() }

    val recycleBinDir: DocumentHolder
        get() = DocumentHolder.fromFile(File(getExternalFilesDir(null), "bin").apply { mkdirs() })

    fun showMsg(msg: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@AppCoreManager, msg, Toast.LENGTH_SHORT).show()
        }
    }


    fun generateUid() = uid++

    override fun onCreate() {
        super.onCreate()
        instance = this

        val crashlyticsReporter = CrashlyticsErrorReporter()
        ErrorHandler.init(reporter = crashlyticsReporter)

        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(observer = this)
    }

    override suspend fun onInitializeApp() = supervisorScope {
        val dataStoreInitialization : Deferred<Unit> = async { initializeDataStore() }
        val adsInitialization : Deferred<Unit> = async { initializeAds() }

        dataStoreInitialization.await()
        adsInitialization.await()
    }

    private fun initializeDataStore() {
        runCatching {
            dataStore = DataStore.getInstance(context = this@AppCoreManager)
        }.onFailure {
            ErrorHandler.handleInitializationFailure(
                message = "DataStore initialization failed" , exception = it as Exception , applicationContext = applicationContext
            )
        }
    }

    private fun initializeAds() {
        adsCoreManager.initializeAds(AdsConstants.APP_OPEN_UNIT_ID)
    }

    fun isAppLoaded() : Boolean {
        return isAppLoaded
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        currentActivity?.let { adsCoreManager.showAdIfAvailable(it) }
    }

    override fun onActivityCreated(activity : Activity , savedInstanceState : Bundle?) {}

    override fun onActivityStarted(activity : Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity : Activity) {}
    override fun onActivityPaused(activity : Activity) {}
    override fun onActivityStopped(activity : Activity) {}
    override fun onActivitySaveInstanceState(activity : Activity , outState : Bundle) {}
    override fun onActivityDestroyed(activity : Activity) {}
}