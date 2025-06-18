package com.example.workbycar.ui.view_models.searcher

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workbycar.domain.model.Leg
import com.example.workbycar.domain.model.Polyline
import com.example.workbycar.domain.model.Route
import com.example.workbycar.domain.model.TextValue
import com.example.workbycar.domain.model.Trip
import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.utils.CallBackHandle
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SearcherViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val placesClient: PlacesClient,
    private val geocoder: Geocoder
): ViewModel() {

    private var sessionToken = AutocompleteSessionToken.newInstance()
    var isOrigin by mutableStateOf(false)
    var searcherPlace by mutableStateOf("")
    var searcherOrigin by mutableStateOf("")
    var searcherDestination by mutableStateOf("")
    var searcherOriginCoordinates by mutableStateOf(LatLng(0.0, 0.0))
    var searcherDestinationCoordinates by mutableStateOf(LatLng(0.0, 0.0))
    var searcherPredictions by mutableStateOf(listOf<Pair<String, String>>())
    var searcherStartOfWeek by mutableStateOf<LocalDate?>(null)
    var searcherEndOfWeek by mutableStateOf<LocalDate?>(null)

    private val _filteredTrips = MutableLiveData<List<Trip>>()
    val trips: LiveData<List<Trip>> = _filteredTrips

    var selectedTrip by mutableStateOf<Trip?>(null)
    private var tripId by mutableStateOf("")

    var driver by mutableStateOf<UserLogged?>(null)
    var passengers by mutableStateOf<List<UserLogged>>(emptyList())

    var userId by mutableStateOf("")

    fun onPlaceChange(newPlace: String){
        if (newPlace.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(newPlace)
                    .setSessionToken(sessionToken)
                    .build()

                try {
                    val response = placesClient.findAutocompletePredictions(request).await()
                    searcherPredictions = response.autocompletePredictions.map { prediction ->
                        val primaryText = prediction.getPrimaryText(null).toString()
                        val secondaryText = prediction.getSecondaryText(null).toString()
                        "$primaryText, $secondaryText" to prediction.placeId
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    searcherPredictions = listOf("Error loading predictions" to "")
                }
            }
        } else {
            searcherPredictions = emptyList()
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
                    searcherOriginCoordinates = LatLng(location.latitude, location.longitude)
                }
                else {
                    searcherDestinationCoordinates = LatLng(location.latitude, location.longitude)
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun search() {
        val margin = 0.1

        FirebaseFirestore.getInstance()
            .collection("trips")
            .whereEqualTo("startOfWeek", searcherStartOfWeek.toString())
            .whereEqualTo("endOfWeek", searcherEndOfWeek.toString())
            .get()
            .addOnSuccessListener { response ->
                val filteredTrips = response.documents.mapNotNull { trip ->
                    try {
                        val originLatitude = trip.getDouble("origincoordinates.latitude") ?: 0.0
                        val originLongitude = trip.getDouble("origincoordinates.longitude") ?: 0.0
                        val destinationLatitude = trip.getDouble("destinationcoordinates.latitude") ?: 0.0
                        val destinationLongitude = trip.getDouble("destinationcoordinates.longitude") ?: 0.0

                        val routeMap = trip["route"] as? Map<String, Any>
                        val route = routeMap?.let {
                            val overviewPolyline = it["overview_polyline"] as? Map<String, Any>
                            val points = overviewPolyline?.get("points") as? String ?: ""

                            val legsList = it["legs"] as? List<Map<String, Any>> ?: emptyList()
                            val legs = legsList.map { leg ->
                                val distanceMap = leg["distance"] as? Map<String, Any>
                                val durationMap = leg["duration"] as? Map<String, Any>

                                Leg(
                                    distance = TextValue(
                                        text = distanceMap?.get("text") as? String ?: "",
                                        value = (distanceMap?.get("value") as? Number)?.toInt() ?: 0
                                    ),
                                    duration = TextValue(
                                        text = durationMap?.get("text") as? String ?: "",
                                        value = (durationMap?.get("value") as? Number)?.toInt() ?: 0
                                    )
                                )
                            }

                            Route(
                                overview_polyline = Polyline(points = points),
                                legs = legs
                            )
                        }

                        val tripObject = Trip(
                            tripId = trip.id,
                            uid = trip.getString("uid") ?: "",
                            description = trip.getString("description") ?: "",
                            departureHour = trip.getString("departureHour") ?: "",
                            origin = trip.getString("origin") ?: "",
                            destination = trip.getString("destination") ?: "",
                            origincoordinates = LatLng(originLatitude, originLongitude),
                            destinationcoordinates = LatLng(destinationLatitude, destinationLongitude),
                            passengersNumber = trip.getLong("passengersNumber")?.toInt() ?: 0,
                            price = trip.getLong("price")?.toInt() ?: 0,
                            route = route,
                            automatedReservation = trip.getBoolean("automatedReservation") ?: false,
                            dates = trip["dates"] as? List<String> ?: emptyList(),
                            startOfWeek = trip.getString("startOfWeek") ?: "",
                            endOfWeek = trip.getString("endOfWeek") ?: "",
                            passengers = trip["passengers"] as? List<String> ?: emptyList()
                        )

                        if (tripObject.origincoordinates.latitude in (searcherOriginCoordinates.latitude - margin)..(searcherOriginCoordinates.latitude + margin) &&
                            tripObject.origincoordinates.longitude in (searcherOriginCoordinates.longitude - margin)..(searcherOriginCoordinates.longitude + margin) &&
                            tripObject.destinationcoordinates.latitude in (searcherDestinationCoordinates.latitude - margin)..(searcherDestinationCoordinates.latitude + margin) &&
                            tripObject.destinationcoordinates.longitude in (searcherDestinationCoordinates.longitude - margin)..(searcherDestinationCoordinates.longitude + margin)
                        ) {
                            tripObject
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("TripDeserialization", "Error deserializing trip: ${e.message}")
                        null
                    }
                }
                _filteredTrips.postValue(filteredTrips)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error searching trips: ${e.message}")
                _filteredTrips.postValue(emptyList())
            }
    }


    fun resetSearcher() {
        searcherOrigin = ""
        searcherDestination = ""
        searcherOriginCoordinates = LatLng(0.0, 0.0)
        searcherDestinationCoordinates = LatLng(0.0, 0.0)
        searcherPredictions = emptyList()
        searcherStartOfWeek = null
        searcherEndOfWeek = null
    }

    fun getDriver() {
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(selectedTrip!!.uid)
            .get()
            .addOnSuccessListener { document ->
                driver = document.toObject(UserLogged::class.java)
            }
    }

    fun getPassengers() {
        val passengerList = mutableListOf<UserLogged>()

        selectedTrip!!.passengers.forEach { passengerId ->
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(passengerId)
                .get()
                .addOnSuccessListener { document ->
                    document.toObject(UserLogged::class.java).let { passenger ->
                        if (passenger != null) {
                            passengerList.add(passenger)

                            if (passengerList.size == passengerList.size) {
                                passengers = passengerList
                            }
                        }
                    }
                }
        }
    }

    fun bookASeat() {
        if (userId !== ""){
            FirebaseFirestore.getInstance()
                .collection("trips")
                .document(selectedTrip!!.tripId)
                .update("passengers",
                    com.google.firebase.firestore.FieldValue.arrayUnion(userId)
                )
                .addOnSuccessListener {
                    println("Seat booked successfully!")
                }
                .addOnFailureListener { e ->
                    println("Error booking seat: ${e.message}")
                }
        } else {
            println("No user logged in")
        }
    }

    fun getUserId(){
        viewModelScope.launch {
            authRepository.getCurrentUser(CallBackHandle(
                onSuccess = { user ->
                    userId = user.uid
                },
                onError = {
                    Log.e("ProfileViewModel", "Error al acceder a la informacion del usuario: $it")
                }
            ))
        }
    }

    fun getResizedBitmap(context: Context, resId: Int, width: Int, height: Int): BitmapDescriptor {
        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }
}