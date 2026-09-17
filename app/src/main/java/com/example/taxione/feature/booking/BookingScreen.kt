package com.example.taxione.feature.booking

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.taxione.domain.model.Booking
import com.example.taxione.domain.model.LatLng
import com.example.taxione.domain.model.SelectedLocation
import com.example.taxione.navigation.HistoryRoute
import com.example.taxione.ui.theme.CONTAINER_YELLOW
import com.example.taxione.ui.theme.DIVIDER_GRAY
import com.example.taxione.ui.theme.TaxiOneTheme

@Composable
fun BookingScreen(
    navController: NavController,
    viewModel: BookingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        navController.popBackStack()
    }

    BookingScreenContent(
        uiState = uiState,
        onViewHistory = { navController.navigate(HistoryRoute) },
    )
}

@Composable
private fun BookingScreenContent(
    uiState: BookingUiState,
    onViewHistory: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        when (val state = uiState) {
            is BookingUiState.Idle ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            is BookingUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    LocationDetailItem(  label = stringResource(R.string.location_label_a), location = state.booking.locationA)
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = DIVIDER_GRAY)
                    Spacer(modifier = Modifier.height(24.dp))
                    LocationDetailItem(label = stringResource(R.string.location_label_b), location = state.booking.locationB)
                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = stringResource(R.string.booking_price), color = Color.Gray, fontSize = 20.sp)
                        Text(
                            "%.0f".format(state.booking.price),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onViewHistory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CONTAINER_YELLOW,
                            contentColor = Color.Black,
                        ),
                    ) {
                        Text(text = stringResource(R.string.label_v), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            is BookingUiState.Error ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text( text = stringResource(
                        R.string.booking_error,
                        state.message
                    ))
                }
        }
    }
}

@Composable
private fun LocationDetailItem(label: String, location: SelectedLocation) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(location.address, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.padding(start = 32.dp)) {
            Text(text = stringResource(R.string.aqi), color = Color.Gray, modifier = Modifier.width(100.dp))
            Text("${location.aqi ?: 0}", fontWeight = FontWeight.Bold)
        }
        if (location.nickname.isNotEmpty()) {
            Row(modifier = Modifier.padding(start = 32.dp)) {
                Text(text = stringResource(R.string.nickname), color = Color.Gray, modifier = Modifier.width(100.dp))
                Text(location.nickname, fontWeight = FontWeight.Bold)
            }
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

@Preview(showBackground = true, showSystemUi = true, name = "Booking — loading")
@Composable
private fun BookingScreenPreviewLoading() {
    TaxiOneTheme {
        BookingScreenContent(
            uiState = BookingUiState.Idle,
            onViewHistory = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Booking — success")
@Composable
private fun BookingScreenPreviewSuccess() {
    TaxiOneTheme {
        BookingScreenContent(
            uiState = BookingUiState.Success(
                Booking(
                    id = "1",
                    locationA = previewLocationA.copy(nickname = "Home"),
                    locationB = previewLocationB,
                    price = 1240.0,
                    createdAt = 0L,
                )
            ),
            onViewHistory = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Booking — error")
@Composable
private fun BookingScreenPreviewError() {
    TaxiOneTheme {
        BookingScreenContent(
            uiState = BookingUiState.Error("Network unavailable"),
            onViewHistory = {},
        )
    }
}