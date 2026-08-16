import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedLogic"
            isStatic = true
        }
    }

    android {
       namespace = "com.bullish.goldalert.sharedLogic"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }

    sourceSets {
        commonMain.dependencies {
            // Room KMP runtime + SQLite driver
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            // Koin (shared DI)
            implementation(libs.koin.core)
            // Kotlin DateTime KMP utility
            implementation(libs.kotlinx.datetime)
            // Ktor Client content negotiation & json serialization
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            // AndroidX Lifecycle Viewmodel (KMP)
            implementation(libs.androidx.lifecycle.viewmodel)
        }
        androidMain.dependencies {
            // SQLCipher native AAR — Android only
            implementation(libs.sqlcipher.android)
            // Koin Android extensions
            implementation(libs.koin.android)
            // Ktor OkHttp Client engine
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            // Ktor Darwin Client engine
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// Pass Room schema location directly to KSP processor
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

// Wire the Room annotation processor (KSP) to every compilation target.
dependencies {
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
}

configurations.configureEach {
    if (name == "kspAndroidMainProcessorClasspath" ||
        name == "kspIosArm64" ||
        name == "kspIosSimulatorArm64") {
        project.dependencies.add(name, libs.androidx.room.compiler.get())
    }
}