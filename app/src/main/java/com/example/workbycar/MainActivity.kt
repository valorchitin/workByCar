package com.example.workbycar

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.workbycar.ui.navigation.AppNavigation
import com.example.workbycar.ui.theme.WorkByCarTheme
import com.example.workbycar.ui.view_models.LoginViewModel
import com.example.workbycar.ui.view_models.SignUpViewModel
import com.example.workbycar.ui.view_models.postTrips.PostTripsViewModel
import com.example.workbycar.ui.view_models.profile.ProfileViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var placesClient: PlacesClient

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        val loginViewModel: LoginViewModel by viewModels()
        val signUpViewModel: SignUpViewModel by viewModels()
        val profileViewModel: ProfileViewModel by viewModels()
        val postTripsViewModel: PostTripsViewModel by viewModels()
        super.onCreate(savedInstanceState)
        Places.initialize(this, getString(R.string.google_maps_api_key))
        placesClient = Places.createClient(this)
        enableEdgeToEdge()
        setContent {
            WorkByCarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(loginViewModel, signUpViewModel, profileViewModel, postTripsViewModel)
                }
            }
        }
    }
}
