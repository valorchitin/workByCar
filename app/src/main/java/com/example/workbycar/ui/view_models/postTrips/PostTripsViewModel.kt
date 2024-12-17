package com.example.workbycar.ui.view_models.postTrips

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
import com.example.workbycar.R
import com.example.workbycar.data.ApiKeyProvider
import com.example.workbycar.domain.model.Route
import com.example.workbycar.domain.repository.AuthRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.example.workbycar.domain.repository.DirectionsAPIService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PostTripsViewModel @Inject constructor(private val authRepository: AuthRepository,
                                             private val placesClient: PlacesClient,
                                             private val geocoder: Geocoder,
                                             private val directionsAPIService: DirectionsAPIService,
                                             private val apiKeyProvider: ApiKeyProvider): ViewModel() {
        private var sessionToken = AutocompleteSessionToken.newInstance()
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

        fun decodePolyline(encoded: String): List<LatLng> {
            val poly = ArrayList<LatLng>()
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
                val dLat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lat += dLat

                shift = 0
                result = 0
                do {
                    b = encoded[index++].code - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dLng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lng += dLng

                poly.add(LatLng(lat / 1E5, lng / 1E5))
            }

            return poly
        }
}