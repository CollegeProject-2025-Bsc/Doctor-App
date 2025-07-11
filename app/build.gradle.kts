plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.doctorapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.doctorapp"
        minSdk = 29
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    //some core library for android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.lifecycle.viewmodel.android)
    implementation(libs.places)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //country code picker library
    implementation ("com.hbb20:ccp:2.7.3")

    //pin view for otp design
    implementation ("io.github.chaosleung:pinview:1.4.4")

    //library for accessing firebase
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
    implementation("com.google.firebase:firebase-auth")

    //location accessing library
    implementation ("com.google.android.gms:play-services-location:21.3.0")

    //another api calling network library
    implementation("io.ktor:ktor-client-android:3.1.1")


    //retrofit for api calling socket
    implementation ("com.google.code.gson:gson:2.9.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.facebook.android:facebook-android-sdk:latest.release")


    //kotlin core library for viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    //Image creating library
    implementation("io.coil-kt:coil:2.5.0")
    implementation("io.coil-kt:coil-svg:2.5.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")


    //lottie animation library
    implementation ("com.airbnb.android:lottie:3.4.0")



    //razorpay library to add online payment feature
    implementation ("com.razorpay:checkout:1.6.33")
}