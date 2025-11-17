package com.example.workbycar.ui.views.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workbycar.ui.view_models.profile.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.workbycar.ui.views.profile.TextInfo
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(navController: NavController, profileViewModel: ProfileViewModel){
    val age = profileViewModel.selectedUser!!.birthDate?.let {
        val birthDate = Date(it).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val today = LocalDate.now()
        Period.between(birthDate, today).years.toString()
    } ?: "Age not available"

    val description = profileViewModel.selectedUser!!.description
        .takeIf { it.isNotEmpty() } ?: "Not description yet"

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "") },
            navigationIcon = {
                IconButton(
                    onClick = {
                    navController.popBackStack()
                    },
                    modifier = Modifier.testTag("back")
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Arrow back")
                }
            }
        )
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .padding(16.dp)
                )
            }

            if (profileViewModel.selectedUser != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TextInfo(Icons.Default.Person, "Name", profileViewModel.selectedUser!!.name)
                            TextInfo(Icons.Default.Person, "Surname", profileViewModel.selectedUser!!.surname)
                            TextInfo(Icons.Default.Cake, "Age", age)
                            TextInfo(Icons.Default.Phone, "Phone", "${profileViewModel.selectedUser!!.prefix} ${profileViewModel.selectedUser!!.phone}")
                            TextInfo(Icons.Default.Info, "Description", description)
                        }
                    }
                }
            }
        }
    }
}