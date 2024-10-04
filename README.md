package com.deloitte.usnewsapp.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtil {
    private const val ANDROID_KEY_STORE = "AndroidKeyStore"

    private lateinit var secretKey: SecretKey

    init {
        generateSecretKey()
    }

    // Generate a new Secret Key
    private fun generateSecretKey() {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            "myKeyAlias",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        secretKey = keyGenerator.generateKey()
    }

    // Encrypt the password
    fun encrypt(password: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedData = cipher.doFinal(password.toByteArray())
        val iv = cipher.iv // Initialization vector
        return Base64.getEncoder().encodeToString(encryptedData) + ":" + Base64.getEncoder().encodeToString(iv)
    }

    // Decrypt the password
    fun decrypt(encryptedData: String): String {
        val parts = encryptedData.split(":")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = Base64.getDecoder().decode(parts[1])
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val decryptedData = cipher.doFinal(Base64.getDecoder().decode(parts[0]))
        return String(decryptedData)
    }
}


package com.deloitte.usnewsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.deloitte.usnewsapp.utils.EncryptionUtil

class AuthViewModel : ViewModel() {

    private val _loginResult = MutableStateFlow<Boolean?>(null)
    val loginResult: StateFlow<Boolean?> = _loginResult

    private val _signupResult = MutableStateFlow<Boolean?>(null)
    val signupResult: StateFlow<Boolean?> = _signupResult

    private val storedEncryptedPassword = EncryptionUtil.encrypt("testPassword") // Placeholder for demo

    // Function to handle login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Simulated login logic (replace with actual API calls)
            val decryptedPassword = EncryptionUtil.decrypt(storedEncryptedPassword) // Decrypt the stored password
            _loginResult.value = email == "test@example.com" && decryptedPassword == password
        }
    }

    // Function to handle signup
    fun signup(username: String, email: String, password: String) {
        viewModelScope.launch {
            // Simulated signup logic (replace with actual API calls)
            if (email != "test@example.com") {
                val encryptedPassword = EncryptionUtil.encrypt(password)
                // Store encryptedPassword in a secure location (e.g., local database)
                _signupResult.value = true // Signup success
            } else {
                _signupResult.value = false // Signup failed (email already exists)
            }
        }
    }
}



package com.deloitte.usnewsapp.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
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
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {

    val customColor = Color(0xFF6495ED)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Observing login result from the ViewModel
    val loginResult by viewModel.loginResult.collectAsState(initial = null)

    // Update UI based on login result
    LaunchedEffect(loginResult) {
        when (loginResult) {
            true -> {
                Toast.makeText(
                    navController.context,
                    "Login successful",
                    Toast.LENGTH_SHORT
                ).show()
                navController.navigate("home")
            }
            false -> errorMessage = "Invalid Email or Password"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(customColor),
        contentAlignment = Alignment.Center
    ) {
        Card(modifier = Modifier
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
                Text("Login", fontSize = 30.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.login(email, password) // Call login function
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = customColor, contentColor = Color.White)
                ) {
                    Text("Login")
                }
                TextButton(onClick = { navController.navigate("signup") }) {
                    Text("Don't have an account? Sign up", color = customColor, fontSize = 16.sp)
                }
                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Color.Red)
                }
            }
        }
    }
}



package com.deloitte.usnewsapp.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
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

    // Observing signup result from the ViewModel
    val signupResult by viewModel.signupResult.collectAsState(initial = null)

    // Update UI based on signup result
    LaunchedEffect(signupResult) {
        if (signupResult == true) {
            Toast.makeText(
                navController.context,
                "Signup successful",
                Toast.LENGTH_SHORT
            ).show()
            navController.navigate("login")
        } else if (signupResult == false) {
            errorMessage = "Email already exists"
        }
    }

    // Function to validate email format
    fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        return emailRegex.matches(email)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(customColor),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(14.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Signup", fontSize = 30.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (isEmailValid(email) && password == confirmPassword) {
                            viewModel.signup(username, email, password) // Call signup function
                        } else {
                            errorMessage = "Please check your input"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = customColor, contentColor = Color.White)
                ) {
                    Text("Signup")
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






