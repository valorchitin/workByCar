package com.example.workbycar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.workbycar.ui.view_models.LoginViewModel
import com.example.workbycar.ui.view_models.SignUpViewModel
import com.example.workbycar.ui.view_models.profile.ProfileViewModel
import com.example.workbycar.ui.views.LogInScreen
import com.example.workbycar.ui.views.MainScreen
import com.example.workbycar.ui.views.SignUpScreen
import com.example.workbycar.ui.views.SplashScreen
import com.example.workbycar.ui.views.messages.MessagesScreen
import com.example.workbycar.ui.views.post.StartTripScreen
import com.example.workbycar.ui.views.profile.ProfileScreen
import com.example.workbycar.ui.views.trips.TripsScreen

@Composable
fun AppNavigation(loginViewModel: LoginViewModel, signUpViewModel: SignUpViewModel, profileViewModel: ProfileViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.SplashScreen.route
    ){
        composable(AppScreens.SplashScreen.route){
            SplashScreen(navController)
        }
        composable(AppScreens.LogInScreen.route){
            LogInScreen(navController, loginViewModel)
        }
        composable(AppScreens.SignUpScreen.route){
            SignUpScreen(navController, signUpViewModel)
        }
        composable(AppScreens.MainScreen.route){
            MainScreen(navController)
        }
        composable(AppScreens.TripsScreen.route) {
            TripsScreen(navController)
        }
        composable(AppScreens.ProfileScreen.route) {
            ProfileScreen(navController, profileViewModel)
        }
        composable(AppScreens.StartTripScreen.route) {
            StartTripScreen(navController)
        }
        composable(AppScreens.MessagesScreen.route) {
            MessagesScreen(navController)
        }
    }
}