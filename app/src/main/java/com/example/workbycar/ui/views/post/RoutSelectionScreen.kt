package com.example.workbycar.ui.views.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.postTrips.PostTripsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(navController: NavController, postTripsViewModel: PostTripsViewModel){
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "RouteSelectionScreen") },
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
            Routes(postTripsViewModel, navController)
        }
    }
}

@Composable
fun Routes(
    postTripsViewModel: PostTripsViewModel,
    navController: NavController,
) {
    val routes by postTripsViewModel.routes.observeAsState(emptyList())
    val selectedRouteIndex by postTripsViewModel.selectedRouteIndex.observeAsState(0)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(postTripsViewModel.origincoordinates, 12f)
    }

    LaunchedEffect(Unit) {
        postTripsViewModel.getRoutes(postTripsViewModel.origincoordinates, postTripsViewModel.destinationcoordinates)
    }

    Column(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight(0.5f),
            cameraPositionState = cameraPositionState
        ) {
            routes.forEachIndexed { index, route ->
                if (index == selectedRouteIndex) {
                    Polyline(
                        points = PolyUtil.decode(route.overview_polyline.points),
                        color = Color.Blue,
                        width = 8f
                    )
                }
            }

            Marker(state = MarkerState(position = postTripsViewModel.origincoordinates), title = "Origin")
            Marker(state = MarkerState(position = postTripsViewModel.destinationcoordinates), title = "Destination")
        }

        Text("¿What is your route?",
            fontSize = 20.sp)

        if (routes.isNotEmpty()) {
            LazyColumn (
                Modifier.fillMaxWidth()
            ) {
                items(routes.take(3)) { route ->
                    val index = routes.indexOf(route)
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            "Option ${index + 1}",
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        RadioButton(
                            onClick = { postTripsViewModel.selectRoute(index) },
                            selected = (index == selectedRouteIndex),
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                postTripsViewModel.selectedRoute = postTripsViewModel.routes.value?.get(selectedRouteIndex)
                navController.navigate(AppScreens.DateSelectionScreen.route)
            }
        ) {
            Text("Continue")
        }
    }
}