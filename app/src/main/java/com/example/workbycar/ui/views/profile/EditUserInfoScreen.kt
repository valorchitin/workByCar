package com.example.workbycar.ui.views.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.example.workbycar.ui.view_models.SignUpViewModel
import com.example.workbycar.ui.view_models.profile.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditUserInfoScreen(navController: NavController, profileViewModel: ProfileViewModel){
    Scaffold(topBar = {
        TopAppBar(
            title = {Text(text = "Personal Data")},
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Arrow back")
                }
            }
        )
    }) { paddingValues ->
            EditUserInfoContent(Modifier.padding((paddingValues)), profileViewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditUserInfoContent(modifier: Modifier = Modifier, profileViewModel: ProfileViewModel){
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NameTextView(profileViewModel)
        SurnameTextView(profileViewModel)
        BirthDateButton(profileViewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NameTextView(profileViewModel: ProfileViewModel){
    TextField(
        value = profileViewModel.name,
        onValueChange = { profileViewModel.name = it },
        label = { Text("Name") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        modifier = Modifier.fillMaxWidth()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SurnameTextView(profileViewModel: ProfileViewModel){
    TextField(
        value = profileViewModel.surname,
        onValueChange = { profileViewModel.surname = it },
        label = { Text("Surname") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        modifier = Modifier.fillMaxWidth()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BirthDateButton(profileViewModel: ProfileViewModel){
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
                profileViewModel.birthDate = date
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