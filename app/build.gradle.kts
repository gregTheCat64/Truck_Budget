plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("com.google.dagger.hilt.android")
    id ("kotlin-kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "ru.javacat.truckbudget"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.javacat.truckbudget"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["YANDEX_CLIENT_ID"] = "4e2906c578da49febf2dbf62eb3081ba"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            manifestPlaceholders["YANDEX_CLIENT_ID"] = "4e2906c578da49febf2dbf62eb3081ba"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":ui"))

    implementation (libs.authsdk)
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.bundles.nav)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
}