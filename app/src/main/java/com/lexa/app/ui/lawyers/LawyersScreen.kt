package com.lexa.app.ui.lawyers

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.lexa.app.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.MapProperties
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LawyersScreen(
    viewModel: LawyerMapViewModel = hiltViewModel()
) {
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
    )

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    when {
        locationPermissionState.status.isGranted -> {
            LaunchedEffect(Unit) {
                viewModel.fetchCurrentLocation()
            }
            LawyerMapContent(viewModel = viewModel)
        }

        !locationPermissionState.status.shouldShowRationale && !locationPermissionState.status.isGranted -> {
            PermissionDeniedScreen(
                title = "Permiso Requerido",
                text = "La función de mapa necesita tu permiso de ubicación para funcionar. " +
                        "Como lo has denegado permanentemente, debes ir a la configuración " +
                        "de tu teléfono para activarlo manualmente."
            )
        }

        else -> {
            PermissionDeniedScreen(
                title = "Permiso de Ubicación",
                text = "Para mostrarte abogados cercanos, LEXA necesita " +
                        "acceder a tu ubicación.",
                buttonText = "Conceder Permiso",
                onButtonClick = {
                    locationPermissionState.launchPermissionRequest()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerMapContent(viewModel: LawyerMapViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val monterrey = LatLng(25.6690, -100.3100)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(monterrey, 12f)
    }

    val scope = rememberCoroutineScope()

    if (uiState is LawyerMapState.Success && (uiState as LawyerMapState.Success).userLocation != null) {
        val userLocation = (uiState as LawyerMapState.Success).userLocation!!
        LaunchedEffect(userLocation) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(userLocation, 15f)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,

            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true
            )
        ) {
            when (val state = uiState) {
                is LawyerMapState.Loading -> {}
                is LawyerMapState.Error -> {}
                is LawyerMapState.Success -> {
                    state.lawyers.filter { it.location != null }.forEach { lawyer ->
                        Marker(
                            state = MarkerState(position = LatLng(lawyer.location!!.latitude, lawyer.location.longitude)),
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_lawyer_pin_red),
                            title = lawyer.name,
                            snippet = lawyer.specialty,
                            onClick = {
                                viewModel.selectLawyer(lawyer)
                                true
                            }
                        )
                    }
                }
            }
        }

        if (uiState is LawyerMapState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        if (uiState is LawyerMapState.Error) {
            Text(text = (uiState as LawyerMapState.Error).message, modifier = Modifier.align(Alignment.Center))
        }

        if (uiState is LawyerMapState.Success && (uiState as LawyerMapState.Success).selectedLawyer != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.dismissLawyerDetails() },
                sheetState = sheetState
            ) {
                LawyerDetailSheet(lawyer = (uiState as LawyerMapState.Success).selectedLawyer!!)
            }
        }
    }
}

@Composable
fun PermissionDeniedScreen(
    title: String,
    text: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Text(text = text, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
        if (buttonText != null && onButtonClick != null) {
            Button(onClick = onButtonClick, modifier = Modifier.padding(top = 16.dp)) {
                Text(buttonText)
            }
        }
    }
}