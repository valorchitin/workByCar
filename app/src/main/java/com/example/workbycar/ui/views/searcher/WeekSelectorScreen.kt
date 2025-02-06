package com.example.workbycar.ui.views.searcher

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekSelectorScreen(navController: NavController, searcherViewModel: SearcherViewModel) {
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
        Text("Week Selector", modifier = Modifier.padding(paddingValues))
    }
}