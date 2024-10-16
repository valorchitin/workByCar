package com.example.workbycar

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.workbycar.ui.navigation.AppNavigation
import com.example.workbycar.ui.theme.WorkByCarTheme
import com.example.workbycar.ui.view_models.LoginViewModel
import com.example.workbycar.ui.view_models.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        val loginViewModel: LoginViewModel by viewModels()
        val signUpViewModel: SignUpViewModel by viewModels()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkByCarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(loginViewModel, signUpViewModel)
                }
            }
        }
    }
}
