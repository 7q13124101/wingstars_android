import org.gradle.internal.extensions.core.extra

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.wingstars.home"
    compileSdk = rootProject.extra["compileSdkVersion"] as Int

    defaultConfig {
        minSdk = rootProject.extra["minSdkVersion"] as Int

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(project(":own"))
    implementation(project(":member"))
    implementation("com.github.lihangleo2:ShadowLayout:3.4.1")
    implementation("com.github.bumptech.glide:glide:4.15.0")

    implementation("io.github.scwang90:refresh-layout-kernel:3.0.0-alpha")    //核心必须依赖
    implementation("io.github.scwang90:refresh-header-classics:3.0.0-alpha")    //经典刷新头
    implementation("io.github.scwang90:refresh-footer-classics:3.0.0-alpha")    //经典加载

    implementation("io.github.youth5201314:banner:2.2.3")

    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")

}