package com.example.workbycar.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.R
import com.example.workbycar.ui.navigation.AppScreens

@Composable
fun MainScreen(navController: NavController){
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        ButtonsMainScreen(navController = navController)
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

