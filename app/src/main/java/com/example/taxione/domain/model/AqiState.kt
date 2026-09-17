package com.example.taxione.domain.model

sealed interface AqiState {
    data object Idle : AqiState
    data object Loading : AqiState
    data class Success(val aqi: Int) : AqiState
    data class Error(val message: String) : AqiState
}