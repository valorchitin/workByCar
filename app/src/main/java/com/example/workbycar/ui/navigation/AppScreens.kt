package com.example.workbycar.ui.navigation

sealed class AppScreens(val route: String) {
    object SplashScreen: AppScreens("splash_screen")
    object LogInScreen: AppScreens("log_in_screen")
    object SignUpScreen: AppScreens("sign_up_screen")
    object AddPhoneScreen: AppScreens("add_phone_screen")
    object MainScreen: AppScreens("main_screen")
    object TripsScreen: AppScreens("trips_screen")
    object ProfileScreen: AppScreens("profile_screen")
    object EditUserInfoScreen: AppScreens("edit_user_info_screen")
    object StartTripScreen: AppScreens("start_trip_screen")
    object MessagesScreen: AppScreens("messages_screen")
    object OriginInMapScreen: AppScreens("origin_in_map_screen")
}