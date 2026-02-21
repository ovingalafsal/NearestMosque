package com.example.nearestmosque.domain.model

data class RoutePoint(val lat: Double, val lng: Double)

data class RouteInfo(
    val points: List<RoutePoint>,
    val distanceText: String,
    val durationText: String
)
