package com.example.workbycar.ui.views.profile

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
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
            EditUserInfoContent(Modifier.padding((paddingValues)), profileViewModel, navController)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditUserInfoContent(modifier: Modifier = Modifier, profileViewModel: ProfileViewModel, navController: NavController){
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Name:")
        NameTextView(profileViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Surname:")
        SurnameTextView(profileViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Phone prefix:")
        PhoneCodeDropDown(profileViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Phone number: ")
        PhoneTextView(profileViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Birthdate: ")
        BirthDateButton(profileViewModel)
        Spacer(modifier = Modifier.height(32.dp))
        SaveChangesButton(profileViewModel, context, navController)
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
    val formattedDate = selectedDate?.let { dateFormat.format(Date(it)) } ?: (dateFormat.format(profileViewModel.birthDate))


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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneCodeDropDown(profileViewModel: ProfileViewModel) {
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
    var selectedCode by remember{ mutableStateOf(profileViewModel.prefix) }

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
            modifier = Modifier.fillMaxWidth().menuAnchor()
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
                        profileViewModel.prefix = prefixMatch?.groups?.get(1)?.value ?: ""
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PhoneTextView(profileViewModel: ProfileViewModel) {
    TextField(
        value = profileViewModel.phone,
        onValueChange = { newValue: String ->
            profileViewModel.phone = newValue
        },
        label = { Text("Phone number") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SaveChangesButton(profileViewModel: ProfileViewModel, context: Context, navController: NavController){
    Button(onClick = {
        profileViewModel.editUserInfo(profileViewModel.name, profileViewModel.surname, profileViewModel.birthDate, profileViewModel.prefix, profileViewModel.phone)
        Toast.makeText(context, "Saved correctly", Toast.LENGTH_LONG).show()
        navController.navigate(AppScreens.ProfileScreen.route)
    }) {
        Text("Save")
    }
}