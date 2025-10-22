plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.github.theapache64.yaseen"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.github.theapache64.yaseen"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        // [latest version - i promise!]
        versionName = "v25.10.22.3"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {


    // ViewPager2 : AndroidX Widget ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")
}