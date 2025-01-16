package com.example.workbycar.domain.model

data class DirectionsResponse(
    val routes: List<Route>
){
    constructor() : this(listOf())
}

data class Route(
    val overview_polyline: Polyline,
    val legs: List<Leg>
){
    constructor() : this(Polyline(""), listOf())
}

data class Polyline(
    val points: String
){
    constructor() : this("")
}

data class Leg(
    val distance: TextValue,
    val duration: TextValue
){
    constructor() : this(TextValue("", 0), TextValue("", 0))
}

data class TextValue(
    val text: String,
    val value: Int
){
    constructor() : this("", 0)
}