plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id ("kotlin-kapt")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "ru.javacat.ui"
    compileSdk = 34

    defaultConfig {
        minSdk = 28
        manifestPlaceholders["YANDEX_CLIENT_ID"] = "4e2906c578da49febf2dbf62eb3081ba"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":common"))

    implementation (libs.authsdk)
    implementation(libs.bundles.coroutines)
    implementation(libs.hilt)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation (libs.androidx.constraintlayout)
    implementation (libs.androidx.activity.ktx)
    implementation (libs.androidx.fragment.ktx)
    implementation(libs.bundles.viewmodel)
    implementation(libs.bundles.nav)
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation (libs.glide)

    kapt(libs.hilt.compiler)

}