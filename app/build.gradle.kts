plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp") version "1.9.0-1.0.12"  // Add KSP plugin
}

android {
    namespace = "com.example.ecommerce"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ecommerce"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database) // Firebase Realtime Database
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx) // LiveData
    implementation(libs.lifecycle.viewmodel.ktx) // ViewModel
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core) // Espresso
    implementation (libs.glide) // Glide
    implementation(libs.rxandroid) // RxAndroid
    implementation(libs.rxjava) // RxJava
    implementation(libs.room.runtime) // Room runtime
    implementation(libs.room.rxjava3) // Room RxJava support
    annotationProcessor(libs.room.compiler)
}
