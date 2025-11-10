package com.example.workbycar.ui.views.searcher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
        OutlinedTextField(
            value = searcherViewModel.searcherPlace,
            onValueChange = { newPlace ->
                searcherViewModel.searcherPlace = newPlace
                searcherViewModel.onPlaceChange(newPlace)
            },
            label = { Text("Write the full address") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Icon", tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searcherViewModel.searcherPlace.isNotEmpty()) {
                    IconButton(onClick = { searcherViewModel.searcherPlace = "" }) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear", tint = Color.Gray)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .testTag("placeTextField"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = searcherViewModel.searcherPredictions.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 8.dp)
                        .animateContentSize()
                ) {
                    items(searcherViewModel.searcherPredictions) { (predictionText, _) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .clickable {
                                    if (searcherViewModel.isOrigin) {
                                        searcherViewModel.searcherOrigin = predictionText
                                        searcherViewModel.getCoordinates(predictionText, 0)
                                    } else {
                                        searcherViewModel.searcherDestination = predictionText
                                        searcherViewModel.getCoordinates(predictionText, 1)
                                    }
                                    searcherViewModel.searcherPredictions = emptyList()
                                    navController.navigate(AppScreens.MainScreen.route)
                                }
                                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Place,
                                contentDescription = "Location Icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
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
    }
}
