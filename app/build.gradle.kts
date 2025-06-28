plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.lyalyapomidortodo"
    compileSdk = 34 // Android 14 (API Level 34), актуальная версия на ноябрь 2024 года

    defaultConfig {
        applicationId = "com.example.lyalyapomidortodo"
        minSdk = 26 // Минимальная версия API, поддерживаемая большинством современных библиотек
        targetSdk = 34 // Соответствует требованиям Google Play на ноябрь 2024 года
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // JDK 17 — рекомендованная версия для AGP 8.9
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17" // Соответствует JDK 17
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11" // Совместим с Kotlin 1.9.24
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.activity:activity:1.9.0")
    implementation("androidx.core:core:1.13.1")
    implementation("androidx.core:core-ktx:1.13.1")

    implementation(platform(libs.compose.bom))
    
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)
    implementation(libs.compose.viewmodel)
    implementation(libs.compose.runtime.livedata)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.androidx.work.runtime)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
