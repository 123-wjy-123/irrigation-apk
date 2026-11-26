plugins {
    id("com.android.application")
}

android {
    namespace = "com.irrigation.web"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.irrigation.web"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        // 默认地址：Pi 热点。长按屏幕可以改成别的地址（会被记住）。
        buildConfigField("String", "DEFAULT_URL", "\"http://10.42.0.1:8080\"")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            // 用 debug 签名，方便直接安装；正式发布请换成自己的 keystore
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
}
