package com.example.workbycar.ui.views.searcher

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.chats.ChatsViewModel
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripInformationScreen(navController: NavController, searcherViewModel: SearcherViewModel, chatsViewModel: ChatsViewModel) {
    LaunchedEffect (Unit) {
        searcherViewModel.getUserId()
    }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Found Trips") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Arrow back")
                }
            }
        )
    },
    bottomBar = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ReservationButtom(searcherViewModel)
        }
    }) { paddingValues ->
        LazyColumn (
            modifier = Modifier.padding(paddingValues)
        ){
            item {
                FirstSection(navController, searcherViewModel)
            }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 10.dp,
                    color = Color(0xFFE0E0E0)
                )
            }
            item {
                SecondSection(searcherViewModel)
            }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 10.dp,
                    color = Color(0xFFE0E0E0)
                )
            }
            item {
                ThirdSection(searcherViewModel, chatsViewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FirstSection(navController: NavController, searcherViewModel: SearcherViewModel){
    val trip = searcherViewModel.selectedTrip!!

    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    val departureTime = LocalTime.parse(trip.departureHour, formatter)

    val durationInSeconds = trip.route?.legs?.get(0)?.duration?.value
    val arrivalTime = durationInSeconds?.let { departureTime.plusSeconds(it.toLong()) }

    val formattedArrivalTime = arrivalTime?.format(formatter)

    for (day in searcherViewModel.selectedTrip!!.dates){
        Text(
            text = day,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate("${AppScreens.MapScreen.route}/true")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Departure: ")
                }
                append("${searcherViewModel.selectedTrip!!.origin} - ${searcherViewModel.selectedTrip!!.departureHour}")
            },
            fontSize = 20.sp,
            modifier = Modifier.weight(1f, false),
            textAlign = TextAlign.Start
        )
        Icon(
            imageVector = Icons.Filled.Map,
            contentDescription = "Select location",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate("${AppScreens.MapScreen.route}/false")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Destination: ")
                }
                append("${searcherViewModel.selectedTrip!!.destination} - $formattedArrivalTime")
            },
            fontSize = 20.sp,
            modifier = Modifier.weight(1f, false),
            textAlign = TextAlign.Start
        )
        Icon(
            imageVector = Icons.Filled.Map,
            contentDescription = "Select location",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun SecondSection(searcherViewModel: SearcherViewModel){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "1 passenger, ${searcherViewModel.selectedTrip!!.dates.size} " +
                    if (searcherViewModel.selectedTrip!!.dates.size > 1) "days" else "day",
            fontSize = 20.sp
        )
        val formattedPrice = String.format("%.2f€", searcherViewModel.selectedTrip!!.price.toDouble())
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(formattedPrice)
                }
            },
            fontSize = 20.sp
        )
    }
}

@Composable
fun ThirdSection(searcherViewModel: SearcherViewModel, chatsViewModel: ChatsViewModel){
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    println(searcherViewModel.driver)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Driver: ")
                    }
                    append("${searcherViewModel.driver?.name} ${searcherViewModel.driver?.surname}")},
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Select location",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(16.dp),
            thickness = 5.dp,
            color = Color(0xFFE0E0E0)
        )
        Text(
            text = "Trip description:",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Start
        )
        Text(
            text = if (searcherViewModel.selectedTrip!!.description != ""){
                    searcherViewModel.selectedTrip!!.description
                } else {
                    "No description available"
            },
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(Color(0xFFBBDEFB), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = if (searcherViewModel.selectedTrip!!.automatedReservation) {
                    "The reservation will be accepted automatically"
                } else {
                    "The reservation must be accepted by the driver"
                },
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF0D47A1)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = {

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = BorderStroke(2.dp, Color(0xFF0D47A1)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable {
                            chatsViewModel.openOrCreateChat(searcherViewModel.userId, searcherViewModel.selectedTrip!!.uid){ chatId ->
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubble,
                        contentDescription = "Chat Icon",
                        tint = Color(0xFF0D47A1)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Contact ${searcherViewModel.driver?.name}",
                        color = Color(0xFF0D47A1),
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun ReservationButtom(searcherViewModel: SearcherViewModel){
    if (searcherViewModel.selectedTrip!!.automatedReservation){
        Button(
            onClick = {
                searcherViewModel.bookASeat()
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Book a seat")
            }
        }
    } else {
        Button(
            onClick = {
                searcherViewModel.bookASeat()
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send request")
            }
        }
    }
}


