package com.deloitte.usnewsapp.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.deloitte.usnewsapp.viewmodel.AuthViewModel


@Composable
fun SignupScreen(navController: NavController, viewModel: AuthViewModel) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick = {
            if (password == confirmPassword) {
                viewModel.signup(username, email, password).observeForever { isSignedUp ->
                    if (isSignedUp) {
                        Toast.makeText(navController.context, "Signup successful", Toast.LENGTH_SHORT).show()
                        navController.navigate("login")
                    } else {
                        errorMessage = "Email already exists"
                    }
                }
            } else {
                errorMessage = "Passwords do not match"
            }
        }) {
            Text("Sign Up")
        }
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login")
        }
        if (errorMessage.isNotEmpty()) {
            Text(errorMessage)
        }
    }
}
