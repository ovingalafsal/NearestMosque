package com.example.nearestmosque.domain.repository

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}
