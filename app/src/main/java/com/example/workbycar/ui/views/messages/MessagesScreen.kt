package com.example.workbycar.ui.views.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel
import com.example.workbycar.ui.views.ButtonsMainScreen

@Composable
fun MessagesScreen(navController: NavController, searcherViewModel: SearcherViewModel){
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text("Messages Screen")
        //Spacer(modifier = Modifier.weight(1f))
        ButtonsMainScreen(navController = navController, searcherViewModel = searcherViewModel)
    }
}