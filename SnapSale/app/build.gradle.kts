plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.snapsale"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.snapsale"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        exclude("META-INF/INDEX.LIST")
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("org.jetbrains:annotations:15.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // GOOGLE MAPS DEPENDENCIES
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")

    implementation("com.google.android.libraries.places:places:3.3.0")

    implementation("com.google.maps:google-maps-services:0.15.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")

    // WEB SCRAPPING DEPENDENCY
    implementation("org.jsoup:jsoup:1.17.2")

    // IMAGE SRC DEPENDENCY
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // GOOGLE TRANSLATE DEPENDENCY
    implementation("com.google.cloud:google-cloud-translate:1.27.0") {
        exclude("com.google.guava", "guava")
    }
    implementation("com.google.guava:guava:30.1.1-android")

    // WORK MANAGER DEPENDENCY
    implementation("androidx.work:work-runtime:2.9.0")
}