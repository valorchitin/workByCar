package com.example.workbycar.ui.views.post

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.postTrips.PostTripsViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DepartureTimeSelectionScreen(navController: NavController, postTripsViewModel: PostTripsViewModel){
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Arrow back")
                }
            }
        )
    }) { paddingValues ->
        Column (
            modifier = Modifier.padding(paddingValues)
        ){
            Text(text = "What time will you pick up your passengers?")
            TimeSelector(navController, postTripsViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSelector(navController: NavController, postTripsViewModel: PostTripsViewModel) {
    var showTimePicker by remember{ mutableStateOf(false) }

    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    if (!showTimePicker){
        Box (
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = { showTimePicker = true },
                shape = CutCornerShape(20),
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Text( text = postTripsViewModel.departureHour.format(timeFormatter))
            }
            Button(
                onClick = {
                    navController.navigate(AppScreens.PassengersNumberScreen.route)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text(text = "Continue")
            }
        }
    } else {
        Column {
            TimePicker(
                state = timePickerState,
            )
            Button(
                onClick = {
                    postTripsViewModel.departureHour = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }
            ) {
                Text("Confirm")
            }
            Button(
                onClick = {
                    showTimePicker = false
                }
            ) {
                Text("Dismiss")
            }
        }
    }
}