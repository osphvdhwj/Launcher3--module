plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.yourname.infinityxsecured"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yourname.infinityxsecured"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4" 
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")
    
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
}
