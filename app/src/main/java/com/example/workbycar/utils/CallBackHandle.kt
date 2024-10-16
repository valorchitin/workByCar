package com.example.workbycar.utils

import androidx.core.app.NotificationCompat.MessagingStyle.Message
import java.lang.Error

data class CallBackHandle<T>(
    val onSuccess: (T) -> Unit,
    val onError: (ErrorData?) -> Unit
)

data class ErrorData(val code: String, val message: String)