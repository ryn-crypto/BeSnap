plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.ryan.storyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ryan.storyapp"
        minSdk = 27
        targetSdk = 33
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
            buildConfigField("String", "BASE_URL", "\"${project.findProperty("BASE_URL") ?: ""}\"")
            buildConfigField("String", "API_KEY", "\"${project.findProperty("API_KEY") ?: ""}\"")
        }
        debug {
            buildConfigField("String", "BASE_URL", "\"${project.findProperty("BASE_URL") ?: ""}\"")
            buildConfigField("String", "API_KEY", "\"${project.findProperty("API_KEY") ?: ""}\"")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // livedata, recyclerview
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    // recyclerview
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    // view model
    implementation("androidx.activity:activity-ktx:1.8.0")
    // shimmer loading
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    // material design
    implementation("com.google.android.material:material:1.10.0")
    // camerax
    val cameraxVersion = "1.3.0"
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    // lottie
    implementation("com.airbnb.android:lottie:6.1.0")
    // DI
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    // navigation
    val navVersion = "2.7.4"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    // maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    // coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    // paging3
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation ("androidx.room:room-ktx:2.6.0")
    kapt ("androidx.room:room-compiler:2.6.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation ("androidx.activity:activity-ktx:1.8.0")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    // testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
kapt {
    correctErrorTypes = true
}