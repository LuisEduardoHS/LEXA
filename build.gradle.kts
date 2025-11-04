// build.gradle.kts (Project: LEXA)

plugins {
    id("com.android.application") version "8.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false

    // PLUGINS QUE AÑADIMOS
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false

    // LÍNEA QUE FALTABA (AÑADE ESTA):
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23" apply false
}