package com.example.nearestmosque.presentation.home

import com.example.nearestmosque.domain.model.Mosque
import com.example.nearestmosque.presentation.base.UiEffect
import com.example.nearestmosque.presentation.base.UiEvent
import com.example.nearestmosque.presentation.base.UiState

data class HomeState(
    val isLoading: Boolean = false,
    val mosques: List<Mosque> = emptyList(),
    val error: String? = null,
    val hasLocationPermission: Boolean = false
) : UiState

sealed class HomeEvent : UiEvent {
    object OnPermissionGranted : HomeEvent()
    object OnPermissionDenied : HomeEvent()
    object LoadNearestMosques : HomeEvent()
    data class OnMosqueClicked(val mosque: Mosque) : HomeEvent()
}

sealed class HomeEffect : UiEffect {
    data class NavigateToDetails(val mosqueId: String, val lat: Double, val lng: Double) : HomeEffect()
    data class ShowToast(val message: String) : HomeEffect()
}
