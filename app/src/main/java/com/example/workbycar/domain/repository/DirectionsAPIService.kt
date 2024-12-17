package com.example.workbycar.domain.repository

import com.example.workbycar.domain.model.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsAPIService {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("alternatives") alternatives: Boolean = true,
        @Query("key") apiKey: String
    ): DirectionsResponse
}