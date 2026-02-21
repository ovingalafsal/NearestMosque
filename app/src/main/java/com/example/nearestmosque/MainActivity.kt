package com.example.nearestmosque

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nearestmosque.presentation.details.DetailsScreen
import com.example.nearestmosque.presentation.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onNavigateToDetails = { mosqueId, lat, lng ->
                                    navController.navigate("details/$lat/$lng")
                                }
                            )
                        }
                        composable(
                            route = "details/{lat}/{lng}",
                            arguments = listOf(
                                navArgument("lat") { type = NavType.FloatType },
                                navArgument("lng") { type = NavType.FloatType }
                            )
                        ) { backStackEntry ->
                            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
                            val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 0.0
                            DetailsScreen(
                                destLat = lat,
                                destLng = lng,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
