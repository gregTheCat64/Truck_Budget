plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")

}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies{
    implementation(libs.coroutines.core)
    implementation(libs.bundles.coroutines)
    implementation(libs.dagger)
}