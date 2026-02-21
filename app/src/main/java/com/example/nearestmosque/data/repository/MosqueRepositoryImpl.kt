package com.example.nearestmosque.data.repository

import com.example.nearestmosque.data.remote.GoogleMapsApi
import com.example.nearestmosque.domain.model.Mosque
import com.example.nearestmosque.domain.model.RouteInfo
import com.example.nearestmosque.domain.model.RoutePoint
import com.example.nearestmosque.domain.repository.MosqueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MosqueRepositoryImpl @Inject constructor(
    private val api: GoogleMapsApi,
    private val apiKey: String
) : MosqueRepository {

    override suspend fun getNearestMosques(lat: Double, lng: Double): Result<List<Mosque>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getNearbyMosques(
                    location = "$lat,$lng",
                    apiKey = apiKey
                )

                if (response.status == "OK" || response.status == "ZERO_RESULTS") {
                    val mosques = response.results.map { dto ->
                        Mosque(
                            id = dto.placeId,
                            name = dto.name,
                            vicinity = dto.vicinity ?: "",
                            lat = dto.geometry.location.lat,
                            lng = dto.geometry.location.lng,
                            rating = dto.rating,
                            userRatingsTotal = dto.userRatingsTotal
                        )
                    }
                    Result.success(mosques)
                } else {
                    Result.failure(Exception("Places API Error: ${response.status}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getWalkingRoute(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ): Result<RouteInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getDirections(
                    origin = "$originLat,$originLng",
                    destination = "$destLat,$destLng",
                    apiKey = apiKey
                )

                if (response.status == "OK" && response.routes.isNotEmpty()) {
                    val routeDto = response.routes.first()
                    val legDto = routeDto.legs.firstOrNull()
                    val polylineString = routeDto.overviewPolyline.points

                    val decodedPath = decodePolyline(polylineString)

                    val routeInfo = RouteInfo(
                        points = decodedPath,
                        distanceText = legDto?.distance?.text ?: "Unknown",
                        durationText = legDto?.duration?.text ?: "Unknown"
                    )
                    Result.success(routeInfo)
                } else {
                    Result.failure(Exception("Directions API Error: ${response.status}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Decodes an encoded path string into a sequence of LatLngs.
     */
    private fun decodePolyline(encoded: String): List<RoutePoint> {
        val poly = ArrayList<RoutePoint>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = RoutePoint(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }
}
