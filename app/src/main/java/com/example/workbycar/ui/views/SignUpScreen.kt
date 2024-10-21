package com.example.workbycar.ui.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.SignUpViewModel

@Composable
fun SignUpScreen(navController: NavController, signUpViewModel: SignUpViewModel){
    val context = LocalContext.current

    LazyColumn (modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item{
            EmailTextView(signUpViewModel)
        }
        item{
            PasswordTextView(signUpViewModel)
        }
        item {
            NameTextView(signUpViewModel)
        }
        item {
            SurnameTextView(signUpViewModel)
        }
        item{
            ButtonSignUp(navController, signUpViewModel, context)
        }
    }
}

@Composable
fun EmailTextView(signUpViewModel: SignUpViewModel){
    TextField(
        value = signUpViewModel.email,
        onValueChange = { signUpViewModel.email = it },
        label = { Text("Add a valid Email") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun NameTextView(signUpViewModel: SignUpViewModel){
    TextField(
        value = signUpViewModel.name,
        onValueChange = { signUpViewModel.name = it },
        label = { Text("Name") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SurnameTextView(signUpViewModel: SignUpViewModel){
    TextField(
        value = signUpViewModel.surname,
        onValueChange = { signUpViewModel.surname = it },
        label = { Text("Surname") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordTextView(signUpViewModel: SignUpViewModel){
    TextField(
        value = signUpViewModel.password,
        onValueChange = {signUpViewModel.password = it},
        label = { Text("Password") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
fun ButtonSignUp(navController: NavController, signUpViewModel: SignUpViewModel, context: Context) {
    Button(
        onClick = {
            signUpViewModel.signUp { success ->
                if (success) {
                    navController.navigate(AppScreens.MainScreen.route) {
                        popUpTo(AppScreens.LogInScreen.route) { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Error on Sign In", Toast.LENGTH_LONG).show()
                }
            }
        }
    ) {
        Text(text = "Sign up")
    }
}

