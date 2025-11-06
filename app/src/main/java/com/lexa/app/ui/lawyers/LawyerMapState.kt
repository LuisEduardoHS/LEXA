package com.lexa.app.ui.lawyers

import com.lexa.app.data.models.Lawyer

sealed interface LawyerMapState {
    data object Loading : LawyerMapState

    data class Error(val message: String) : LawyerMapState

    data class Success(val lawyers: List<Lawyer>) : LawyerMapState
}