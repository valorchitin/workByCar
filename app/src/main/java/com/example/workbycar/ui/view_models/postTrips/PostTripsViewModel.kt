package com.example.workbycar.ui.view_models.postTrips

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workbycar.domain.repository.AuthRepository
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PostTripsViewModel @Inject constructor(private val authRepository: AuthRepository,
    val placesClient: PlacesClient): ViewModel() {
        private var sessionToken = AutocompleteSessionToken.newInstance()
        var origin by mutableStateOf("")
        var predictions by mutableStateOf(listOf<Pair<String, String>>())

        fun onOriginChange(newOrigin: String){
            if (newOrigin.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(newOrigin)
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
}