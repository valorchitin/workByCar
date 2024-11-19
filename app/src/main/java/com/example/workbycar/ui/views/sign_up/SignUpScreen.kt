package com.example.workbycar.ui.views.sign_up

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SignUpScreen(navController: NavController, signUpViewModel: SignUpViewModel){
    val context = LocalContext.current

    var isSignUpSucces by remember { mutableStateOf(false) }

    LazyColumn (modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //if (!isSignUpSucces) {
        item {
            EmailTextViewForSignUp(signUpViewModel)
        }
        item {
            PasswordTextViewForSignUp(signUpViewModel)
        }
        item {
            NameTextView(signUpViewModel)
        }
        item {
            SurnameTextView(signUpViewModel)
        }
        item {
            BirthDateButton(signUpViewModel)
        }
        item {
            ButtonSignUp(signUpViewModel, context, navController)//{ signUpSuccesful ->
                //isSignUpSucces = signUpSuccesful
            //}
        }
        //} else {
        //    item {
        //        Text("Message sent.")
        //        Text("Please verify your email address through mail")
        //    }
        //    item {
        //        ContinueButton(navController, context)
        //    }
        //}
    }
}

@Composable
fun EmailTextViewForSignUp(signUpViewModel: SignUpViewModel){
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
fun PasswordTextViewForSignUp(signUpViewModel: SignUpViewModel){
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
fun BirthDateButton(signUpViewModel: SignUpViewModel){
    var showDatePicker by remember{ mutableStateOf(false) }

    var selectedDate by remember{ mutableStateOf<Long?>(null) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = selectedDate?.let { dateFormat.format(Date(it)) } ?: "Selecciona tu fecha de nacimiento"


    Button(
        onClick = { showDatePicker = true},
        shape = CutCornerShape(0),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text( text = formattedDate)
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { date ->
                signUpViewModel.birthDate = date
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun ButtonSignUp(signUpViewModel: SignUpViewModel, context: Context, navController: NavController) {
    Button(
        onClick = {
            if (signUpViewModel.signUpScreenInputValid()){
                navController.navigate(AppScreens.AddPhoneScreen.route) {
                    popUpTo(AppScreens.SignUpScreen.route) {inclusive = true}
                }
            } else {
                Toast.makeText(context, "Empty requested values", Toast.LENGTH_LONG).show()
            }
        }
    ) {
        Text(text = "Continue")
    }
}