package com.example.workbycar.di

import android.content.Context
import android.location.Geocoder
import com.example.workbycar.R
import com.example.workbycar.data.ApiKeyInterceptor
import com.example.workbycar.data.AuthFirebaseImpl
import com.example.workbycar.data.ApiKeyProvider
import com.example.workbycar.domain.repository.AuthRepository
import com.example.workbycar.domain.repository.DirectionsAPIService
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Singleton
    @Provides
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository{
        return AuthFirebaseImpl(firebaseAuth)
    }

    @Singleton
    @Provides
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        Places.initialize(context, context.getString(R.string.google_maps_api_key))
        return Places.createClient(context)
    }

    @Singleton
    @Provides
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context, Locale.getDefault())
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(apiKeyInterceptor: ApiKeyInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideDirectionsApiService(retrofit: Retrofit): DirectionsAPIService {
        return retrofit.create(DirectionsAPIService::class.java)
    }

    @Singleton
    @Provides
    fun provideApiKeyProvider(@ApplicationContext context: Context): ApiKeyProvider {
        return ApiKeyProvider(context)
    }
}