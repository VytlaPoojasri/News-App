package com.deloitte.usnewsapp.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.deloitte.usnewsapp.viewmodel.AuthViewModel

@Composable
fun SignupScreen(navController: NavController, viewModel: AuthViewModel) {

    val customColor = Color(0xFF6495ED)

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Function to validate email format
    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
        return emailRegex.matches(email)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(customColor),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(14.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sign Up", fontSize = 30.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(onClick = {
                    when {
                        !isEmailValid(email) -> {
                            errorMessage = "Invalid email format"
                        }
                        password != confirmPassword -> {
                            errorMessage = "Passwords do not match"
                        }
                        else -> {
                            viewModel.signup(username, email, password).observeForever { isSignedUp ->
                                if (isSignedUp) {
                                    Toast.makeText(
                                        navController.context,
                                        "Signup successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("login")
                                } else {
                                    errorMessage = "Email already exists"
                                }
                            }
                        }
                    }
                },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = customColor, contentColor = Color.White)) {
                    Text("Sign Up")
                }
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Already have an account? Login", color = customColor, fontSize = 16.sp)
                }
                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color.Red)
                }
            }
        }

    }
}
