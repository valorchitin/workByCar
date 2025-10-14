package com.example.workbycar.ui.views.sign_up

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.SignUpViewModel
import com.example.workbycar.utils.CountryCodes
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AddPhoneScreen(navController: NavController, signUpViewModel: SignUpViewModel){
    val context = LocalContext.current

    var isSignUpSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        signUpViewModel.loadCurrentUser()
        Log.d("Datos sign up: ", signUpViewModel.name)
    }
    if (!isSignUpSuccess) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
            text = "Enter Your\nPhone Number",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0061A8),
            textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Country Code:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            PhoneCodeDropDown(signUpViewModel)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Phone Number:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            PhoneTextView(signUpViewModel)
            Spacer(modifier = Modifier.height(32.dp))

            ButtonSendMessage(signUpViewModel, context){ signUpSuccessful ->
                isSignUpSuccess = signUpSuccessful
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
            Text(
                text = "Message sent.\nPlease verify your email address through mail",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0061A8),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "If you have not received the message\nor\nencountered another problem,\nclick the following link:",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424242)
            )
            SendMessageHyperLinkText(signUpViewModel)
            Spacer(modifier = Modifier.height(32.dp))
            ContinueButton(navController, context)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneCodeDropDown(signUpViewModel: SignUpViewModel) {
    var expanded by remember{ mutableStateOf(false) }
    var selectedCode by remember{ mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedCode,
            onValueChange = {
                selectedCode = it
            },
            label = {Text("Select Country Code")},
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .border(
                    width = 0.dp,
                    color = Color.White,
                    shape = CutCornerShape(5.dp)
                )
                .testTag("prefixDropdown")
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CountryCodes.countryCodes.forEach { countryCode ->
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
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.dp,
                color = Color.White,
                shape = CutCornerShape(5.dp)
            )
            .testTag("phoneTextView")
    )
}

@Composable
fun ButtonSendMessage(signUpViewModel: SignUpViewModel, context: Context, onSignUpSuccess: (Boolean) -> Unit){
    Button(
        onClick = {
            if (signUpViewModel.phoneNotEmpty()) {
                signUpViewModel.signUp { success ->
                       if (success) {
                           onSignUpSuccess(true)
                       } else {
                            Toast.makeText(context, "Error on Sign In", Toast.LENGTH_LONG).show()
                       }
                }
            } else {
                Toast.makeText(context, "Empty requested values", Toast.LENGTH_LONG).show()
            }
        }
    ) {
        Text(
            text = "Add phone",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SendMessageHyperLinkText(signUpViewModel: SignUpViewModel){
    Text(
        text = "send message again",
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable{
            signUpViewModel.sendVerificationEmail()
        },
        style = MaterialTheme.typography.titleLarge,
        color = Color(0xFF0061A8),
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
        },
    ) {
        Text(
            text = "Continue",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}