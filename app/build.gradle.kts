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
    implementation(project(":apptoolkit"))
    implementation(dependencyNotation = libs.androidx.constraintlayout.compose)

    // Image Compression
    implementation(dependencyNotation = libs.compressor)
    implementation(dependencyNotation = libs.coil3.coil.video)

    implementation(libs.storage)
    implementation(libs.gson)
}