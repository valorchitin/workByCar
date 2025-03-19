package com.example.workbycar.ui.view_models.chats

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workbycar.domain.model.Chat
import com.example.workbycar.domain.model.Message
import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.utils.CallBackHandle
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    var currentUserId by mutableStateOf("")
    private val _filteredChats = MutableLiveData<List<Chat>>()
    val chats: LiveData<List<Chat>> = _filteredChats

    var selectedUser by mutableStateOf<UserLogged?>(null)
    var selectedChat by mutableStateOf<Chat?>(null)

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    fun openOrCreateChat(currentUserId: String, otherUserId: String, onChatOpened: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("chats")
            .whereArrayContains("users", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                val existingChat = documents.documents.find { doc ->
                    val users = doc.get("users") as List<String>
                    users.contains(otherUserId)
                }

                if (existingChat != null) {
                    onChatOpened(existingChat.id)
                } else {
                    val newChatRef = db.collection("chats").document()
                    val newChatId = newChatRef.id

                    val newChat = hashMapOf(
                        "users" to listOf(currentUserId, otherUserId),
                        "lastMessage" to "",
                        "timestamp" to System.currentTimeMillis()
                    )

                    newChatRef.set(newChat).addOnSuccessListener {
                        if (currentUserId.isNotBlank() && otherUserId.isNotBlank()) {
                            db.collection("usuarios").document(currentUserId)
                                .update("chats", com.google.firebase.firestore.FieldValue.arrayUnion(newChatId))
                                .addOnFailureListener { e -> Log.e("Firestore", "Error updating user chat list", e) }

                            db.collection("usuarios").document(otherUserId)
                                .update("chats", com.google.firebase.firestore.FieldValue.arrayUnion(newChatId))
                                .addOnFailureListener { e -> Log.e("Firestore", "Error updating user chat list", e) }
                        } else {
                            Log.e("ChatsViewModel", "User ID is empty, cannot create chat")
                        }
                        onChatOpened(newChatId)
                    }
                }
            }
    }

    fun loadEveryChats() {
        viewModelScope.launch {
            authRepository.getCurrentUser(CallBackHandle(
                onSuccess = { user ->
                    currentUserId = user.uid

                    FirebaseFirestore.getInstance()
                    .collection("chats")
                        .whereArrayContains("users", currentUserId)
                        .get()
                        .addOnSuccessListener { response ->
                            val userChats = response.documents.mapNotNull { chat ->
                                val timestamp = when (val timestampValue = chat["timestamp"]) {
                                    is Timestamp -> timestampValue
                                    is Long -> Timestamp(timestampValue / 1000, ((timestampValue % 1000) * 1000000).toInt())
                                    else -> Timestamp.now()
                                }

                                val chatObject = Chat(
                                    id = chat.id,
                                    users = chat["users"] as? List<String> ?: emptyList(),
                                    lastMessage = chat.getString("lastMessage") ?: "",
                                    timestamp = timestamp,
                                )
                                chatObject
                            }
                            _filteredChats.postValue(userChats)
                        }
                },
                onError = {
                    Log.e("ProfileViewModel", "Error al acceder a la informacion del usuario: $it")
                }
            ))
        }
    }

    fun getOtherUser(otherUserId: String, onResult: (UserLogged?) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(otherUserId)
            .get()
            .addOnSuccessListener { document ->
                val otherUser = document.toObject(UserLogged::class.java)
                onResult(otherUser)
            }
            .addOnFailureListener {
                Log.e("ChatsViewModel", "Error obteniendo usuario: ${it.message}")
                onResult(null)
            }
    }

    fun sendMessage(chatId: String, messageText: String) {
        if (messageText.isBlank()) return

        val db = FirebaseFirestore.getInstance()
        val messagesRef = db.collection("chats").document(chatId).collection("messages")
        val newMessage = Message(senderId = currentUserId, text = messageText, timestamp = Timestamp.now())

        messagesRef.add(newMessage)
            .addOnSuccessListener {
                db.collection("chats").document(chatId)
                    .update(
                        mapOf(
                            "lastMessage" to messageText,
                            "timestamp" to System.currentTimeMillis()
                        )
                    )
            }
            .addOnFailureListener { e ->
                Log.e("ChatsViewModel", "Error sending message", e)
            }
    }

    fun listenForMessages(chatId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ChatsViewModel", "Error listening for messages", e)
                    return@addSnapshotListener
                }

                val messagesList = snapshots?.documents?.mapNotNull { it.toObject(Message::class.java) } ?: emptyList()
                _messages.postValue(messagesList)
            }
    }
}