import com.android.aaptcompiler.android.isTruthy

plugins {
    alias(notation= libs.plugins.androidApplication)
    alias(notation= libs.plugins.jetbrainsKotlinAndroid)
    alias(notation= libs.plugins.googlePlayServices)
    alias(notation= libs.plugins.googleFirebase)
    alias(notation= libs.plugins.compose.compiler)
    alias(notation= libs.plugins.about.libraries)
}

android {
    compileSdk = 35
    namespace = "com.d4rk.cleaner"
    defaultConfig {
        applicationId = "com.d4rk.cleaner"
        minSdk = 26
        targetSdk = 35
        versionCode = 158
        versionName = "3.2.4"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        @Suppress("UnstableApiUsage")
        androidResources.localeFilters += listOf(
            "en" ,
            "bg-rBG" ,
            "de-rDE" ,
            "es-rGQ" ,
            "fr-rFR" ,
            "hi-rIN" ,
            "hu-rHU" ,
            "in-rID" ,
            "it-rIT" ,
            "ja-rJP" ,
            "pl-rPL" ,
            "pt-rBR" ,
            "ro-rRO" ,
            "ru-rRU" ,
            "sv-rSE" ,
            "th-rTH" ,
            "tr-rTR" ,
            "uk-rUA" ,
            "zh-rTW" ,
            "zh-rCN" ,
        )
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isDebuggable = false
        }
        debug {
            isDebuggable = true
        }
    }

    buildTypes.forEach { buildType ->
        with(buildType) {
            multiDexEnabled = true
            proguardFiles(
                getDefaultProguardFile(name = "proguard-android-optimize.txt") ,
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    bundle {
        storeArchive {
            enable = true
        }
    }
}

dependencies {

    // App Core
//    implementation(project(":apptoolkit"))
    //AndroidX
    api(dependencyNotation = libs.androidx.core.ktx)
    api(dependencyNotation = libs.androidx.appcompat)
    api(dependencyNotation = libs.androidx.core.splashscreen)
    api(dependencyNotation = libs.androidx.multidex)
    api(dependencyNotation = libs.androidx.work.runtime.ktx)

    // Compose
    api(dependencyNotation = platform(libs.androidx.compose.bom))
    api(dependencyNotation = libs.androidx.ui)
    api(dependencyNotation = libs.androidx.activity.compose)
    api(dependencyNotation = libs.androidx.ui.graphics)
    api(dependencyNotation = libs.androidx.compose.runtime)
    api(dependencyNotation = libs.androidx.runtime.livedata)
    api(dependencyNotation = libs.androidx.ui.tooling.preview)
    api(dependencyNotation = libs.androidx.material3)
    api(dependencyNotation = libs.androidx.material.icons.extended)
    api(dependencyNotation = libs.datastore.preferences)
    api(dependencyNotation = libs.androidx.datastore.preferences)
    api(dependencyNotation = libs.androidx.foundation)
    api(dependencyNotation = libs.androidx.navigation.compose)

    // Firebase
    api(dependencyNotation = platform(libs.firebase.bom))
    api(dependencyNotation = libs.firebase.analytics.ktx)
    api(dependencyNotation = libs.firebase.crashlytics.ktx)
    api(dependencyNotation = libs.firebase.perf)

    // Google
    api(dependencyNotation = libs.play.services.ads)
    api(dependencyNotation = libs.user.messaging.platform)
    api(dependencyNotation = libs.material)
    api(dependencyNotation = libs.app.update.ktx)
    api(dependencyNotation = libs.billing)
    api(dependencyNotation = libs.review.ktx)

    // Images
    api(dependencyNotation = libs.coil.compose)
    api(dependencyNotation = libs.coil.gif)
    api(dependencyNotation = libs.coil.network.okhttp)

    // Kotlin
    api(dependencyNotation = libs.kotlinx.coroutines.android)
    api(dependencyNotation = libs.kotlinx.serialization.json)

    // Ktor
    api(dependencyNotation = platform(libs.ktor.bom))
    api(dependencyNotation = libs.ktor.client.android)
    api(dependencyNotation = libs.ktor.client.serialization)
    api(dependencyNotation = libs.ktor.client.logging)
    api(dependencyNotation = libs.ktor.client.content.negotiation)
    api(dependencyNotation = libs.ktor.serialization.kotlinx.json)

    // Lifecycle
    api(dependencyNotation = libs.androidx.lifecycle.runtime.ktx)
    api(dependencyNotation = libs.androidx.lifecycle.livedata.ktx)
    api(dependencyNotation = libs.androidx.lifecycle.process)
    api(dependencyNotation = libs.androidx.lifecycle.viewmodel.ktx)
    api(dependencyNotation = libs.androidx.lifecycle.viewmodel.compose)
    api(dependencyNotation = libs.androidx.lifecycle.runtime.compose)

    // About
    api(dependencyNotation = libs.aboutlibraries)
    api(dependencyNotation = libs.core)
    implementation(dependencyNotation = libs.androidx.constraintlayout.compose)

    // Image Compression
    implementation(dependencyNotation = libs.compressor)
    implementation(dependencyNotation = libs.coil3.coil.video)

    implementation(libs.storage)
    implementation(libs.gson)

    debugImplementation("androidx.compose.ui:ui-tooling:1.8.1")
    releaseImplementation("androidx.compose.ui:ui-tooling-preview:1.8.1")
}