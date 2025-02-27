package com.example.workbycar.ui.views.post

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.postTrips.PostTripsViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionScreen(navController: NavController, postTripsViewModel: PostTripsViewModel){
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "DateSelectionScreen") },
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
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "Please select the days you offer the trip")
                MultiSelectCalendar (postTripsViewModel)
                Button(
                    onClick = {
                        navController.navigate(AppScreens.DepartureTimeSelectionScreen.route)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(text = "Continue")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MultiSelectCalendar(postTripsViewModel: PostTripsViewModel) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val firstSelectedDate = remember { mutableStateOf<LocalDate?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "<",
                fontSize = 24.sp,
                modifier = Modifier.clickable {
                    currentMonth.value = currentMonth.value.minusMonths(1)
                }
            )
            Text(
                text = currentMonth.value.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        + " " + currentMonth.value.year,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = ">",
                fontSize = 24.sp,
                modifier = Modifier.clickable {
                    currentMonth.value = currentMonth.value.plusMonths(1)
                }
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(7) { index ->
                Text(
                    text = DayOfWeek.entries[index].getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        val daysInMonth = currentMonth.value.lengthOfMonth()
        val firstDayOfWeek = LocalDate.of(currentMonth.value.year, currentMonth.value.month, 1).dayOfWeek.value

        LazyColumn {
            items((daysInMonth + firstDayOfWeek - 1) / 7 + 1) { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (day in 1..7) {
                        val dayOfMonth = (week * 7 + day) - firstDayOfWeek + 1
                        if (dayOfMonth in 1..daysInMonth) {
                            val date = LocalDate.of(currentMonth.value.year, currentMonth.value.month, dayOfMonth)
                            val isSelected = postTripsViewModel.dates.contains(date)

                            val isWithinAllowedRange = firstSelectedDate.value?.let { selectedDate ->
                                val startOfWeek = selectedDate.minusDays(selectedDate.dayOfWeek.value.toLong() - 1)
                                val endOfWeek = startOfWeek.plusDays(6)
                                postTripsViewModel.startOfWeek = startOfWeek
                                postTripsViewModel.endOfWeek = endOfWeek
                                date in startOfWeek..endOfWeek
                            } ?: true

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (isSelected) Color.Blue else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable(
                                        enabled = isWithinAllowedRange,
                                        onClick = {
                                            val updatedDates = postTripsViewModel.dates.toMutableSet()
                                            if (isSelected) {
                                                updatedDates.remove(date)
                                                if (updatedDates.isEmpty()) {
                                                    firstSelectedDate.value = null
                                                }
                                            } else {
                                                updatedDates.add(date)
                                                if (firstSelectedDate.value == null) {
                                                    firstSelectedDate.value = date
                                                }
                                            }
                                            postTripsViewModel.dates = updatedDates
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayOfMonth.toString(),
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(40.dp))
                        }
                    }
                }
            }
        }
    }
}