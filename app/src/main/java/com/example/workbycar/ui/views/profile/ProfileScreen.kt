package com.example.workbycar.ui.views.profile

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.profile.ProfileViewModel
import com.example.workbycar.ui.views.ButtonsMainScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel){
    LaunchedEffect(Unit) {
        profileViewModel.userInfo()
    }

    val user = profileViewModel.currentUser
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedBirthDate = user?.birthDate?.let {
        dateFormat.format(Date(it))
    } ?: "Fecha no válida"
    Scaffold ( topBar = {
        TopAppBar(
            title = {Text(text = "User information")}
        )
    }) { paddingValues ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aquí aplicamos paddingValues
                .padding(16.dp)
        ) {
            item {
                if (user != null) {
                    TextInfo("Name", user.name)
                    TextInfo("Surname", user.surname)
                    TextInfo("Birthdate", formattedBirthDate)
                    TextInfo("Phone", (user.prefix + user.phone))
                    TextInfo("Email", user.email)
                    Spacer(modifier = Modifier.height(32.dp))
                    HyperLinkText(navController)
                    Spacer(modifier = Modifier.height(44.dp))
                    LogOutButton(navController, profileViewModel)
                    Spacer(modifier = Modifier.height(16.dp))
                    DeleteAcountButton(navController, profileViewModel)
                }
            }
        }
        Column (
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ){
            ButtonsMainScreen(navController = navController)
        }
    }
}

@Composable
fun TextInfo(typeInfo: String, info: String){
    Text("$typeInfo: $info")
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun HyperLinkText(navController: NavController){
    Text(
        text = "Edit user info",
        modifier = Modifier.clickable{
            navController.navigate(AppScreens.EditUserInfoScreen.route)
        },
        color = Color.Blue,
        textDecoration = TextDecoration.Underline
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LogOutButton(navController: NavController, profileViewModel: ProfileViewModel){
    Button(onClick = {
        profileViewModel.logOut()
        navController.navigate(AppScreens.LogInScreen.route){
            popUpTo(0)
        }
    }) {
        Text("Log out")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeleteAcountButton(navController: NavController, profileViewModel: ProfileViewModel){
    val context = LocalContext.current
    Button(onClick = {
        profileViewModel.deleteAccount(navController, context)
    }) {
        Text("Delete account")
    }
}
