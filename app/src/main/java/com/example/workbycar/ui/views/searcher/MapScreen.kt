package com.example.workbycar.ui.views.searcher

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, searcherViewModel: SearcherViewModel, isOrigin: Boolean){
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Found Trips") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Arrow back")
                }
            }
        )
    }) { paddingValues ->
        MapSection(searcherViewModel, paddingValues, isOrigin)
    }
}

@Composable
fun MapSection(searcherViewModel: SearcherViewModel, paddingValues: PaddingValues, isOrigin: Boolean){
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(if (isOrigin)
            searcherViewModel.selectedTrip!!.origincoordinates else
            searcherViewModel.selectedTrip!!.destinationcoordinates, 12f)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState
        ) {
            Polyline(
                points = PolyUtil.decode(searcherViewModel.selectedTrip!!.route?.overview_polyline?.points ?: ""),
                color = Color.Blue,
                width = 8f
            )

            Marker(state = MarkerState(position = searcherViewModel.selectedTrip!!.origincoordinates), title = "Origin")
            Marker(state = MarkerState(position = searcherViewModel.selectedTrip!!.destinationcoordinates), title = "Destination")
        }

        Button(
            onClick = {
                val coordinates = if (isOrigin)
                    searcherViewModel.selectedTrip!!.origincoordinates else
                    searcherViewModel.selectedTrip!!.destinationcoordinates

                val uri = Uri.parse("geo:${coordinates.latitude},${coordinates.longitude}?q=${coordinates.latitude},${coordinates.longitude}")

                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1),
                contentColor = Color.White
            )
        ) {
            Text(text = "Open in google maps")
        }
    }
}