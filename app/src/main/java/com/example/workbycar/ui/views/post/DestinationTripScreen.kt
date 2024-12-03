package com.example.workbycar.ui.views.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.postTrips.PostTripsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationTripScreen(navController: NavController, postTripsViewModel: PostTripsViewModel) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "DestinationTripScreen") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Arrow back")
                }
            }
        )
    }) { paddingValues ->
        DestinationTripContent(navController, Modifier.padding(paddingValues), postTripsViewModel)
    }
}

@Composable
fun DestinationTripContent(navController: NavController, modifier: Modifier = Modifier, postTripsViewModel: PostTripsViewModel){
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Where are you going?")
        DestinationTextView(navController, postTripsViewModel)
    }
}

@Composable
fun DestinationTextView(navController: NavController, postTripsViewModel: PostTripsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        TextField(
            value = postTripsViewModel.destination,
            onValueChange = { newDestination ->
                postTripsViewModel.destination = newDestination
                postTripsViewModel.onPlaceChange(newDestination)
            },
            label = { Text("Write the full address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(postTripsViewModel.predictions) { (predictionText, placeId) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            postTripsViewModel.destination = predictionText
                            postTripsViewModel.getCoordinates(postTripsViewModel.destination, 1)
                            navController.navigate(AppScreens.DestinationInMapScreen.route)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = predictionText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Select location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}