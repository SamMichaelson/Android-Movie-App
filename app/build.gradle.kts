
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.apiconnection"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.apiconnection"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Picasso for image loading
    implementation("com.squareup.picasso:picasso:2.71828")

    // Material Design components
    implementation("com.google.android.material:material:1.9.0")

    // Coroutines for asynchronous programming
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Retrofit for HTTP requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // AndroidX Core KTX
    implementation("androidx.core:core-ktx:1.12.0")

    // AndroidX Lifecycle components
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // AndroidX Activity
    implementation("androidx.activity:activity-compose:1.7.2")

    // AndroidX Annotation
    implementation("androidx.annotation:annotation:1.7.0")

    // AndroidX AppCompat
    implementation("androidx.appcompat:appcompat:1.6.1")

    // AndroidX ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // AndroidX RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.12.0")

    // Firebase In-App Messaging
    implementation("com.google.firebase:firebase-inappmessaging:20.3.3")

    // Testing dependencies
    androidTestImplementation("androidx.test:monitor:1.6.1")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("junit:junit:4.13.2")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // GridLayout
    implementation("androidx.gridlayout:gridlayout:1.0.0")
}
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.1") // Use the latest version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21") // Use the latest version
    }
}