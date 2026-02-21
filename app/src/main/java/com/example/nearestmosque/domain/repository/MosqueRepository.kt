package com.example.nearestmosque.domain.repository

import com.example.nearestmosque.domain.model.Mosque
import com.example.nearestmosque.domain.model.RouteInfo

interface MosqueRepository {
    suspend fun getNearestMosques(lat: Double, lng: Double): Result<List<Mosque>>
    suspend fun getWalkingRoute(originLat: Double, originLng: Double, destLat: Double, destLng: Double): Result<RouteInfo>
}
