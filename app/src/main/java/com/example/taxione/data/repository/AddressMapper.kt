package com.example.taxione.data.repository

import com.example.taxione.data.dto.GeocodingResponseDto

internal fun GeocodingResponseDto.toDisplayAddress(): String {
    val admins = localityInfo?.administrative ?: return ""
    val sorted = admins.sortedBy { it.order }
    return when {
        sorted.size >= 2 -> "${sorted[sorted.size - 2].name}, ${sorted.last().name}"
        sorted.size == 1 -> sorted.first().name
        else -> ""
    }
}
