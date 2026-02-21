package com.example.nearestmosque.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DirectionsResponse(
    @SerializedName("routes") val routes: List<RouteDto>,
    @SerializedName("status") val status: String
)

data class RouteDto(
    @SerializedName("legs") val legs: List<LegDto>,
    @SerializedName("overview_polyline") val overviewPolyline: PolylineDto
)

data class LegDto(
    @SerializedName("distance") val distance: TextValueDto,
    @SerializedName("duration") val duration: TextValueDto
)

data class PolylineDto(
    @SerializedName("points") val points: String
)

data class TextValueDto(
    @SerializedName("text") val text: String,
    @SerializedName("value") val value: Int
)
