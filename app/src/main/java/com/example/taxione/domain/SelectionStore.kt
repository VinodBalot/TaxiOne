package com.example.taxione.domain

import com.example.taxione.domain.model.Booking
import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.model.SelectedLocation
import com.example.taxione.domain.model.Slot
import com.example.taxione.feature.map.SelectionUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectionStore @Inject constructor() {

    private val _selection = MutableStateFlow(SelectionUiState())
    val selection: StateFlow<SelectionUiState> = _selection.asStateFlow()

    private val _lastBooking = MutableStateFlow<Booking?>(null)
    val lastBooking: StateFlow<Booking?> = _lastBooking.asStateFlow()

    // Channel buffers events even when MapScreen is off-stack (no active collector).
    private val _cameraTargetChannel = Channel<LatLng>(Channel.BUFFERED)
    val cameraTargetEvents: Flow<LatLng> = _cameraTargetChannel.receiveAsFlow()

    fun setSlot(slot: Slot, location: SelectedLocation) {
        _selection.update { it.withSlot(slot, location) }
    }

    fun setNickname(slot: Slot, nickname: String) {
        _selection.update { it.withNickname(slot, nickname) }
    }

    fun setBothSlots(locationA: SelectedLocation, locationB: SelectedLocation) {
        _selection.update {
            it.copy(a = locationA, b = locationB)
        }
    }

    fun emitCameraTarget(latLng: LatLng) {
        _cameraTargetChannel.trySend(latLng)
    }

    fun setLastBooking(booking: Booking) {
        _lastBooking.value = booking
    }

    fun clear() {
        _selection.value = SelectionUiState()
    }
}
