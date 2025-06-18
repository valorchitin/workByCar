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
import com.example.workbycar.ui.view_models.profile.ProfileViewModel
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripInformationScreen(navController: NavController, searcherViewModel: SearcherViewModel, chatsViewModel: ChatsViewModel, profileViewModel: ProfileViewModel, isMyTrip: Boolean) {
    LaunchedEffect (Unit) {
        searcherViewModel.getUserId()
    }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "") },
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
            ReservationButton(searcherViewModel, navController)
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
                ThirdSection(searcherViewModel, chatsViewModel, profileViewModel, isMyTrip, navController)
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

    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    val formattedDates = searcherViewModel.selectedTrip!!.dates
        .mapNotNull {
            try {
                LocalDate.parse(it, inputFormatter).format(outputFormatter)
            } catch (e: Exception) {
                null
            }
        }
        .sorted()

    for (day in formattedDates){
        Text(
            text = day,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0277BD),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ThirdSection(searcherViewModel: SearcherViewModel, chatsViewModel: ChatsViewModel, profileViewModel: ProfileViewModel, isMyTrip: Boolean, navController: NavController){
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    profileViewModel.selectedUser = searcherViewModel.driver
                    navController.navigate(AppScreens.UserInfoScreen.route)
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
        if (searcherViewModel.passengers.isNotEmpty()) {
            Text(
                text = "Passengers:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            searcherViewModel.passengers.forEach { passenger ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable {
                            profileViewModel.selectedUser = passenger
                            navController.navigate(AppScreens.UserInfoScreen.route)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${passenger.name} ${passenger.surname}",
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "View passenger",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            print(searcherViewModel.passengers)
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
        if (!isMyTrip){
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
                            chatsViewModel.openOrCreateChat(
                                searcherViewModel.userId,
                                searcherViewModel.selectedTrip!!.uid,
                                searcherViewModel.selectedTrip!!.tripId
                            ) { chat ->
                                chatsViewModel.selectedChat = chat
                                chatsViewModel.selectedUser = searcherViewModel.driver
                                chatsViewModel.currentUserId = searcherViewModel.userId
                                navController.navigate(AppScreens.ChatScreen.route)
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
}

@Composable
fun ReservationButton(searcherViewModel: SearcherViewModel, navController: NavController){
    if (searcherViewModel.selectedTrip!!.automatedReservation){
        Button(
            onClick = {
                searcherViewModel.bookASeat()
                navController.navigate(AppScreens.TripsScreen.route)
            },
            enabled = searcherViewModel.selectedTrip!!.passengers.size != searcherViewModel.selectedTrip!!.passengersNumber,
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
                Text(
                    text = "Book a seat",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        Button(
            onClick = {
                searcherViewModel.bookASeat()
            },
            enabled = searcherViewModel.selectedTrip!!.passengers.size != searcherViewModel.selectedTrip!!.passengersNumber,
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
                Text(
                    text = "Send request",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


