plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "org.tmurakam.presentationtimer"

    compileSdk = 37

    // 署名設定は keystore.propertiesファイルに記述すること
    /*
    val props = java.util.Properties()
    props.load(java.io.FileInputStream(file("keystore.properties")))

    signingConfigs {
        create("release") {
            storeFile = file(props["keystore"] as String)
            storePassword = props["keystore.password"] as String
            keyAlias = props["key.alias"] as String
            keyPassword = props["key.password"] as String
        }
    }
    */

    buildTypes {
        release {
            //signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-project.txt"
            )
        }
    }

    defaultConfig {
        applicationId = "org.tmurakam.presentationtimer"
        versionCode = 9
        versionName = "2.1.3"

        minSdk = 23
        targetSdk = 37
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

dependencies {
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.fragment:fragment:1.8.9")
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.browser:browser:1.10.0")
    implementation("androidx.media:media:1.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
    implementation("com.google.firebase:firebase-crashlytics")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
