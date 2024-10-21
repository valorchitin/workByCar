package com.example.workbycar.ui.views.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.workbycar.ui.view_models.profile.ProfileViewModel
import com.example.workbycar.ui.views.ButtonsMainScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel){
    val user = profileViewModel.currentUser
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text("Name: ${user.name}")
        Text("Surname: ${user.surname}")
        Text("Profile Screen")
        ButtonsMainScreen(navController = navController)
    }
}
