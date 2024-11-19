package com.example.workbycar.ui.views.sign_up

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AddPhoneScreen(navController: NavController, signUpViewModel: SignUpViewModel){
    val context = LocalContext.current

    var isSignUpSucces by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        signUpViewModel.loadCurrentUser()
        Log.d("Datos sign up: ", signUpViewModel.name)
    }
    if (!isSignUpSucces) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PhoneCodeDropDown(signUpViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            PhoneTextView(signUpViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            ButtonSendMessage(signUpViewModel, context){ signUpSuccesful ->
                isSignUpSucces = signUpSuccesful
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Message sent.")
            Text("Please verify your email address through mail")
            ContinueButton(navController, context)
            Text(
                text = "If you have not received the message\nor\nencountered another problem,\nclick the following link:",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            SendMessageHyperLinkText(signUpViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneCodeDropDown(signUpViewModel: SignUpViewModel) {
    val countryCodes = listOf(
        "United States (+1)",
        "United Kingdom (+44)",
        "Canada (+1)",
        "France (+33)",
        "Germany (+49)",
        "Spain (+34)",
        "Mexico (+52)"
    )

    var expanded by remember{ mutableStateOf(false) }
    var selectedCode by remember{ mutableStateOf("") }

    // Caja que contiene el TextField y el menú desplegable
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        // TextField que actúa como disparador del menú
        TextField(
            value = selectedCode,
            onValueChange = {
                selectedCode = it
            },
            label = {Text("Select Country Code")},
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor()
        )

        // Menú desplegable
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countryCodes.forEach { countryCode ->
                DropdownMenuItem(
                    text = { Text(countryCode) },
                    onClick = {
                        selectedCode = countryCode
                        expanded = false

                        val regex = "\\((\\+\\d+)\\)".toRegex()
                        val prefixMatch = regex.find(selectedCode)
                        signUpViewModel.prefix = prefixMatch?.groups?.get(1)?.value ?: ""
                    }
                )
            }
        }
    }
}

@Composable
fun PhoneTextView(signUpViewModel: SignUpViewModel) {
    TextField(
        value = signUpViewModel.phone,
        onValueChange = { newValue: String ->
            signUpViewModel.phone = newValue
        },
        label = {Text("Phone number")},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ButtonSendMessage(signUpViewModel: SignUpViewModel, context: Context, onSignUpSucces: (Boolean) -> Unit){
    Button(
        onClick = {
            if (signUpViewModel.phoneNotEmpty()) {
                signUpViewModel.signUp { success ->
                       if (success) {
                           onSignUpSucces(true)
                       } else {
                            Toast.makeText(context, "Error on Sign In", Toast.LENGTH_LONG).show()
                       }
                }
            } else {
                Toast.makeText(context, "Empty requested values", Toast.LENGTH_LONG).show()
            }
        }
    ) {
        Text("Add phone")
    }
}

@Composable
fun SendMessageHyperLinkText(signUpViewModel: SignUpViewModel){
    Text(
        text = "send message again",
        modifier = Modifier.clickable{
            signUpViewModel.sendVerificationEmail()
        },
        color = Color.Blue,
        textDecoration = TextDecoration.Underline
    )
}

@Composable
fun ContinueButton(navController: NavController, context: Context){
    Button(
        onClick ={
            val user = FirebaseAuth.getInstance().currentUser
            user?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    if (user.isEmailVerified){
                        navController.navigate(AppScreens.MainScreen.route)
                    } else {
                        Toast.makeText(context,"Verify your email",  Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Error reloading user data", Toast.LENGTH_LONG).show()
                }
            }
        }
    ) {
        Text(text = "Continue")
    }
}