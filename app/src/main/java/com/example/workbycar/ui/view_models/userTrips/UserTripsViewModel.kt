package com.example.workbycar.ui.view_models.userTrips

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workbycar.domain.model.Leg
import com.example.workbycar.domain.model.Polyline
import com.example.workbycar.domain.model.Route
import com.example.workbycar.domain.model.TextValue
import com.example.workbycar.domain.model.Trip
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.utils.CallBackHandle
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserTripsViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    private val _userTrips = MutableLiveData<List<Trip>>()
    val trips: LiveData<List<Trip>> = _userTrips

    fun LoadTrips() {
        viewModelScope.launch {
            authRepository.getCurrentUser(CallBackHandle(
                onSuccess = {user ->
                    FirebaseFirestore.getInstance()
                        .collection("trips")
                        .whereEqualTo("uid", user.uid)
                        .get()
                        .addOnSuccessListener { response ->
                            val trips = response.documents.mapNotNull { trip ->
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

                                    tripObject
                                } catch (e: Exception) {
                                    Log.e(
                                        "TripDeserialization",
                                        "Error deserializing trip: ${e.message}"
                                    )
                                    null
                                }
                            }
                            _userTrips.postValue(trips)
                        }
                        .addOnFailureListener{ e ->
                            Log.e("UserTripsViewModel", "Error loading trips: ${e.message}")
                            _userTrips.postValue(emptyList())
                        }
                },
                onError = {
                    Log.e("UserTripsViewModel", "Error obtaining current user")
                }
            ))
        }
    }
}