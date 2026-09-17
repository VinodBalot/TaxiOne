package com.example.taxione.feature.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.ContextCompat
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.taxione.R
import com.example.taxione.domain.model.SelectedLocation
import com.example.taxione.domain.model.Slot
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.taxione.navigation.BookingRoute
import com.example.taxione.navigation.CachedLocationsRoute
import com.example.taxione.navigation.DetailRoute
import com.example.taxione.ui.theme.CONTAINER_YELLOW
import com.example.taxione.ui.theme.GRAY
import com.example.taxione.ui.theme.SURFACE_GRAY
import com.example.taxione.ui.theme.TaxiOneTheme
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val cameraPositionState = rememberCameraPositionState()

    var locationMoved by rememberSaveable { mutableStateOf(false) }

    val selection by viewModel.selection.collectAsStateWithLifecycle()
    val centerAqiState by viewModel.centerAqiState.collectAsStateWithLifecycle()
    val slotActionState by viewModel.slotActionState.collectAsStateWithLifecycle()
    val bookingActionState by viewModel.bookingActionState.collectAsStateWithLifecycle()

    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.position to cameraPositionState.isMoving }
            .collect { (position, isMoving) -> viewModel.onCameraChanged(position, isMoving) }
    }

    var isPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val moveToCurrentLocation: suspend () -> Unit = {
        try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val location = suspendCancellableCoroutine { cont ->
                fusedClient.lastLocation
                    .addOnSuccessListener { loc -> cont.resume(loc) }
                    .addOnFailureListener { cont.resume(null) }
            }
            location?.let {
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 14f)
                    )
                )
            }
        } catch (_: Exception) {}
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        isPermissionGranted = granted
        if (granted) {
            locationMoved = true
            scope.launch { moveToCurrentLocation() }
        }
    }

    LaunchedEffect(Unit) {
        if (!locationMoved) {
            if (isPermissionGranted) {
                locationMoved = true
                moveToCurrentLocation()
            } else {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.cameraTargetEvents.collect { target ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLng(LatLng(target.latitude, target.longitude))
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToBooking.collect {
            navController.navigate(BookingRoute)
        }
    }

    LaunchedEffect(slotActionState) {
        if (slotActionState is SlotActionState.Error) {
            snackbarHostState.showSnackbar((slotActionState as SlotActionState.Error).message)
            viewModel.clearSlotActionError()
        }
    }

    LaunchedEffect(bookingActionState) {
        if (bookingActionState is BookingActionState.Error) {
            snackbarHostState.showSnackbar((bookingActionState as BookingActionState.Error).message)
            viewModel.clearBookingError()
        }
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false
            )
        )
    }

    MapScreenContent(
        selection = selection,
        aqiState = centerAqiState,
        slotActionState = slotActionState,
        bookingActionState = bookingActionState,
        snackbarHostState = snackbarHostState,
        onSlotAClick = {
            if (selection.a != null) navController.navigate(DetailRoute(Slot.A))
            else navController.navigate(CachedLocationsRoute(Slot.A))
        },
        onSlotBClick = {
            if (selection.b != null) navController.navigate(DetailRoute(Slot.B))
            else navController.navigate(CachedLocationsRoute(Slot.B))
        },
        onVButtonClick = {
            if (selection.buttonLabel == "Book") viewModel.book()
            else viewModel.onVButtonTapped(cameraPositionState.position)
        },
        mapContent = {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                properties = MapProperties(
                    isMyLocationEnabled = isPermissionGranted
                )
            )
        },
    )
}

@Composable
private fun MapScreenContent(
    selection: SelectionUiState,
    aqiState: CenterAqiUiState,
    slotActionState: SlotActionState,
    bookingActionState: BookingActionState,
    snackbarHostState: SnackbarHostState,
    onSlotAClick: () -> Unit,
    onSlotBClick: () -> Unit,
    onVButtonClick: () -> Unit,
    mapContent: @Composable () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
    ) { contentPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AqiChip(state = aqiState)
            }

            Box(modifier = Modifier.weight(1f)) {
                mapContent()
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.icon_pin),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Unspecified,
                    )
                }
            }

            Surface (
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SlotLabel(
                            label = "A",
                            displayName = selection.a?.displayName,
                            modifier = Modifier.weight(1f),
                            onClick = onSlotAClick,
                        )
                        SlotLabel(
                            label = "B",
                            displayName = selection.b?.displayName,
                            modifier = Modifier.weight(1f),
                            onClick = onSlotBClick,
                        )
                    }

                    if (slotActionState is SlotActionState.Loading || bookingActionState is BookingActionState.Loading) {
                        Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Button(
                            onClick = onVButtonClick,
                            modifier = Modifier
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CONTAINER_YELLOW,
                                contentColor = Color.Black,
                            ),
                        ) {
                            Text(
                                selection.buttonLabel,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AqiChip(state: CenterAqiUiState, modifier: Modifier = Modifier) {

        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("aqi", color = GRAY, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            when (state) {
                is CenterAqiUiState.Idle -> Text("–", fontWeight = FontWeight.Bold, color = Color.Black)
                is CenterAqiUiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                is CenterAqiUiState.Success -> Text("${state.aqi}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                is CenterAqiUiState.Error -> Text("!", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }

}

@Composable
private fun SlotLabel(
    label: String,
    displayName: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = SURFACE_GRAY,
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = displayName ?: "",
                maxLines = 1,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Map — Book (both filled)")
@Composable
private fun MapScreenPreviewBothFilled() {
    TaxiOneTheme {
        MapScreenContent(
            selection = SelectionUiState(
                a = SelectedLocation(
                    com.example.taxione.domain.model.LatLng(37.564, 127.001),
                    42,
                    "Seocho District, Yangjae 2(i)-dong"
                ),
                b = SelectedLocation(com.example.taxione.domain.model.LatLng(37.500, 127.050), 35, "Gangnam District, Yeoksam 1(il)-dong"),
            ),
            aqiState = CenterAqiUiState.Success(42),
            slotActionState = SlotActionState.Idle,
            bookingActionState = BookingActionState.Idle,
            snackbarHostState = remember { SnackbarHostState() },

            onSlotAClick = {},
            onSlotBClick = {},
            onVButtonClick = {},
            mapContent = { Box(Modifier.fillMaxSize().background(Color(0xFFD8E8D8))) },
        )
    }
}
