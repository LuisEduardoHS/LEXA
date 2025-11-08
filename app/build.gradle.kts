// build.gradle.kts (Module :app)
import java.util.Properties

// 1. Plugins que necesita nuestra app
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Hilt (Inyección de Dependencias)
    id("com.google.dagger.hilt.android")
    // KSP (Procesador de anotaciones más rápido que kapt)
    id("com.google.devtools.ksp")
    // Kotlinx Serialization (para convertir JSON a objetos Kotlin)
    id("org.jetbrains.kotlin.plugin.serialization")
    // Google Maps Secrets (para guardar tu API key de forma segura)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.lexa.app"
    compileSdk = 34 // API 34 (Android 14)

    defaultConfig {
        applicationId = "com.lexa.app"
        minSdk = 24 // Android 7.0

        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // 1. Lee el archivo local.properties (ahora "Properties" funciona gracias al import)
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            // El .use es una extensión de Kotlin, ahora debería ser reconocido
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        } else {
            localProperties.setProperty("HF_TOKEN", System.getenv("HF_TOKEN") ?: "")
        }

        // 2. Obtiene el token
        val hfToken = localProperties.getProperty("HF_TOKEN")
        if (hfToken.isNullOrEmpty()) {
            println("ADVERTENCIA: HF_TOKEN no encontrado en local.properties. La app puede fallar.")
        }

        // 3. Expone el token como una variable de compilación
        buildConfigField("String", "HF_TOKEN", "\"$hfToken\"")

        // 3. Obtiene el token de Gemini
        val geminiKey = localProperties.getProperty("GEMINI_API_KEY")
        if (geminiKey.isNullOrEmpty()) {
            println("ADVERTENCIA: GEMINI_API_KEY no encontrado en local.properties.")
        }
        // 4. Expone el token de Gemini
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
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
        // Habilitamos Jetpack Compose
        compose = true
        buildConfig = true
    }
    composeOptions {
        // Usamos el compilador de Kotlin para Compose
        kotlinCompilerExtensionVersion = "1.5.12"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// 2. Aquí listamos todas nuestras librerías
dependencies {

    // ----- CORE Y UI (JETPACK COMPOSE) -----
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.github.jeziellago:compose-markdown:0.4.0")
    // BOM (Bill of Materials) - Nos ayuda a manejar las versiones de Compose
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    implementation("androidx.compose.foundation:foundation")
    // ViewModels para Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    // Navegación en Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("androidx.compose.material:material-icons-extended")

    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // ----- INYECCIÓN DE DEPENDENCIAS (HILT) -----
    // Esencial para arquitectura limpia
    implementation("com.google.dagger:hilt-android:2.48.1")
    ksp("com.google.dagger:hilt-compiler:2.48.1")
    // Integración de Hilt con Navegación de Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // ----- NETWORKING (RETROFIT & KOTLINX SERIALIZATION) -----
    // Retrofit (para llamar a la API de IA)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Serialización (JSON a Kotlin) - Más moderno que GSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    // OkHttp (El motor de Retrofit, para logs)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ----- BASE DE DATOS LOCAL (ROOM) -----
    // Para guardar el historial de chat
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1") // Para Corutinas
    ksp("androidx.room:room-compiler:2.6.1")

    // ----- GOOGLE MAPS -----
    // SDK de Mapas para Compose
    implementation("com.google.maps.android:maps-compose:4.4.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // ----- FIREBASE -----
    // BOM (Bill of Materials) - Maneja las versiones de Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    // Base de datos en tiempo real (para Abogados y Foro)
    implementation("com.google.firebase:firebase-firestore-ktx")
    // Autenticación (para el Foro)
    implementation("com.google.firebase:firebase-auth-ktx")

    // ----- TESTING (Pruebas) -----
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}