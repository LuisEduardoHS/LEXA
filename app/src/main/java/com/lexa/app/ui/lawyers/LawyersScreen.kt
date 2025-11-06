package com.lexa.app.ui.lawyers

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LawyersScreen() {

    // 1. Define una ubicación por defecto (Ciudad de México)
    val mexicoCity = LatLng(19.4326, -99.1332)

    // 2. Define el "estado" de la cámara (dónde está, qué tan cerca está)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mexicoCity, 10f)
    }

    // 3. ¡El Composable de Google Maps!
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // 4. Aquí pondremos los marcadores (Marker)
    }
}