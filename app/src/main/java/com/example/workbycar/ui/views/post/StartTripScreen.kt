package com.example.workbycar.ui.views.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.view_models.postTrips.PostTripsViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartTripScreen(navController: NavController){//, postTripsViewModel: PostTripsViewModel){
    Scaffold(topBar = {
            TopAppBar(
                title = {Text(text = "StartTripScreen")},
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Arrow back")
                    }
                }
            )
    }) { paddingValues ->
            StartTripContent(Modifier.padding(paddingValues))
    }
}

@Composable
fun StartTripContent(modifier: Modifier = Modifier){
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Start Trip Screen")
        OriginTextView()
    }
}

@Composable
fun OriginTextView() {
    val context = LocalContext.current
    val placesClient = Places.createClient(context)
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    var query by remember { mutableStateOf(TextFieldValue("")) }
    var predictions by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var selectedPlaceDetails by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        TextField(
            value = query,
            onValueChange = { newQuery ->
                query = newQuery
                if (newQuery.text.isNotEmpty()) {
                    scope.launch(Dispatchers.IO) {
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(newQuery.text)
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
                            predictions = listOf("Error al cargar predicciones" to "")
                        }
                    }
                } else {
                    predictions = emptyList()
                }
            },
            label = { Text("Buscar lugar") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(predictions) { (predictionText, placeId) ->
                Text(
                    text = predictionText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            // Fetch place details on click
                            scope.launch(Dispatchers.IO) {
                                val placeRequest = FetchPlaceRequest.builder(placeId, listOf(Place.Field.ADDRESS, Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS))
                                    .build()
                                try {
                                    val placeResponse = placesClient.fetchPlace(placeRequest).await()
                                    val place = placeResponse.place
                                    val addressComponents = place.addressComponents?.asList()
                                    val country = addressComponents?.find { it.types.contains("country") }?.name
                                    val city = addressComponents?.find { it.types.contains("locality") }?.name

                                    selectedPlaceDetails = "Name: ${place.name}, City: $city, Country: $country"
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    selectedPlaceDetails = "Error al obtener detalles del lugar"
                                }
                            }
                        },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedPlaceDetails?.let { details ->
            Text(
                text = details,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}