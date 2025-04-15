package com.example.workbycar.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.workbycar.ui.view_models.LoginViewModel
import com.example.workbycar.ui.view_models.SignUpViewModel
import com.example.workbycar.ui.view_models.chats.ChatsViewModel
import com.example.workbycar.ui.view_models.postTrips.PostTripsViewModel
import com.example.workbycar.ui.view_models.profile.ProfileViewModel
import com.example.workbycar.ui.view_models.searcher.SearcherViewModel
import com.example.workbycar.ui.view_models.userTrips.UserTripsViewModel
import com.example.workbycar.ui.views.LogInScreen
import com.example.workbycar.ui.views.MainScreen
import com.example.workbycar.ui.views.sign_up.SignUpScreen
import com.example.workbycar.ui.views.SplashScreen
import com.example.workbycar.ui.views.messages.ChatScreen
import com.example.workbycar.ui.views.messages.MessagesScreen
import com.example.workbycar.ui.views.post.DateSelectionScreen
import com.example.workbycar.ui.views.post.DepartureTimeSelectionScreen
import com.example.workbycar.ui.views.post.DestinationInMapScreen
import com.example.workbycar.ui.views.post.DestinationTripScreen
import com.example.workbycar.ui.views.post.OriginInMapScreen
import com.example.workbycar.ui.views.post.PassengersNumberScreen
import com.example.workbycar.ui.views.post.PriceRecommendationScreen
import com.example.workbycar.ui.views.post.PriceSelectorScreen
import com.example.workbycar.ui.views.post.PublicationConfirmationScreen
import com.example.workbycar.ui.views.post.ReservationTypeScreen
import com.example.workbycar.ui.views.post.RouteSelectionScreen
import com.example.workbycar.ui.views.post.StartTripScreen
import com.example.workbycar.ui.views.post.TripPostingScreen
import com.example.workbycar.ui.views.profile.EditUserInfoScreen
import com.example.workbycar.ui.views.profile.ProfileScreen
import com.example.workbycar.ui.views.searcher.FoundTripsScreen
import com.example.workbycar.ui.views.searcher.MapScreen
import com.example.workbycar.ui.views.searcher.PlaceSelectorScreen
import com.example.workbycar.ui.views.searcher.TripInformationScreen
import com.example.workbycar.ui.views.searcher.WeekSelectorScreen
import com.example.workbycar.ui.views.sign_up.AddPhoneScreen
import com.example.workbycar.ui.views.trips.TripsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(loginViewModel: LoginViewModel,
                  signUpViewModel: SignUpViewModel,
                  profileViewModel: ProfileViewModel,
                  postTripsViewModel: PostTripsViewModel,
                  userTripsViewModel: UserTripsViewModel,
                  searcherViewModel: SearcherViewModel,
                  chatsViewModel: ChatsViewModel) {
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
        composable(AppScreens.AddPhoneScreen.route){
            AddPhoneScreen(navController, signUpViewModel)
        }
        composable(AppScreens.MainScreen.route){
            MainScreen(navController, searcherViewModel, profileViewModel)
        }
        composable(AppScreens.PlaceSelectorScreen.route){
            PlaceSelectorScreen(navController, searcherViewModel)
        }
        composable(AppScreens.WeekSelectorScreen.route){
            WeekSelectorScreen(navController, searcherViewModel)
        }
        composable(AppScreens.FoundTripsScreen.route){
            FoundTripsScreen(navController, searcherViewModel)
        }
        composable(
            route = "${AppScreens.TripInformationScreen.route}/{isMyTrip}",
            arguments = listOf(navArgument("isMyTrip") { type = NavType.BoolType })
        ){ backStackEntry ->
            val isMyTrip = backStackEntry.arguments?.getBoolean("isMyTrip") ?: false
            TripInformationScreen(navController, searcherViewModel, chatsViewModel, isMyTrip)
        }
        composable(
            route = "${AppScreens.MapScreen.route}/{isOrigin}",
            arguments = listOf(navArgument("isOrigin") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isOrigin = backStackEntry.arguments?.getBoolean("isOrigin") ?: false
            MapScreen(navController, searcherViewModel, isOrigin)
        }
        composable(AppScreens.TripsScreen.route) {
            TripsScreen(navController, userTripsViewModel, searcherViewModel)
        }
        composable(AppScreens.ProfileScreen.route) {
            ProfileScreen(navController, profileViewModel, searcherViewModel)
        }
        composable(AppScreens.EditUserInfoScreen.route) {
            EditUserInfoScreen(navController, profileViewModel)
        }
        composable(AppScreens.StartTripScreen.route) {
            StartTripScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.OriginInMapScreen.route) {
            OriginInMapScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.DestinationTripScreen.route) {
            DestinationTripScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.DestinationInMapScreen.route) {
            DestinationInMapScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.RouteSelectionScreen.route) {
            RouteSelectionScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.DateSelectionScreen.route) {
            DateSelectionScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.DepartureTimeSelectionScreen.route) {
            DepartureTimeSelectionScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.PassengersNumberScreen.route) {
            PassengersNumberScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.ReservationTypeScreen.route) {
            ReservationTypeScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.PriceRecommendationScreen.route){
            PriceRecommendationScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.PriceSelectorScreen.route){
            PriceSelectorScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.TripPostingScreen.route) {
            TripPostingScreen(navController, postTripsViewModel)
        }
        composable(AppScreens.PublicationConfirmationScreen.route){
            PublicationConfirmationScreen(navController)
        }
        composable(AppScreens.MessagesScreen.route) {
            MessagesScreen(navController, searcherViewModel, chatsViewModel)
        }
        composable(AppScreens.ChatScreen.route) {
            ChatScreen(navController, chatsViewModel)
        }
    }
}