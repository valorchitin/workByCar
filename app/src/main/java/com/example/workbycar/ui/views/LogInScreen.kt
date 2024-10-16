package com.example.workbycar.ui.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.LoginViewModel


@Composable
fun LogInScreen(navController: NavController, loginViewModel: LoginViewModel){
    val context = LocalContext.current

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailTextView(loginViewModel)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextView(loginViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        ButtonLogIn(navController, loginViewModel, context)
        Spacer(modifier = Modifier.height(30.dp))
        HyperLinkText(navController)
    }

}

@Composable
fun EmailTextView(loginViewModel: LoginViewModel){
    TextField(
        value = loginViewModel.email,
        onValueChange = { loginViewModel.email = it },
        label = {Text("Email")},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordTextView(loginViewModel: LoginViewModel){
    TextField(
        value = loginViewModel.password,
        onValueChange = {loginViewModel.password = it},
        label = {Text("Password")},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
fun ButtonLogIn(navController: NavController, loginViewModel: LoginViewModel, context: Context){
    Button(
        onClick = { loginViewModel.login {
                success ->
            if (success) {
                navController.navigate(AppScreens.MainScreen.route) {
                    popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
                }
            } else {
                Toast.makeText(context, "Error on Sign In", Toast.LENGTH_LONG).show()
            }
        }}
    ) {
        Text(text = "Sign In")
    }
}

@Composable
fun HyperLinkText(navController: NavController){
    Text(
        text = "Sign up",
        modifier = Modifier.clickable{
            navController.navigate(AppScreens.SignUpScreen.route)
        },
        color = Color.Blue,
        textDecoration = TextDecoration.Underline
    )
}
