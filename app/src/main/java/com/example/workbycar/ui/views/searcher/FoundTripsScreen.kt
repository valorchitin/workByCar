package com.example.workbycar.ui.views.searcher

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.domain.model.Trip
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundTripsScreen(navController: NavController, searcherViewModel: SearcherViewModel) {
    val trips by searcherViewModel.trips.observeAsState(emptyList())
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Found Trips") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Arrow back")
                }
            }
        )
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Text("Found Trips Screen", style = MaterialTheme.typography.headlineMedium)

            if (trips.isEmpty()) {
                Text("No trips found", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn {
                    items(trips) { trip ->
                        TripCard(trip, navController, searcherViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun TripCard(trip: Trip, navController: NavController, searcherViewModel: SearcherViewModel){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .clickable {
                searcherViewModel.selectedTrip = trip
                searcherViewModel.getDriver()
                navController.navigate(AppScreens.TripInformationScreen.route)
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Text(
                text = "${trip.origin} -> ${trip.destination}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Days: ${trip.dates.joinToString(", ")}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Departure: ${trip.departureHour}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = "Price: ${trip.price}€", style = MaterialTheme.typography.bodyMedium)
        }
    }
}