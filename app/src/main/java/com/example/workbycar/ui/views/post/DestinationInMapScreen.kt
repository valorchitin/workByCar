package com.example.workbycar.ui.views.post

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.postTrips.PostTripsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationInMapScreen(navController: NavController, postTripsViewModel: PostTripsViewModel){
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "StartTripScreen") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Arrow back")
                }
            }
        )
    }) { paddingValues ->
        Column (
            modifier = Modifier.padding(paddingValues)
        ){
            Text("Coordinates: ${postTripsViewModel.destinationcoordinates}")
            SelectDestinationMap (navController, postTripsViewModel) {selectedLocation ->
                Log.d("SelectLocationMap", "Ubicación seleccionada: ${selectedLocation.latitude}, ${selectedLocation.longitude}")
            }
        }

    }
}


@Composable
fun SelectDestinationMap(
    navController: NavController,
    postTripsViewModel: PostTripsViewModel,
    onLocationSelected: (LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(postTripsViewModel.destinationcoordinates, 15f)
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { newCoordinates ->
                Log.d("NewLocation", "New coordinates: $newCoordinates")
                postTripsViewModel.destinationcoordinates = newCoordinates
            }
        ){
            Marker(
                state = MarkerState(position = postTripsViewModel.destinationcoordinates),
                title = "Selected Location"
            )
        }

        Button(
            onClick = {
                onLocationSelected(postTripsViewModel.destinationcoordinates)
                navController.navigate(AppScreens.RouteSelectionScreen.route)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Confirm Location")
        }
    }
}