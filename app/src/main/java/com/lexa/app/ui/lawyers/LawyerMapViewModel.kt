package com.lexa.app.ui.lawyers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexa.app.data.repository.LawyerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LawyerMapViewModel @Inject constructor(
    lawyerRepository: LawyerRepository
) : ViewModel() {

    val uiState: StateFlow<LawyerMapState> = lawyerRepository.getAllLawyers()
        .map { lawyerList ->
            LawyerMapState.Success(lawyerList) as LawyerMapState
        }
        .catch { e ->
            emit(LawyerMapState.Error(e.message ?: "Error desconocido"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LawyerMapState.Loading
        )
}