package com.example.workbycar.ui.views.searcher

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSelectorScreen(navController: NavController, searcherViewModel: SearcherViewModel) {
    LaunchedEffect (Unit) {
        searcherViewModel.searcherPlace = ""
    }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Arrow back")
                }
            }
        )
    }) { paddingValues ->
        PlaceTextView(Modifier.padding(paddingValues), navController, searcherViewModel)
    }
}

@Composable
fun PlaceTextView(modifier: Modifier, navController: NavController, searcherViewModel: SearcherViewModel) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        TextField(
            value = searcherViewModel.searcherPlace,
            onValueChange = { newPlace ->
                searcherViewModel.searcherPlace = newPlace
                searcherViewModel.onPlaceChange(newPlace)
            },
            label = { Text("Write the full address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(searcherViewModel.searcherPredictions) { (predictionText, _) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            if (searcherViewModel.isOrigin){
                                searcherViewModel.searcherOrigin = predictionText
                                searcherViewModel.getCoordinates(searcherViewModel.searcherOrigin, 0)
                            } else {
                                searcherViewModel.searcherDestination = predictionText
                                searcherViewModel.getCoordinates(searcherViewModel.searcherDestination, 1)
                            }
                            navController.navigate(AppScreens.MainScreen.route)
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