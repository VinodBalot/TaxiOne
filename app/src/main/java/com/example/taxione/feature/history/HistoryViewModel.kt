package com.example.taxione.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxione.domain.model.Booking
import com.example.taxione.domain.usecase.GetBookingUseCase
import com.example.taxione.domain.usecase.RebookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val rebookUseCase: RebookUseCase,
    private val getBookingUseCase: GetBookingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _rebookState = MutableStateFlow<RebookState>(RebookState.Idle)
    val rebookState: StateFlow<RebookState> = _rebookState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            try {
                val bookings = getBookingUseCase()
                _uiState.value = HistoryUiState.Success(
                    bookings = bookings,
                    totalCount = bookings.size,
                    totalPrice = bookings.sumOf { it.price },
                )
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(e.message ?: "Failed to load history")
            }
        }
    }

    fun rebook(booking: Booking) {
        viewModelScope.launch {
            _rebookState.value = RebookState.Loading
            try {
                rebookUseCase(booking)
                _rebookState.value = RebookState.Done
            } catch (e: Exception) {
                _rebookState.value = RebookState.Error(e.message ?: "Failed to refresh AQI")
            }
        }
    }

    fun clearRebookState() {
        _rebookState.value = RebookState.Idle
    }

    fun retry() = loadHistory()
}

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(
        val bookings: List<Booking>,
        val totalCount: Int,
        val totalPrice: Double,
    ) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

sealed interface RebookState {
    data object Idle : RebookState
    data object Loading : RebookState
    data object Done : RebookState
    data class Error(val message: String) : RebookState
}
