package com.example.nearestmosque.presentation.home

import androidx.lifecycle.viewModelScope
import com.example.nearestmosque.domain.repository.LocationTracker
import com.example.nearestmosque.domain.repository.MosqueRepository
import com.example.nearestmosque.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationTracker: LocationTracker,
    private val mosqueRepository: MosqueRepository
) : BaseViewModel<HomeEvent, HomeState, HomeEffect>() {

    override fun createInitialState(): HomeState = HomeState()

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnPermissionGranted -> {
                setState { copy(hasLocationPermission = true) }
                setEvent(HomeEvent.LoadNearestMosques)
            }
            is HomeEvent.OnPermissionDenied -> {
                setState { copy(hasLocationPermission = false, error = "Location permission is required to find nearest mosques.") }
            }
            is HomeEvent.LoadNearestMosques -> fetchMosques()
            is HomeEvent.OnMosqueClicked -> {
                setEffect { HomeEffect.NavigateToDetails(event.mosque.id, event.mosque.lat, event.mosque.lng) }
            }
        }
    }

    private fun fetchMosques() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                val result = mosqueRepository.getNearestMosques(location.latitude, location.longitude)
                result.fold(
                    onSuccess = { mosques ->
                        setState { copy(isLoading = false, mosques = mosques) }
                    },
                    onFailure = { error ->
                        setState { copy(isLoading = false, error = error.message ?: "Failed to fetch nearby mosques") }
                        setEffect { HomeEffect.ShowToast(error.message ?: "Error occurred") }
                    }
                )
            } else {
                setState { copy(isLoading = false, error = "Could not retrieve current location. Please ensure GPS is enabled.") }
            }
        }
    }
}
