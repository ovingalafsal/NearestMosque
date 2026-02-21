package com.example.nearestmosque.presentation.details

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    destLat: Double,
    destLng: Double,
    viewModel: DetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effectFlow
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.setEvent(DetailsEvent.Init(destLat, destLng))
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is DetailsEffect.NavigateBack -> onNavigateBack()
                is DetailsEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Route to Mosque", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setEvent(DetailsEvent.OnBackClicked) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val destination = LatLng(destLat, destLng)
            val origin = if (state.originLat != 0.0) LatLng(state.originLat, state.originLng) else destination
            
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(destination, 15f)
            }

            // Update camera to bounds of origin and destination if route is loaded
            if (state.routePoints.isNotEmpty()) {
                LaunchedEffect(state.routePoints) {
                    val boundsBuilder = LatLngBounds.builder()
                    boundsBuilder.include(origin)
                    boundsBuilder.include(destination)
                    for (point in state.routePoints) {
                        boundsBuilder.include(point)
                    }
                    val bounds = boundsBuilder.build()
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(myLocationButtonEnabled = true),
                properties = MapProperties(isMyLocationEnabled = state.originLat != 0.0)
            ) {
                Marker(
                    state = MarkerState(position = destination),
                    title = "Mosque",
                    snippet = "Destination"
                )
                if (state.originLat != 0.0) {
                    Marker(
                        state = MarkerState(position = origin),
                        title = "You",
                        snippet = "Origin"
                    )
                }

                if (state.routePoints.isNotEmpty()) {
                    Polyline(
                        points = state.routePoints,
                        color = MaterialTheme.colorScheme.primary,
                        width = 10f
                    )
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (state.distance.isNotEmpty() && state.duration.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Walk to destination",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Distance", style = MaterialTheme.typography.labelMedium)
                                Text(state.distance, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Time", style = MaterialTheme.typography.labelMedium)
                                Text(state.duration, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
