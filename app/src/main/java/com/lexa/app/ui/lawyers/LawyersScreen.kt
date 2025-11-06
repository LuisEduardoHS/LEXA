package com.lexa.app.ui.lawyers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.lexa.app.R

@Composable
fun LawyersScreen(
    viewModel: LawyerMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val monterrey = LatLng(25.6690, -100.3100)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(monterrey, 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ) {

            when (val state = uiState) {
                is LawyerMapState.Loading -> { }
                is LawyerMapState.Error -> {  }
                is LawyerMapState.Success -> {

                    state.lawyers.filter { it.location != null }.forEach { lawyer ->

                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    lawyer.location!!.latitude,
                                    lawyer.location.longitude
                                )
                            ),
                            icon = BitmapDescriptorFactory.fromResource(
                                R.drawable.ic_lawyer_pin_red
                            ),
                            title = lawyer.name,
                            snippet = lawyer.specialty,
                        )
                    }
                }
            }
        }

        if (uiState is LawyerMapState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (uiState is LawyerMapState.Error) {
            Text(
                text = (uiState as LawyerMapState.Error).message,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}