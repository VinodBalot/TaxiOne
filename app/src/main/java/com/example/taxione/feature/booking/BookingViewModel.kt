package com.example.taxione.feature.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxione.domain.SelectionStore
import com.example.taxione.domain.model.Booking
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    store: SelectionStore,
) : ViewModel() {

    val uiState: StateFlow<BookingUiState> = store.lastBooking
        .map { booking ->
            if (booking != null) BookingUiState.Success(booking) else BookingUiState.Idle
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookingUiState.Idle)
}

sealed interface BookingUiState {
    data object Idle : BookingUiState
    data class Success(val booking: Booking) : BookingUiState
    data class Error(val message: String) : BookingUiState
}