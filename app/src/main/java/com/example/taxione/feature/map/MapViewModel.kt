package com.example.taxione.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxione.domain.SelectionStore
import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.model.SelectedLocation
import com.example.taxione.domain.repository.AqiRepository
import com.example.taxione.domain.repository.LocationRepository
import com.example.taxione.domain.usecase.CreateBookingUseCase
import com.example.taxione.domain.usecase.GetAddressUseCase
import com.example.taxione.domain.usecase.GetAqiUseCase
import com.example.taxione.domain.usecase.UpdateCachedAqiUseCase
import com.google.android.gms.maps.model.CameraPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    val store: SelectionStore,
    private val createBookingUseCase: CreateBookingUseCase,
    private val getAddressUseCase: GetAddressUseCase,
    private val updateCachedAqiUseCase: UpdateCachedAqiUseCase,
    private val getAqiUseCase: GetAqiUseCase
) : ViewModel() {

    val selection = store.selection
    val cameraTargetEvents = store.cameraTargetEvents

    private val _slotActionState = MutableStateFlow<SlotActionState>(SlotActionState.Idle)
    val slotActionState: StateFlow<SlotActionState> = _slotActionState.asStateFlow()

    private val _bookingActionState = MutableStateFlow<BookingActionState>(BookingActionState.Idle)
    val bookingActionState: StateFlow<BookingActionState> = _bookingActionState.asStateFlow()

    private val _navigateToBooking = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToBooking: SharedFlow<Unit> = _navigateToBooking.asSharedFlow()

    private val _rawCamera = MutableStateFlow<Pair<CameraPosition, Boolean>?>(null)

    fun onCameraChanged(position: CameraPosition, isMoving: Boolean) {
        _rawCamera.value = position to isMoving
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val centerAqiState: StateFlow<CenterAqiUiState> = _rawCamera
        .filterNotNull()
        .filter { (_, isMoving) -> !isMoving }
        .debounce(400L)
        .mapLatest { (position, _) ->
            val domainLatLng = position.toDomainLatLng()
            getAqiUseCase(domainLatLng).fold(
                onSuccess = { aqi ->
                    CenterAqiUiState.Success(aqi)
                },
                onFailure = { throwable ->
                    CenterAqiUiState.Error(
                        throwable.message ?: "Failed to fetch AQI"
                    )
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CenterAqiUiState.Idle)

    fun onVButtonTapped(cameraPosition: CameraPosition) {
        val nextSlot = selection.value.nextEmptySlot ?: return
        val aqi = (centerAqiState.value as? CenterAqiUiState.Success)?.aqi
        viewModelScope.launch {
            _slotActionState.value = SlotActionState.Loading
            val latLng = cameraPosition.toDomainLatLng()
            getAddressUseCase(latLng).onSuccess {
                store.setSlot(
                    nextSlot,
                    SelectedLocation(latLng = latLng, aqi = aqi, address = it)
                )
                if (aqi != null) updateCachedAqiUseCase(latLng, aqi)
                _slotActionState.value = SlotActionState.Idle
            }.onFailure {
                _slotActionState.value =
                    SlotActionState.Error(it.message ?: "Failed to set location")
            }
        }
    }

    fun clearSlotActionError() {
        _slotActionState.value = SlotActionState.Idle
    }

    fun book() {
        val selection = store.selection.value
        val a = selection.a ?: return
        val b = selection.b ?: return
        viewModelScope.launch {
            _bookingActionState.value = BookingActionState.Loading
            createBookingUseCase(a, b).onSuccess {
                store.setLastBooking(it)
                store.clear()
                _bookingActionState.value = BookingActionState.Idle
                _navigateToBooking.emit(Unit)
            }.onFailure {
                _bookingActionState.value =
                    BookingActionState.Error(it.message ?: "Booking failed")
            }
        }
    }

    fun clearBookingError() {
        _bookingActionState.value = BookingActionState.Idle
    }

    private fun CameraPosition.toDomainLatLng(): LatLng =
        LatLng(target.latitude, target.longitude)
}

sealed interface CenterAqiUiState {
    data object Idle : CenterAqiUiState
    data object Loading : CenterAqiUiState
    data class Success(val aqi: Int) : CenterAqiUiState
    data class Error(val message: String) : CenterAqiUiState
}

sealed interface SlotActionState {
    data object Idle : SlotActionState
    data object Loading : SlotActionState
    data class Error(val message: String) : SlotActionState
}

sealed interface BookingActionState {
    data object Idle : BookingActionState
    data object Loading : BookingActionState
    data class Error(val message: String) : BookingActionState
}
