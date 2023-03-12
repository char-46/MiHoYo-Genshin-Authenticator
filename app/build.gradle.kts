plugins {
    id("kotlin-android")
    id("com.android.application")
}

val vCompose = "1.2.0-beta03"

android {

    compileSdk = 33

    defaultConfig {
        applicationId = "char46.auth"
        minSdk = 26
        targetSdk = 33
        versionCode = 14
        versionName = "2.0.5"
        multiDexEnabled = true
        resourceConfigurations.apply {
            clear()
            add("zh")
        }

    }

    buildTypes {

        fun com.android.build.api.dsl.ApplicationBuildType.enableMinify() {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
        }

        debug {
            versionNameSuffix = "-debug"
            applicationIdSuffix = ".debug"
            buildConfigField("String","APP_CENTER_KEY","\"28c700e2-b5bd-42cc-9302-6f87749f6968\"")
        }

        release {

            enableMinify()
            versionNameSuffix = "-release"
            buildConfigField("String","APP_CENTER_KEY","\"1c793f09-3bc5-4eb7-984c-b5f3d975601f\"")
        }

        create("beta") {
            initWith(getByName("debug"))
            enableMinify()
            versionNameSuffix = "-beta"
            isDebuggable = true
            isJniDebuggable = true
            isRenderscriptDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = vCompose
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "char46.auth"
}

dependencies {

    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.google.android.material:material:1.7.0")
    implementation("com.google.accompanist:accompanist-permissions:0.20.0")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.20.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha03")
    implementation("com.google.crypto.tink:tink-android") {
        version {
            strictly("1.4.0")
        }
    }

    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.github.skydoves:landscapist-coil:1.4.0")
    implementation("com.microsoft.appcenter:appcenter-crashes:4.4.2")
    implementation("com.microsoft.appcenter:appcenter-analytics:4.4.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
    implementation("com.geetest.sensebot:sensebot:4.3.4.5") {
        exclude("com.squareup.okhttp3","okhttp")
    }
    implementation("com.journeyapps:zxing-android-embedded:4.2.0") {
        exclude("androidx.legacy","legacy-support-v4")
    }

    implementation("androidx.compose.ui:ui:$vCompose")
    implementation("androidx.compose.material:material:$vCompose")
    debugImplementation("androidx.compose.ui:ui-tooling:$vCompose")

}
