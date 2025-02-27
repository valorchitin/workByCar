package com.example.workbycar.ui.views.searcher

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekSelectorScreen(navController: NavController, searcherViewModel: SearcherViewModel) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Arrow back")
                }
            }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Week Selector")

            MultiSelectCalendar(searcherViewModel)

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    navController.navigate(AppScreens.MainScreen.route)
                },
                enabled = searcherViewModel.searcherStartOfWeek != null && searcherViewModel.searcherEndOfWeek != null,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MultiSelectCalendar(searcherViewModel: SearcherViewModel) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val selectedWeekStart = remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
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

                            val startOfWeek = date.minusDays(date.dayOfWeek.value.toLong() - 1)
                            val endOfWeek = startOfWeek.plusDays(6)

                            val isSelectedWeek = selectedWeekStart.value != null &&
                                    startOfWeek == selectedWeekStart.value

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (isSelectedWeek) Color.Blue else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable(
                                        onClick = {
                                            selectedWeekStart.value = startOfWeek
                                            searcherViewModel.searcherStartOfWeek = startOfWeek
                                            searcherViewModel.searcherEndOfWeek = endOfWeek
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayOfMonth.toString(),
                                    color = if (isSelectedWeek) Color.White else Color.Black
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