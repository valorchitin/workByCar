package com.example.workbycar.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.R
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.profile.ProfileViewModel
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavController, searcherViewModel: SearcherViewModel, profileViewModel: ProfileViewModel) {
    LaunchedEffect(Unit) {
        profileViewModel.userInfo()
    }

    Scaffold(
        bottomBar = {
            ButtonsMainScreen(navController = navController, searcherViewModel = searcherViewModel)
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondo_buscador),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                val fadeInAnim = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    fadeInAnim.animateTo(1f, animationSpec = tween(durationMillis = 1000))
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = profileViewModel.currentUser?.let {
                        "Welcome\n${it.name} ${it.surname}"
                    } ?: "Welcome",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(fadeInAnim.value)
                        .shadow(8.dp, shape = RoundedCornerShape(12.dp))
                        .background(Color(0xAA0277BD), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                )

                Searcher(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    searcherViewModel = searcherViewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun ButtonsMainScreen(navController: NavController, searcherViewModel: SearcherViewModel){
    Row (modifier = Modifier
        .navigationBarsPadding()
        .fillMaxWidth()
        .height(60.dp)
        ) {
        ButtonMain(image = R.drawable.busqueda,
            imageDescription = "Search",
            modifier = Modifier.weight(1f).fillMaxHeight(),
            route = AppScreens.MainScreen.route,
            navController = navController,
            searcherViewModel = searcherViewModel)
        ButtonMain(image = R.drawable.agregar,
            imageDescription = "Post",
            modifier = Modifier.weight(1f).fillMaxHeight(),
            route = AppScreens.StartTripScreen.route,
            navController = navController,
            searcherViewModel = searcherViewModel)
        ButtonMain(image = R.drawable.viaje_en_coche,
            imageDescription = "My trips",
            modifier = Modifier.weight(1f).fillMaxHeight(),
            route = AppScreens.TripsScreen.route,
            navController = navController,
            searcherViewModel = searcherViewModel)
        ButtonMain(image = R.drawable.puntos_de_comentario,
            imageDescription = "Messages",
            modifier = Modifier.weight(1f).fillMaxHeight(),
            route = AppScreens.MessagesScreen.route,
            navController = navController,
            searcherViewModel = searcherViewModel)
        ButtonMain(image = R.drawable.usuario,
            imageDescription = "Profile",
            modifier = Modifier.weight(1f).fillMaxHeight(),
            route = AppScreens.ProfileScreen.route,
            navController = navController,
            searcherViewModel = searcherViewModel)
    }
}

@Composable
fun ButtonMain(image: Int, imageDescription: String, modifier: Modifier, route: String, navController: NavController, searcherViewModel: SearcherViewModel){
    FilledTonalButton(onClick = {
            searcherViewModel.resetSearcher()
            navController.navigate(route = route)
        },
        shape = CutCornerShape(0),
        modifier = modifier.testTag(imageDescription)) {
        Icon(
            painter = painterResource(id = image),
            contentDescription = imageDescription
        )
    }
}

@Composable
fun Searcher(modifier: Modifier, searcherViewModel: SearcherViewModel, navController: NavController) {
    val fadeInAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fadeInAnim.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .alpha(fadeInAnim.value)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Search Trip",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                SearchField(
                    label = "Origin",
                    value = searcherViewModel.searcherOrigin,
                    icon = Icons.Filled.Place,
                    onClick = {
                        searcherViewModel.isOrigin = true
                        navController.navigate(AppScreens.PlaceSelectorScreen.route)
                    }
                )

                SearchField(
                    label = "Destination",
                    value = searcherViewModel.searcherDestination,
                    icon = Icons.Filled.Place,
                    onClick = {
                        searcherViewModel.isOrigin = false
                        navController.navigate(AppScreens.PlaceSelectorScreen.route)
                    }
                )

                SearchField(
                    label = "Week",
                    value = if (searcherViewModel.searcherStartOfWeek != null && searcherViewModel.searcherEndOfWeek != null) {
                        "From ${searcherViewModel.searcherStartOfWeek} to ${searcherViewModel.searcherEndOfWeek}"
                    } else {
                        "Select a week"
                    },
                    icon = Icons.Default.CalendarToday,
                    onClick = {
                        navController.navigate(AppScreens.WeekSelectorScreen.route)
                    }
                )

                Button(
                    onClick = {
                        searcherViewModel.search()
                        navController.navigate(AppScreens.FoundTripsScreen.route)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SearchField(label: String, value: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedTextField(
        value = value.ifEmpty { "Tap to select" },
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(label),
        enabled = false,
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = label, tint = Color.Gray)
        },
        shape = RoundedCornerShape(10.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        maxLines = 2,
        colors = OutlinedTextFieldDefaults.colors().copy(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledIndicatorColor = MaterialTheme.colorScheme.outline,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}


