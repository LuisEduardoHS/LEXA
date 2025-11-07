package com.lexa.app.ui.lawyers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexa.app.data.models.Lawyer
import com.lexa.app.data.repository.LawyerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    lawyerRepository: LawyerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LawyerMapState>(LawyerMapState.Loading)
    val uiState: StateFlow<LawyerMapState> = _uiState.asStateFlow()

    init {
        lawyerRepository.getAllLawyers()
            .onEach { lawyerList ->
                _uiState.update {
                    LawyerMapState.Success(lawyers = lawyerList, selectedLawyer = null)
                }
            }
            .catch { e ->
                _uiState.update { LawyerMapState.Error(e.message ?: "Error desconocido") }
            }
            .launchIn(viewModelScope)
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