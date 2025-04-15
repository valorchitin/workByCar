package com.example.workbycar.ui.views.messages

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.domain.model.Chat
import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.chats.ChatsViewModel
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel
import com.example.workbycar.ui.views.ButtonsMainScreen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    navController: NavController,
    searcherViewModel: SearcherViewModel,
    chatsViewModel: ChatsViewModel
) {
    val chats by chatsViewModel.chats.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        chatsViewModel.loadEveryChats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chats",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0277BD)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ButtonsMainScreen(navController = navController, searcherViewModel = searcherViewModel)
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(chats) { chat ->
                ChatElement(chat, chatsViewModel, navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatElement(chat: Chat, chatsViewModel: ChatsViewModel, navController: NavController) {
    var otherUser by remember { mutableStateOf<UserLogged?>(null) }

    LaunchedEffect(chat) {
        val otherUserId = chat.users.find { it != chatsViewModel.currentUserId }
        if (otherUserId != null) {
            chatsViewModel.getOtherUser(otherUserId) { user ->
                otherUser = user
            }
        } else {
            Log.e("ChatElement", "No other user was found in the chat")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                chatsViewModel.selectedUser = otherUser
                chatsViewModel.selectedChat = chat
                navController.navigate(AppScreens.ChatScreen.route)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFF0277BD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                otherUser?.let {
                    Text(
                        text = it.name.firstOrNull()?.uppercase() + " " + it.surname.firstOrNull()?.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = otherUser?.name + " " + otherUser?.surname,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chat.lastMessage.ifEmpty { "There are no messages yet" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = chatsViewModel.formatTimestampWhatsAppStyle(chat.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}