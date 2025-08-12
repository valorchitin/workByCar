package com.example.workbycar.ui.views.profile

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.profile.ProfileViewModel
import com.example.workbycar.utils.CountryCodes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditUserInfoScreen(navController: NavController, profileViewModel: ProfileViewModel){
    Scaffold(topBar = {
        TopAppBar(
            title = {Text(text = "Personal Data", fontWeight = FontWeight.Bold)},
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Name:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            NameTextView(profileViewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Text(
                text = "Surname:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            SurnameTextView(profileViewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Text(
                text = "Description:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            DescriptionTextView(profileViewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Text(
                text = "Phone prefix:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            PhoneCodeDropDown(profileViewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Text(
                text = "Phone number:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            PhoneTextView(profileViewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Text(
                text = "Birthdate:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            BirthDateTextField(profileViewModel)
            Spacer(modifier = Modifier.height(32.dp))
        }
        item {
            SaveChangesButton(profileViewModel, context, navController)
        }
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
fun DescriptionTextView(profileViewModel: ProfileViewModel){
    TextField(
        value = profileViewModel.description,
        onValueChange = { profileViewModel.description = it },
        label = { Text("Description") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        modifier = Modifier.fillMaxWidth()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BirthDateTextField(profileViewModel: ProfileViewModel) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = selectedDate?.let { dateFormat.format(Date(it)) } ?: (dateFormat.format(profileViewModel.birthDate))

    TextField(
        value = formattedDate,
        onValueChange = {},
        label = { Text("Date of Birth") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable (
                onClick = {
                    showDatePicker = true
                }
            ),
        enabled = false,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledIndicatorColor = MaterialTheme.colorScheme.outline,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

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
    var expanded by remember{ mutableStateOf(false) }
    var selectedCode by remember{ mutableStateOf(profileViewModel.prefix) }

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
        profileViewModel.editUserInfo(profileViewModel.name, profileViewModel.surname, profileViewModel.birthDate, profileViewModel.description, profileViewModel.prefix, profileViewModel.phone)
        Toast.makeText(context, "Saved correctly", Toast.LENGTH_LONG).show()
        navController.navigate(AppScreens.ProfileScreen.route)
    }) {
        Text(
            text = "Save",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}