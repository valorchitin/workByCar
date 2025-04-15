package com.example.workbycar.ui.views.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.profile.ProfileViewModel
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel
import com.example.workbycar.ui.views.ButtonsMainScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel, searcherViewModel: SearcherViewModel) {
    LaunchedEffect(Unit) {
        profileViewModel.userInfo()
    }

    val user = profileViewModel.currentUser
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedBirthDate = user?.birthDate?.let {
        dateFormat.format(Date(it))
    } ?: "Fecha no válida"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "User Profile", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ButtonsMainScreen(navController = navController, searcherViewModel = searcherViewModel)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .padding(16.dp)
                )
            }

            if (user != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TextInfo(Icons.Default.Person, "Name", user.name)
                            TextInfo(Icons.Default.Person, "Surname", user.surname)
                            TextInfo(Icons.Default.Cake, "Birthdate", formattedBirthDate)
                            TextInfo(Icons.Default.Phone, "Phone", "${user.prefix} ${user.phone}")
                            TextInfo(Icons.Default.Email, "Email", user.email)
                        }
                    }
                }

                item { HyperLinkText(navController) }
                item { LogOutButton(navController, profileViewModel) }
                item { DeleteAccountButton(navController, profileViewModel) }
            }
        }
    }
}

@Composable
fun TextInfo(icon: ImageVector, typeInfo: String, info: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = typeInfo, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = typeInfo, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = info, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun HyperLinkText(navController: NavController){
    Text(
        text = "Edit user info",
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable{
            navController.navigate(AppScreens.EditUserInfoScreen.route)
        },
        style = MaterialTheme.typography.titleLarge,
        color = Color(0xFF0061A8),
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
        Text(
            text = "Log out",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeleteAccountButton(navController: NavController, profileViewModel: ProfileViewModel){
    val context = LocalContext.current
    Button(onClick = {
        profileViewModel.deleteAccount(navController, context)
    }) {
        Text(
            text = "Delete account",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
