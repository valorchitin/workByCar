package com.example.workbycar.ui.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.workbycar.R
import com.example.workbycar.ui.navigation.AppScreens
import com.example.workbycar.ui.view_models.LoginViewModel


@Composable
fun LogInScreen(navController: NavController, loginViewModel: LoginViewModel){
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        loginViewModel.email = ""
        loginViewModel.password = ""
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_image),
            contentDescription = "Login Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your route awaits,\nlet's get started",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0277BD),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        EmailTextViewForLogIn(loginViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextViewForLogIn(loginViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        ButtonLogIn(navController, loginViewModel, context)

        Spacer(modifier = Modifier.height(48.dp))

        HyperLinkText(navController)
    }

}

@Composable
fun EmailTextViewForLogIn(loginViewModel: LoginViewModel){
    TextField(
        value = loginViewModel.email,
        onValueChange = { loginViewModel.email = it },
        label = {Text("Email")},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.dp,
                color = Color.White,
                shape = CutCornerShape(5.dp)
            )
            .testTag("emailTestView")
    )
}

@Composable
fun PasswordTextViewForLogIn(loginViewModel: LoginViewModel){
    TextField(
        value = loginViewModel.password,
        onValueChange = {loginViewModel.password = it},
        label = {Text("Password")},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.dp,
                color = Color.White,
                shape = CutCornerShape(5.dp)
            )
            .testTag("passwordTextView"),
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
                Toast.makeText(context, "Wrong credentials", Toast.LENGTH_LONG).show()
            }
        }},
    ) {
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HyperLinkText(navController: NavController){
    Text(
        text = "Sign up",
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable{
            navController.navigate(AppScreens.SignUpScreen.route)
        },
        style = MaterialTheme.typography.titleLarge,
        color = Color(0xFF0061A8),
        textDecoration = TextDecoration.Underline
    )
}
