package com.example.taxione.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxione.domain.SelectionStore
import com.example.taxione.domain.model.Slot
import com.example.taxione.domain.usecase.UpdateCachedNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val store: SelectionStore,
    private val updateCachedNicknameUseCase: UpdateCachedNicknameUseCase
) : ViewModel() {

    fun locationFor(slot: Slot) = store.selection.map { it.slot(slot) }

    fun updateNickname(slot: Slot, raw: String) {
        val trimmed = raw.take(20)
        store.setNickname(slot, trimmed)
        val location = store.selection.value.slot(slot) ?: return
        viewModelScope.launch {
            updateCachedNicknameUseCase(location.latLng, trimmed)
        }
    }
}