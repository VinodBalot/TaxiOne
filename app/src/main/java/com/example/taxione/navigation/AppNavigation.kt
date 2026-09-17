package com.example.taxione.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.taxione.domain.model.Slot
import com.example.taxione.feature.booking.BookingScreen
import com.example.taxione.feature.cachedlocations.CachedLocationsScreen
import com.example.taxione.feature.detail.DetailScreen
import com.example.taxione.feature.history.HistoryScreen
import com.example.taxione.feature.map.MapScreen
import kotlinx.serialization.Serializable

@Serializable
object MapRoute

@Serializable
data class DetailRoute(val slot: Slot)

@Serializable
object BookingRoute

@Serializable
object HistoryRoute

@Serializable
data class CachedLocationsRoute(val slot: Slot)

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = MapRoute, modifier = modifier) {
        composable<MapRoute> {
            MapScreen(navController = navController)
        }
        composable<DetailRoute> { entry ->
            val route = entry.toRoute<DetailRoute>()
            DetailScreen(navController = navController, slot = route.slot)
        }
        composable<BookingRoute> {
            BookingScreen(navController = navController)
        }
        composable<HistoryRoute> {
            HistoryScreen(navController = navController)
        }
        composable<CachedLocationsRoute> { entry ->
            val route = entry.toRoute<CachedLocationsRoute>()
            CachedLocationsScreen(navController = navController, slot = route.slot)
        }
    }
}
