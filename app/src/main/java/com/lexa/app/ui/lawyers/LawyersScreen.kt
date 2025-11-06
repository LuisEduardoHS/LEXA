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

    // 1. Define una ubicación por defecto
    val monterrey = LatLng(25.6690, -100.3100)

    // 2. Define el "estado" de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(monterrey, 12f)
    }

    // 3. ¡El Composable de Google Maps!
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // 4. Aquí pondremos los marcadores (Marker)
    }
}