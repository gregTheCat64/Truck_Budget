import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id ("kotlin-kapt")
}

val configProps = Properties()
val YANDEX_CLIENT_ID = "4e2906c578da49febf2dbf62eb3081ba"

project.rootProject.file("config.properties").let {
    if (it.exists()) configProps.load(it.reader())
}

android {
    namespace = "ru.javacat.data"
    compileSdk = 34

    buildFeatures{
        buildConfig = true
    }

    defaultConfig {
        minSdk = 28
        //manifestPlaceholders.put("YANDEX_CLIENT_ID", "4e2906c578da49febf2dbf62eb3081ba")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "YANDEX_CLIENT_ID",
            configProps.getProperty("YANDEX_CLIENT_ID", "")
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    implementation(project(":common"))

    //implementation (libs.authsdk)
    implementation(libs.bundles.room)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.coroutines)
    implementation(libs.hilt)
    implementation(project(mapOf("path" to ":domain")))

    ksp(libs.room.compiler)

}