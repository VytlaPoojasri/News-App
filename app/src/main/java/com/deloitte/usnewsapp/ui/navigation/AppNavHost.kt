package com.deloitte.usnewsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.deloitte.usnewsapp.NewsApp
import com.deloitte.usnewsapp.ui.screens.login.LoginScreen
import com.deloitte.usnewsapp.ui.screens.login.SignupScreen
import com.deloitte.usnewsapp.viewmodel.AuthViewModel

@Composable
fun AppNavHost(navController: NavHostController, viewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, viewModel) }
        composable("signup") { SignupScreen(navController, viewModel) }
        composable("home") { NewsApp() }
    }
}