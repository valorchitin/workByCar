package com.example.workbycar.domain.model

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val overview_polyline: Polyline,
    val legs: List<Leg>
)

data class Polyline(
    val points: String
)

data class Leg(
    val distance: TextValue,
    val duration: TextValue
)

data class TextValue(
    val text: String,
    val value: Int
)