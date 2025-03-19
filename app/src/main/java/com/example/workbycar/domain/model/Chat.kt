package com.example.workbycar.domain.model

import com.google.firebase.Timestamp

data class Chat(
    val id: String = "",
    val users: List<String> = emptyList(),
    val lastMessage: String = "",
    val timestamp: Timestamp = Timestamp.now(),
)