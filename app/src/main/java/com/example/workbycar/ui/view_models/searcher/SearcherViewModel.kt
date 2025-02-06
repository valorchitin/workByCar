package com.example.workbycar.ui.view_models.searcher

import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workbycar.domain.repository.AuthRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
}