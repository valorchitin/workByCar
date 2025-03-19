package com.example.workbycar.ui.view_models.postTrips

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workbycar.data.ApiKeyProvider
import com.example.workbycar.domain.model.Route
import com.example.workbycar.domain.model.Trip
import com.example.workbycar.domain.repository.AuthRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.example.workbycar.domain.repository.DirectionsAPIService
import com.example.workbycar.utils.CallBackHandle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class PostTripsViewModel @Inject constructor(private val authRepository: AuthRepository,
                                             private val placesClient: PlacesClient,
                                             private val geocoder: Geocoder,
                                             private val directionsAPIService: DirectionsAPIService,
                                             private val apiKeyProvider: ApiKeyProvider): ViewModel() {

        private var sessionToken = AutocompleteSessionToken.newInstance()
        private var auth: FirebaseAuth = FirebaseAuth.getInstance()
        var origin by mutableStateOf("")
        var destination by mutableStateOf("")
        var origincoordinates by mutableStateOf(LatLng(0.0, 0.0))
        var destinationcoordinates by mutableStateOf(LatLng(0.0, 0.0))
        var predictions by mutableStateOf(listOf<Pair<String, String>>())

        // Routes
        private val _routes = MutableLiveData<List<Route>>()
        val routes: LiveData<List<Route>> = _routes
        private val _selectedRouteIndex = MutableLiveData(0)
        val selectedRouteIndex: LiveData<Int> = _selectedRouteIndex
        var selectedRoute by mutableStateOf<Route?>(null)

        // Dates
        var dates by mutableStateOf(setOf<LocalDate>())
        var startOfWeek by mutableStateOf<LocalDate?>(null)
        var endOfWeek by mutableStateOf<LocalDate?>(null)

        // Departure hour
        var departureHour by mutableStateOf(LocalTime.now())

        // Passengers number
        var passengersNumber by mutableIntStateOf(3)

        // Reservation Type
        var automatedReservation by mutableStateOf(false)

        // Price
        var price by mutableIntStateOf(0)

        // Description
        var description by mutableStateOf("")

        fun onPlaceChange(newPlace: String){
            if (newPlace.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(newPlace)
                        .setSessionToken(sessionToken)
                        .build()

                    try {
                        val response = placesClient.findAutocompletePredictions(request).await()
                        predictions = response.autocompletePredictions.map { prediction ->
                            val primaryText = prediction.getPrimaryText(null).toString()
                            val secondaryText = prediction.getSecondaryText(null).toString()
                            "$primaryText, $secondaryText" to prediction.placeId
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        predictions = listOf("Error loading predictions" to "")
                    }
                }
            } else {
                predictions = emptyList()
            }
        }

        fun getCoordinates(address: String, type: Int){
            try {
                val address: List<Address>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocationName(address, 1)
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(address, 1)
                }
                address?.firstOrNull()?.let { location ->
                    if(type == 0){
                        origincoordinates = LatLng(location.latitude, location.longitude)
                    }
                    else {
                        destinationcoordinates = LatLng(location.latitude, location.longitude)
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }

        fun getRoutes(origin: LatLng, destination: LatLng) {
            viewModelScope.launch {
                try {
                    val originStr = "${origin.latitude},${origin.longitude}"
                    val destinationStr = "${destination.latitude},${destination.longitude}"
                    val apiKey = apiKeyProvider.getApiKey()

                    val response = directionsAPIService.getDirections(originStr, destinationStr, true, apiKey)

                    _routes.postValue(response.routes)
                } catch (e: Exception) {
                    Log.e("DirectionsAPI", "Error fetching routes: ${e.message}")
                }
            }
        }

        fun selectRoute(index: Int) {
            _selectedRouteIndex.value = index
        }

        fun increasePassengers() {
            if(passengersNumber < 8){
                passengersNumber++;
            }
        }

        fun decreasePassengers() {
            if(passengersNumber != 0){
                passengersNumber--;
            }
        }

        fun setReservationType(type: Boolean) {
            automatedReservation = type;
        }

        fun calculatePrice() {
            if (selectedRoute != null) {
                val kilometers = (selectedRoute!!.legs.firstOrNull()?.distance?.value ?: 0) / 1000

                val min = (kilometers * 0.05)
                val max = (kilometers * 0.08)

                price = (((min + max) / 2) * dates.size).toInt()

                if (price < 1) {
                    price = 1
                }
                else if (price > 120) {
                    price = 120
                }
            }
        }

        fun postTrip() {
            viewModelScope.launch {
                authRepository.getCurrentUser(CallBackHandle(
                    onSuccess = {user ->
                        val trip = Trip(
                            uid = user.uid,
                            origin = origin,
                            destination = destination,
                            origincoordinates = origincoordinates,
                            destinationcoordinates = destinationcoordinates,
                            route = selectedRoute,
                            dates = dates.map { date -> date.toString() },
                            startOfWeek = startOfWeek.toString(),
                            endOfWeek = endOfWeek.toString(),
                            departureHour = departureHour.toString(),
                            passengersNumber = passengersNumber,
                            automatedReservation = automatedReservation,
                            price = price,
                            description = description,
                            passengers = emptyList(),
                        )

                        FirebaseFirestore.getInstance().collection("trips").add(trip)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Trip added successfully!")
                                reset()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error adding trip", e)
                            }
                    },
                    onError = {
                        Log.e("SignUpViewModel", "Error al obtener el usario actual.")
                    }
                ))
            }
        }

        private fun reset() {
            origin = ""
            destination = ""
            origincoordinates = LatLng(0.0, 0.0)
            destinationcoordinates = LatLng(0.0, 0.0)
            predictions = emptyList()
            selectedRoute = null
            dates = emptySet()
            startOfWeek = null
            endOfWeek = null
            departureHour = LocalTime.now()
            passengersNumber = 3
            automatedReservation = false
            price = 0
            description = ""
        }
}