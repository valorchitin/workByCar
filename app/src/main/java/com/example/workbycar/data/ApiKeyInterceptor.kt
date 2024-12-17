package com.example.workbycar.data

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiKeyInterceptor @Inject constructor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val packageName = "com.example.workbycar"
        val sha1 = "46:80:27:E1:7A:CF:7C:FA:20:F6:5A:62:F8:56:B4:0F:B2:E1:CF:76:D2:E8:68:F0:C1:DF:BF:79:2A:6F:97:71"

        val newRequest = chain.request().newBuilder()
            .addHeader("X-Android-Package", packageName)
            .addHeader("X-Android-Cert", sha1)
            .build()

        return chain.proceed(newRequest)
    }
}