package com.example.nearestmosque.domain.model

data class Mosque(
    val id: String,
    val name: String,
    val vicinity: String,
    val lat: Double,
    val lng: Double,
    val rating: Double?,
    val userRatingsTotal: Int?
)
