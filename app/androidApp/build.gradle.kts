import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

android {
    namespace = "com.bullish.goldalert"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    signingConfigs {
        create("release") {
            storeFile = file("release.keystore")
            storePassword = "goldrun123"
            keyAlias = "goldrun"
            keyPassword = "goldrun123"
        }
    }

    defaultConfig {
        applicationId = "com.bullish.goldalert"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Shared business logic + ViewModels
    implementation(project(":app:sharedLogic"))

    // Android Compose UI (platform-specific)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    
    // Dependency Injection & Network Core (client setup) — NEW
    implementation(libs.koin.compose)
    implementation(libs.koin.android)
    implementation(libs.ktor.client.core)
    
    // Compose Material Icons — NEW
    implementation(libs.compose.materialIcons)

    // Jetpack Glance Widget — NEW
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // Navigation Compose — NEW
    implementation(libs.androidx.navigation.compose)

    // WorkManager background scheduler — NEW
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    debugImplementation(libs.compose.uiTooling)
}