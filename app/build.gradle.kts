plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    // Firebase / Google Services
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.minorproject"

    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.minorproject"

        minSdk = 24

        targetSdk = 37

        versionCode = 1

        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}


dependencies {

    // =====================================================
    // ANDROIDX
    // =====================================================

    implementation(
        "androidx.appcompat:appcompat:1.7.1"
    )

    implementation(
        "androidx.activity:activity-ktx:1.10.1"
    )

    implementation(
        "androidx.core:core-ktx:1.16.0"
    )

    implementation(
        "androidx.constraintlayout:constraintlayout:2.2.1"
    )

    implementation(
        "androidx.cardview:cardview:1.0.0"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.9.1"
    )


    // =====================================================
    // MATERIAL
    // =====================================================

    implementation(
        "com.google.android.material:material:1.12.0"
    )


    // =====================================================
    // COMPOSE
    // =====================================================

    implementation(
        platform(
            "androidx.compose:compose-bom:2025.06.01"
        )
    )

    implementation(
        "androidx.activity:activity-compose:1.10.1"
    )

    implementation(
        "androidx.compose.ui:ui"
    )

    implementation(
        "androidx.compose.ui:ui-graphics"
    )

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    implementation(
        "androidx.compose.material3:material3"
    )

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    debugImplementation(
        "androidx.compose.ui:ui-test-manifest"
    )


    // =====================================================
    // FIREBASE BOM
    // =====================================================

    implementation(
        platform(
            "com.google.firebase:firebase-bom:34.17.0"
        )
    )


    // =====================================================
    // FIREBASE AUTHENTICATION
    // =====================================================

    implementation(
        "com.google.firebase:firebase-auth"
    )


    // =====================================================
    // FIRESTORE
    // =====================================================

    implementation(
        "com.google.firebase:firebase-firestore"
    )


    // =====================================================
    // GOOGLE CREDENTIAL MANAGER
    // =====================================================

    implementation(
        "androidx.credentials:credentials:1.3.0"
    )

    implementation(
        "androidx.credentials:credentials-play-services-auth:1.3.0"
    )

    implementation(
        "com.google.android.libraries.identity.googleid:googleid:1.1.1"
    )


    // =====================================================
    // SPLASH SCREEN
    // =====================================================

    implementation(
        "androidx.core:core-splashscreen:1.0.1"
    )


    // =====================================================
    // TESTING
    // =====================================================

    testImplementation(
        "junit:junit:4.13.2"
    )

    androidTestImplementation(
        "androidx.test.ext:junit:1.2.1"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.6.1"
    )
}