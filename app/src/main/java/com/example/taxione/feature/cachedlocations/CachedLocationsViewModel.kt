package com.example.taxione.feature.cachedlocations

import androidx.lifecycle.ViewModel
import com.example.taxione.domain.SelectionStore
import com.example.taxione.domain.model.CachedLocation
import com.example.taxione.domain.model.SelectedLocation
import com.example.taxione.domain.model.Slot
import com.example.taxione.domain.repository.LocationRepository
import com.example.taxione.domain.usecase.ObserveCachedLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CachedLocationsViewModel @Inject constructor(
    private val store: SelectionStore,
    observeCachedLocationsUseCase: ObserveCachedLocationsUseCase
) : ViewModel() {

    val cachedLocations = observeCachedLocationsUseCase()

    fun selectLocation(location: CachedLocation, slot: Slot) {
        val selected = SelectedLocation(
            latLng = location.latLng,
            aqi = location.aqi,
            address = location.address,
            nickname = location.nickname,
        )
        store.setSlot(slot, selected)
        store.emitCameraTarget(selected.latLng)
    }
}
