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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel){
    val user = profileViewModel.currentUser
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedBirthDate = user.birthDate?.let {
        dateFormat.format(Date(it))
    } ?: "Fecha no válida"
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text("Name: ${user.name}")
        Text("Surname: ${user.surname}")
        Text("Birthdate: $formattedBirthDate")
        Text("Phone: ${user.phone}")
        Text("Profile Screen")
        ButtonsMainScreen(navController = navController)
    }
}
