package com.example.taxione.feature.cachedlocations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.taxione.R
import com.example.taxione.domain.model.CachedLocation
import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.model.Slot
import com.example.taxione.ui.theme.TaxiOneTheme

@Composable
fun CachedLocationsScreen(
    navController: NavController,
    slot: Slot,
    viewModel: CachedLocationsViewModel = hiltViewModel(),
) {
    val locations by viewModel.cachedLocations.collectAsStateWithLifecycle(initialValue = emptyList())

    CachedLocationsScreenContent(
        slot = slot,
        locations = locations,
        onLocationSelected = {
            viewModel.selectLocation(it, slot)
            navController.popBackStack()
        },
    )
}

@Composable
private fun CachedLocationsScreenContent(
    slot: Slot,
    locations: List<CachedLocation>,
    onLocationSelected: (CachedLocation) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        if (locations.isEmpty()) {
            Text(
                text = stringResource(R.string.cached_locations_empty),
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(locations) { _, location ->
                    LocationRow(
                        location = location,
                        onClick = { onLocationSelected(location) },
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                }
            }
        }
    }
}

@Composable
private fun LocationRow(location: CachedLocation, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(location.address, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

private val previewLocations = listOf(
    CachedLocation(LatLng(37.564, 127.001), "Seocho District, Yangjae 2(i)-dong", ""),
    CachedLocation(LatLng(37.500, 127.050), "Gangnam District, Yeoksam 1(il)-dong", ""),
    CachedLocation(LatLng(37.600, 126.900), "Mapo District, Sangam-dong", "Home"),
)

@Preview(showBackground = true, showSystemUi = true, name = "CachedLocations — with items")
@Composable
private fun CachedLocationsScreenPreview() {
    TaxiOneTheme {
        CachedLocationsScreenContent(
            slot = Slot.A,
            locations = previewLocations,
            onLocationSelected = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "CachedLocations — empty")
@Composable
private fun CachedLocationsScreenPreviewEmpty() {
    TaxiOneTheme {
        CachedLocationsScreenContent(
            slot = Slot.B,
            locations = emptyList(),
            onLocationSelected = {},
        )
    }
}
