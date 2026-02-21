package com.example.nearestmosque.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PlacesResponse(
    @SerializedName("results") val results: List<PlaceDto>,
    @SerializedName("status") val status: String
)

data class PlaceDto(
    @SerializedName("place_id") val placeId: String,
    @SerializedName("name") val name: String,
    @SerializedName("vicinity") val vicinity: String?,
    @SerializedName("geometry") val geometry: GeometryDto,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("user_ratings_total") val userRatingsTotal: Int?
)

data class GeometryDto(
    @SerializedName("location") val location: LocationDto
)

data class LocationDto(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)
