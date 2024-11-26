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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppNavigation
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.postTrips.PostTripsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartTripScreen(navController: NavController, postTripsViewModel: PostTripsViewModel){
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
            StartTripContent(navController, Modifier.padding(paddingValues), postTripsViewModel)
    }
}

@Composable
fun StartTripContent(navController: NavController, modifier: Modifier = Modifier, postTripsViewModel: PostTripsViewModel){
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("¿Where do you leave from?")
        OriginTextView(navController, postTripsViewModel)
    }
}

@Composable
fun OriginTextView(navController: NavController, postTripsViewModel: PostTripsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        TextField(
            value = postTripsViewModel.origin,
            onValueChange = { newOrigin ->
                postTripsViewModel.origin = newOrigin
                postTripsViewModel.onOriginChange(newOrigin)
            },
            label = { Text("Write the full address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(postTripsViewModel.predictions) { (predictionText, placeId) ->
                Text(
                    text = predictionText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            postTripsViewModel.origin = predictionText
                            navController.navigate(AppScreens.OriginInMapScreen.route)
                        },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}