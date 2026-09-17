package com.example.taxione.feature.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.taxione.domain.model.Booking
import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.model.SelectedLocation
import com.example.taxione.navigation.MapRoute
import com.example.taxione.ui.theme.DIVIDER_GRAY
import com.example.taxione.ui.theme.TaxiOneTheme

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rebookState by viewModel.rebookState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler {
        navController.navigate(MapRoute) {
            popUpTo<MapRoute> { inclusive = true }
        }
    }

    LaunchedEffect(rebookState) {
        when (val s = rebookState) {
            is RebookState.Done -> {
                navController.navigate(MapRoute) {
                    popUpTo<MapRoute> { inclusive = true }
                }
                viewModel.clearRebookState()
            }

            is RebookState.Error -> {
                snackbarHostState.showSnackbar(s.message)
                viewModel.clearRebookState()
            }

            else -> {}
        }
    }

    HistoryScreenContent(
        uiState = uiState,
        rebookState = rebookState,
        snackbarHostState = snackbarHostState,
        onRebook = { viewModel.rebook(it) },
        onRetry = { viewModel.retry() },
    )
}

@Composable
private fun HistoryScreenContent(
    uiState: HistoryUiState,
    rebookState: RebookState,
    snackbarHostState: SnackbarHostState,
    onRebook: (Booking) -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White),
        ) {
            when (val state = uiState) {
                is HistoryUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is HistoryUiState.Error -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(state.message)
                    Button(onClick = onRetry) { Text("Retry") }
                }

                is HistoryUiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            HistoryHeader(state.totalCount, state.totalPrice)
                            HorizontalDivider(thickness = 8.dp, color = DIVIDER_GRAY)
                        }
                        items(state.bookings) { booking ->
                            BookingItem(
                                booking = booking,
                                isRebooking = rebookState is RebookState.Loading,
                                onClick = { onRebook(booking) },
                            )
                            HorizontalDivider(color = DIVIDER_GRAY)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryHeader(count: Int, totalPrice: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total Count", color = Color.Gray, fontSize = 16.sp)
            Text("$count", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Black)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total Price", color = Color.Gray, fontSize = 16.sp)
            Text(
                "%.0f".format(totalPrice),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun BookingItem(booking: Booking, isRebooking: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isRebooking, onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "A",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.width(32.dp),
                color = Color.Black
            )
            Text(
                booking.locationA.address,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "B",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.width(32.dp),
                color = Color.Black
            )
            Text(
                booking.locationB.address,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

private val previewLocationA = SelectedLocation(
    latLng = LatLng(37.564, 127.001),
    aqi = 42,
    address = "Seocho District, Yangjae 2(i)-dong",
)
private val previewLocationB = SelectedLocation(
    latLng = LatLng(37.500, 127.050),
    aqi = 35,
    address = "Gangnam District, Yeoksam 1(il)-dong",
)
private val previewBookings = listOf(
    Booking("1", previewLocationA, previewLocationB, 1240.0, 0L),
    Booking("2", previewLocationB, previewLocationA, 980.0, 0L),
)

@Preview(showBackground = true, showSystemUi = true, name = "History — loading")
@Composable
private fun HistoryScreenPreviewLoading() {
    TaxiOneTheme {
        HistoryScreenContent(
            uiState = HistoryUiState.Loading,
            rebookState = RebookState.Idle,
            snackbarHostState = remember { SnackbarHostState() },
            onRebook = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "History — success")
@Composable
private fun HistoryScreenPreviewSuccess() {
    TaxiOneTheme {
        HistoryScreenContent(
            uiState = HistoryUiState.Success(
                totalCount = 2,
                totalPrice = 2220.0,
                bookings = previewBookings,
            ),
            rebookState = RebookState.Idle,
            snackbarHostState = remember { SnackbarHostState() },
            onRebook = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "History — error")
@Composable
private fun HistoryScreenPreviewError() {
    TaxiOneTheme {
        HistoryScreenContent(
            uiState = HistoryUiState.Error("Failed to load history"),
            rebookState = RebookState.Idle,
            snackbarHostState = remember { SnackbarHostState() },
            onRebook = {},
            onRetry = {},
        )
    }
}
