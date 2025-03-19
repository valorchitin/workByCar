package com.example.workbycar.ui.views.messages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.workbycar.domain.model.Chat
import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.chats.ChatsViewModel
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel
import com.example.workbycar.ui.views.ButtonsMainScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(navController: NavController, searcherViewModel: SearcherViewModel, chatsViewModel: ChatsViewModel){
    val chats by chatsViewModel.chats.observeAsState(emptyList())

    LaunchedEffect (Unit) {
        chatsViewModel.loadEveryChats()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chats",
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
                ButtonsMainScreen(navController = navController, searcherViewModel = searcherViewModel)
            }
        }) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues = paddingValues)) {
            items(chats) { chat ->
                ChatElement(chat, chatsViewModel, navController)
            }
        }
    }
}

@Composable
fun ChatElement(chat: Chat, chatsViewModel: ChatsViewModel, navController: NavController){
    var otherUser by remember { mutableStateOf<UserLogged?>(null) }

    LaunchedEffect(chat) {
        val otherUserId = chat.users.find { it != chatsViewModel.currentUserId }
        if (otherUserId != null) {
            chatsViewModel.getOtherUser(otherUserId) { user ->
                otherUser = user
            }
        } else {
            Log.e("ChatElement", "No se encontró otro usuario en el chat")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                chatsViewModel.selectedUser = otherUser
                chatsViewModel.selectedChat = chat
                navController.navigate(AppScreens.ChatScreen.route)
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Gray, CircleShape)
            ) {
                otherUser?.let {
                    Text(
                        text = it.name,
                        modifier = Modifier.align(Alignment.Center),
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
                    text = otherUser?.name ?: "...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = chat.lastMessage.ifEmpty { "No hay mensajes aún" },
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = chat.timestamp.toDate().toLocaleString().substring(11, 16),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}