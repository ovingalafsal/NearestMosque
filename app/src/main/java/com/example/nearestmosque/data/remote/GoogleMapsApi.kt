package com.example.nearestmosque.data.remote

import com.example.nearestmosque.data.remote.dto.DirectionsResponse
import com.example.nearestmosque.data.remote.dto.PlacesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsApi {

    @GET("place/nearbysearch/json")
    suspend fun getNearbyMosques(
        @Query("location") location: String, // "lat,lng"
        @Query("radius") radius: Int = 5000,
        @Query("type") type: String = "mosque",
        @Query("keyword") keyword: String = "masjid",
        @Query("key") apiKey: String
    ): PlacesResponse

    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String, // "lat,lng"
        @Query("destination") destination: String, // "lat,lng"
        @Query("mode") mode: String = "walking",
        @Query("key") apiKey: String
    ): DirectionsResponse
}
