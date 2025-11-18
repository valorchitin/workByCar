package com.example.workbycar.ui.views.post

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.workbycar.R
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.postTrips.PostTripsViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(navController: NavController, postTripsViewModel: PostTripsViewModel){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Arrow back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Routes(postTripsViewModel, navController)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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
        postTripsViewModel.getRoutes(
            postTripsViewModel.origincoordinates,
            postTripsViewModel.destinationcoordinates
        )
    }

    Column(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f),
            cameraPositionState = cameraPositionState
        ) {
            routes.forEachIndexed { index, route ->
                if (index == selectedRouteIndex) {
                    Polyline(
                        points = PolyUtil.decode(route.overview_polyline.points),
                        color = Color(0xFF0277BD),
                        width = 8f
                    )
                }
            }

            Marker(
                state = MarkerState(position = postTripsViewModel.origincoordinates),
                title = "Origin",
                icon = postTripsViewModel.getResizedBitmap(LocalContext.current, R.drawable.rec, 50, 50)
            )

            Marker(
                state = MarkerState(position = postTripsViewModel.destinationcoordinates),
                title = "Destination",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }

        Text(
            text = "Select your preferred route",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0277BD),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp)
        )

        if (routes.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(routes.take(3)) { route ->
                    val index = routes.indexOf(route)
                    val totalDistanceInMeters = route.legs.sumOf { it.distance.value }
                    val totalDistanceInKilometers = totalDistanceInMeters / 1000.0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Option ${index + 1} - ${"%.2f".format(totalDistanceInKilometers)} km",
                            fontSize = 16.sp
                        )
                        RadioButton(
                            onClick = { postTripsViewModel.selectRoute(index) },
                            selected = index == selectedRouteIndex,
                            modifier = Modifier.testTag("routeRadioButton")
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                postTripsViewModel.selectedRoute =
                    postTripsViewModel.routes.value?.get(selectedRouteIndex)
                navController.navigate(AppScreens.DateSelectionScreen.route)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 24.dp)
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
