package com.example.workbycar.ui.views.trips

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.domain.model.Trip
import com.example.workbycar.ui.view_models.userTrips.UserTripsViewModel
import com.example.workbycar.ui.views.ButtonsMainScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(navController: NavController, userTripsViewModel: UserTripsViewModel) {
    val trips by userTripsViewModel.trips.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        userTripsViewModel.LoadTrips()
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Trips",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        )
    },
    bottomBar = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ButtonsMainScreen(navController = navController)
        }
    }) { paddingValues ->
        if (trips.isEmpty()) {
            Text(
                text = "No trips yet",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(trips) { trip ->
                    TripCard(trip)
                }
            }
        }
    }
}

@Composable
fun TripCard(trip: Trip){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
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
        }
    }
}