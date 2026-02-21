package com.example.nearestmosque.presentation.details

import com.example.nearestmosque.domain.model.RoutePoint
import com.example.nearestmosque.presentation.base.UiEffect
import com.example.nearestmosque.presentation.base.UiEvent
import com.example.nearestmosque.presentation.base.UiState
import com.google.android.gms.maps.model.LatLng

data class DetailsState(
    val isLoading: Boolean = false,
    val originLat: Double = 0.0,
    val originLng: Double = 0.0,
    val destLat: Double = 0.0,
    val destLng: Double = 0.0,
    val routePoints: List<LatLng> = emptyList(),
    val distance: String = "",
    val duration: String = "",
    val error: String? = null
) : UiState

sealed class DetailsEvent : UiEvent {
    data class Init(val destLat: Double, val destLng: Double) : DetailsEvent()
    object LoadRoute : DetailsEvent()
    object OnBackClicked : DetailsEvent()
}

sealed class DetailsEffect : UiEffect {
    object NavigateBack : DetailsEffect()
    data class ShowToast(val message: String) : DetailsEffect()
}
