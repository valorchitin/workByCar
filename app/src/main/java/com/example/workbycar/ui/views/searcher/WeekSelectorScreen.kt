package com.example.workbycar.ui.views.searcher

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
            MultiSelectCalendar(searcherViewModel)

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    navController.navigate(AppScreens.MainScreen.route)
                },
                enabled = searcherViewModel.searcherStartOfWeek != null && searcherViewModel.searcherEndOfWeek != null,
                modifier = Modifier.padding(bottom = 40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous month",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { currentMonth.value = currentMonth.value.minusMonths(1) }
            )
            Text(
                text = "${currentMonth.value.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.value.year}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next month",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { currentMonth.value = currentMonth.value.plusMonths(1) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DayOfWeek.entries.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        val daysInMonth = currentMonth.value.lengthOfMonth()
        val firstDayOfWeek = LocalDate.of(currentMonth.value.year, currentMonth.value.month, 1).dayOfWeek.value

        LazyColumn {
            val today = LocalDate.now()
            val currentWeekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)

            items((daysInMonth + firstDayOfWeek - 1) / 7 + 1) { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
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

                            val isDisabledWeek = startOfWeek.isBefore(currentWeekStart)

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelectedWeek -> Color(0xFF0277BD)
                                            else -> Color.Transparent
                                        },
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = when {
                                            isSelectedWeek -> Color(0xFF0277BD)
                                            isDisabledWeek -> Color.LightGray
                                            else -> Color.Gray
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable(
                                        enabled = !isDisabledWeek,
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
                                    fontWeight = FontWeight.Medium,
                                    color = when {
                                        isSelectedWeek -> Color.White
                                        isDisabledWeek -> Color.Gray
                                        else -> Color.Black
                                    }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(48.dp))
                        }
                    }
                }
            }
        }
    }
}
