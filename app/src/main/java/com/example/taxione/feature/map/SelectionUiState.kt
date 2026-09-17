package com.example.taxione.feature.map

import com.example.taxione.domain.model.SelectedLocation
import com.example.taxione.domain.model.Slot

data class SelectionUiState(
    val a: SelectedLocation? = null,
    val b: SelectedLocation? = null,
) {
    val buttonLabel: String get() = when {
        a == null -> "Set A"
        b == null -> "Set B"
        else -> "Book"
    }

    val nextEmptySlot: Slot? get() = when {
        a == null -> Slot.A
        b == null -> Slot.B
        else -> null
    }

    fun slot(slot: Slot): SelectedLocation? = if (slot == Slot.A) a else b

    fun withSlot(slot: Slot, location: SelectedLocation): SelectionUiState =
        if (slot == Slot.A) copy(a = location) else copy(b = location)

    fun withNickname(slot: Slot, nickname: String): SelectionUiState {
        val loc = slot(slot) ?: return this
        return withSlot(slot, loc.copy(nickname = nickname))
    }
}