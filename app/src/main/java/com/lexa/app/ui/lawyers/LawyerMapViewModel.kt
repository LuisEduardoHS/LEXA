package com.lexa.app.ui.lawyers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.lexa.app.data.models.Lawyer
import com.lexa.app.data.repository.LawyerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LawyerMapViewModel @Inject constructor(
    lawyerRepository: LawyerRepository,
    private val locationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<LawyerMapState>(LawyerMapState.Loading)
    val uiState: StateFlow<LawyerMapState> = _uiState.asStateFlow()

    init {
        lawyerRepository.getAllLawyers()
            .onEach { lawyerList ->
                _uiState.update {
                    LawyerMapState.Success(lawyers = lawyerList, selectedLawyer = null)
                }
                fetchCurrentLocation()
            }
            .catch { e ->
                _uiState.update { LawyerMapState.Error(e.message ?: "Error desconocido") }
            }
            .launchIn(viewModelScope)
    }

    fun fetchCurrentLocation() {
        val hasPermissions = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermissions) {
            locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    _uiState.update {
                        if (it is LawyerMapState.Success) {
                            it.copy(userLocation = userLatLng)
                        } else {
                            it
                        }
                    }
                }
            }
        }
    }

    fun selectLawyer(lawyer: Lawyer) {
        _uiState.update {
            if (it is LawyerMapState.Success) {
                it.copy(selectedLawyer = lawyer)
            } else {
                it
            }
        }
    }

    fun dismissLawyerDetails() {
        _uiState.update {
            if (it is LawyerMapState.Success) {
                it.copy(selectedLawyer = null)
            } else {
                it
            }
        }
    }

}