package com.example.workbycar.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.R
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, searcherViewModel: SearcherViewModel){
    Scaffold(
        topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Trips",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        )
    },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ButtonsMainScreen(navController = navController)
            }
        }) { paddingValues ->
        Searcher(Modifier.padding(paddingValues), searcherViewModel, navController)
    }
}

@Composable
fun ButtonsMainScreen(navController: NavController){
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        ) {
        ButtonMain(image = R.drawable.busqueda,
            imageDescription = "Buscar",
            modifier = Modifier.weight(1f).fillMaxHeight(),
            route = AppScreens.MainScreen.route,
            navController = navController)
        ButtonMain(image = R.drawable.agregar,
            imageDescription = "Publicar",
            modifier = Modifier.weight(1f).fillMaxHeight(),
            route = AppScreens.StartTripScreen.route,
            navController = navController)
        ButtonMain(image = R.drawable.viaje_en_coche,
            imageDescription = "Tus viajes",
            modifier = Modifier.weight(1f).fillMaxHeight(),
            route = AppScreens.TripsScreen.route,
            navController = navController)
        ButtonMain(image = R.drawable.puntos_de_comentario,
            imageDescription = "Mensajes",
            modifier = Modifier.weight(1f).fillMaxHeight(),
            route = AppScreens.MessagesScreen.route,
            navController = navController)
        ButtonMain(image = R.drawable.usuario,
            imageDescription = "Perfil",
            modifier = Modifier.weight(1f).fillMaxHeight(),
            route = AppScreens.ProfileScreen.route,
            navController = navController)
    }
}

@Composable
fun ButtonMain(image: Int, imageDescription: String, modifier: Modifier, route: String, navController: NavController){
    FilledTonalButton(onClick = {
            navController.navigate(route = route)
        },
        shape = CutCornerShape(0),
        modifier = modifier) {
        Icon(
            painter = painterResource(id = image),
            contentDescription = imageDescription
        )
    }
}

@Composable
fun Searcher(modifier: Modifier, searcherViewModel: SearcherViewModel, navController: NavController){
    var selectedDate by remember { mutableStateOf("Selecciona una semana") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Buscar Viaje",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = searcherViewModel.searcherOrigin,
                    onValueChange = {},
                    label = { Text("Origen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable (
                            onClick = {
                                searcherViewModel.isOrigin = true
                                navController.navigate(AppScreens.PlaceSelectorScreen.route)
                            }
                        ),
                    enabled = false,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                OutlinedTextField(
                    value = searcherViewModel.searcherDestination,
                    onValueChange = {},
                    label = { Text("Destino") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable (
                            onClick = {
                                searcherViewModel.isOrigin = false
                                navController.navigate(AppScreens.PlaceSelectorScreen.route)
                            }
                        ),
                    enabled = false,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    label = { Text("Semana") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(AppScreens.WeekSelectorScreen.route)
                        },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Calendario",
                            tint = Color.Gray
                        )
                    },
                    enabled = false,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Button(
                    onClick = {
                        // Logic to handle the search action
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Buscar")
                }
            }
        }
    }
}

