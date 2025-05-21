package com.example.workbycar.ui.view_models.chats

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workbycar.domain.model.Chat
import com.example.workbycar.domain.model.Leg
import com.example.workbycar.domain.model.Message
import com.example.workbycar.domain.model.Polyline
import com.example.workbycar.domain.model.Route
import com.example.workbycar.domain.model.TextValue
import com.example.workbycar.domain.model.Trip
import com.example.workbycar.domain.model.UserLogged
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.utils.CallBackHandle
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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

    fun openOrCreateChat(
        currentUserId: String,
        otherUserId: String,
        tripId: String,
        onChatOpened: (Chat) -> Unit
    ) {
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
                    val chat = existingChat.toChatObject(existingChat.id)
                    onChatOpened(chat)
                } else {
                    val newChatRef = db.collection("chats").document()
                    val newChatId = newChatRef.id

                    val newChat = hashMapOf(
                        "users" to listOf(currentUserId, otherUserId),
                        "lastMessage" to "",
                        "timestamp" to System.currentTimeMillis(),
                        "relatedTrip" to tripId
                    )

                    newChatRef.set(newChat)
                        .addOnSuccessListener {
                            val chat = newChatRef.get().result?.toChatObject(newChatId)
                            if (chat != null) {
                                updateUserChats(currentUserId, newChatId)
                                updateUserChats(otherUserId, newChatId)
                                onChatOpened(chat)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error creating chat: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching chats: ${e.message}")
            }
    }

    private fun DocumentSnapshot.toChatObject(chatId: String): Chat {
        val users = get("users") as List<String>
        val lastMessage = get("lastMessage") as? String
        val timestamp = get("timestamp") as? Timestamp
        val relatedTrip = get("relatedTrip") as String

        return Chat(
            id = chatId,
            users = users,
            lastMessage = lastMessage ?: "",
            timestamp = timestamp ?: Timestamp.now(),
            relatedTrip = relatedTrip
        )
    }

    private fun updateUserChats(userId: String, chatId: String) {
        val db = FirebaseFirestore.getInstance()
        if (userId.isNotBlank()) {
            db.collection("usuarios").document(userId)
                .update("chats", FieldValue.arrayUnion(chatId))
                .addOnFailureListener { e -> Log.e("Firestore", "Error updating user chat list: ${e.message}") }
        } else {
            Log.e("ChatsViewModel", "User ID is empty, cannot update chat list")
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
                                    relatedTrip = chat.getString("relatedTrip") ?: "",
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimestamp(timestamp: Timestamp): String {
        val messageDateTime = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val messageDate = messageDateTime.toLocalDate()
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        return when (messageDate) {
            today -> messageDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            yesterday -> "Yesterday"
            else -> messageDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
        }
    }

    fun loadRelatedTrip(tripId: String, onSuccess: (Trip) -> Unit, onError: (Exception) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("trips")
            .document(tripId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    try {
                        val originLatitude = document.getDouble("origincoordinates.latitude") ?: 0.0
                        val originLongitude = document.getDouble("origincoordinates.longitude") ?: 0.0
                        val destinationLatitude = document.getDouble("destinationcoordinates.latitude") ?: 0.0
                        val destinationLongitude = document.getDouble("destinationcoordinates.longitude") ?: 0.0

                        val routeMap = document["route"] as? Map<String, Any>
                        val route = routeMap?.let {
                            val overviewPolyline = it["overview_polyline"] as? Map<String, Any>
                            val points = overviewPolyline?.get("points") as? String ?: ""

                            val legsList = it["legs"] as? List<Map<String, Any>> ?: emptyList()
                            val legs = legsList.map { leg ->
                                val distanceMap = leg["distance"] as? Map<String, Any>
                                val durationMap = leg["duration"] as? Map<String, Any>

                                Leg(
                                    distance = TextValue(
                                        text = distanceMap?.get("text") as? String ?: "",
                                        value = (distanceMap?.get("value") as? Number)?.toInt() ?: 0
                                    ),
                                    duration = TextValue(
                                        text = durationMap?.get("text") as? String ?: "",
                                        value = (durationMap?.get("value") as? Number)?.toInt() ?: 0
                                    )
                                )
                            }

                            Route(
                                overview_polyline = Polyline(points = points),
                                legs = legs
                            )
                        }

                        val trip = Trip(
                            tripId = document.id,
                            uid = document.getString("uid") ?: "",
                            description = document.getString("description") ?: "",
                            departureHour = document.getString("departureHour") ?: "",
                            origin = document.getString("origin") ?: "",
                            destination = document.getString("destination") ?: "",
                            origincoordinates = LatLng(originLatitude, originLongitude),
                            destinationcoordinates = LatLng(destinationLatitude, destinationLongitude),
                            passengersNumber = document.getLong("passengersNumber")?.toInt() ?: 0,
                            price = document.getLong("price")?.toInt() ?: 0,
                            route = route,
                            automatedReservation = document.getBoolean("automatedReservation") ?: false,
                            dates = document["dates"] as? List<String> ?: emptyList(),
                            startOfWeek = document.getString("startOfWeek") ?: "",
                            endOfWeek = document.getString("endOfWeek") ?: "",
                            passengers = document["passengers"] as? List<String> ?: emptyList()
                        )

                        onSuccess(trip)
                    } catch (e: Exception) {
                        Log.e("TripDeserialization", "Error: ${e.message}")
                        onError(e)
                    }
                } else {
                    onError(Exception("Trip not found"))
                }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

}