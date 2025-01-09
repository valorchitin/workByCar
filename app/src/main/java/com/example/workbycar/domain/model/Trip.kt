package com.example.workbycar.domain.model

import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.time.LocalTime

data class Trip(
    val uid: String,
    val origin: String,
    val destination: String,
    val origincoordinates: LatLng,
    val destinationcoordinates: LatLng,
    val route: Route?,
    val dates: List<String>,
    val departureHour: String,
    val passengersNumber: Int,
    val automatedReservation: Boolean,
    val price: Int,
)