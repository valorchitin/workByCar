package com.example.workbycar.data

import android.content.Context
import com.example.workbycar.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyProvider @Inject constructor(
    @ApplicationContext
    private val context: Context
){
    fun getApiKey(): String {
        return context.getString(R.string.google_maps_api_key)
    }

    fun getApiKeyDirections(): String {
        return context.getString(R.string.directions_api_key)
    }
}