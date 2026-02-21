package com.example.nearestmosque.presentation.details

import androidx.lifecycle.viewModelScope
import com.example.nearestmosque.domain.repository.LocationTracker
import com.example.nearestmosque.domain.repository.MosqueRepository
import com.example.nearestmosque.presentation.base.BaseViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val locationTracker: LocationTracker,
    private val mosqueRepository: MosqueRepository
) : BaseViewModel<DetailsEvent, DetailsState, DetailsEffect>() {

    override fun createInitialState(): DetailsState = DetailsState()

    override fun handleEvent(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.Init -> {
                setState { copy(destLat = event.destLat, destLng = event.destLng) }
                setEvent(DetailsEvent.LoadRoute)
            }
            is DetailsEvent.LoadRoute -> fetchRoute()
            is DetailsEvent.OnBackClicked -> setEffect { DetailsEffect.NavigateBack }
        }
    }

    private fun fetchRoute() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                setState { copy(originLat = location.latitude, originLng = location.longitude) }
                val state = uiState.value
                val result = mosqueRepository.getWalkingRoute(
                    originLat = location.latitude,
                    originLng = location.longitude,
                    destLat = state.destLat,
                    destLng = state.destLng
                )
                
                result.fold(
                    onSuccess = { routeInfo ->
                        val latLngPoints = routeInfo.points.map { LatLng(it.lat, it.lng) }
                        setState { 
                            copy(
                                isLoading = false,
                                routePoints = latLngPoints,
                                distance = routeInfo.distanceText,
                                duration = routeInfo.durationText
                            ) 
                        }
                    },
                    onFailure = { error ->
                        setState { copy(isLoading = false, error = error.message ?: "Failed to fetch route") }
                        setEffect { DetailsEffect.ShowToast(error.message ?: "Failed to find walking route") }
                    }
                )
            } else {
                setState { copy(isLoading = false, error = "Could not get current location for routing") }
                setEffect { DetailsEffect.ShowToast("Location unavailable") }
            }
        }
    }
}
