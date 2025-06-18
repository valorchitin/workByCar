package com.example.workbycar.utils

import androidx.core.app.NotificationCompat.MessagingStyle.Message
import java.lang.Error

data class CallBackHandle<T>(
    val onSuccess: (T) -> Unit,
    val onError: (ErrorData?) -> Unit
)

data class ErrorData(
    val code: String = "UNKNOWN_ERROR",
    val message: String
) {
    constructor(exception: Exception) : this(
        code = exception::class.simpleName ?: "UNKNOWN_EXCEPTION",
        message = exception.localizedMessage ?: "Ha ocurrido un error desconocido"
    )
}
