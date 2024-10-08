package com.deloitte.usnewsapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.deloitte.usnewsapp.data.news.remote.RetrofitInstance
import com.deloitte.usnewsapp.viewmodel.NewsViewModel
import com.deloitte.usnewsapp.viewmodel.NewsViewModelFactory
import com.deloitte.usnewsapp.ui.screens.news.DetailedNewsScreen
import com.deloitte.usnewsapp.ui.screens.news.NewsScreen
import com.deloitte.usnewsapp.ui.theme.USNewsAppTheme
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.deloitte.usnewsapp.ui.navigation.AppNavHost
import com.deloitte.usnewsapp.ui.screens.login.LoginScreen
import com.deloitte.usnewsapp.ui.screens.login.SignupScreen
import com.deloitte.usnewsapp.ui.screens.news.WebViewScreen
import com.deloitte.usnewsapp.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()
            AppNavHost(navController = navController, viewModel = authViewModel)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsApp(viewModel: AuthViewModel) {
    USNewsAppTheme {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()
        val customColor = Color(0xFF6495ED)
        var topBarTitle by remember { mutableStateOf("Top Headlines") }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = { DrawerContent(navController, { coroutineScope.launch { drawerState.close() } }, viewModel) }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(topBarTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White) },

                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu,
                                    contentDescription = "Open Drawer",
                                    tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = customColor)
                    )
                }
            ) { innerPadding ->
                NavGraph(navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    onCategorySelected = { category ->
                        topBarTitle = "$category News"
                    },
                    onDetailedNewsSelected = { articleTitle ->
                        topBarTitle = articleTitle
                    }
                )
            }
        }
    }
}


@Composable
fun DrawerContent(navController: NavController, onClose: () -> Unit, viewModel: AuthViewModel) {
    val categories = listOf("Business", "Entertainment", "Health", "Science", "Sports", "Technology")
    val customColor = Color(0xFF6495ED)
    val username by viewModel.username.observeAsState("")

    Box(modifier = Modifier
        .fillMaxHeight()
        .width(250.dp)
        .background(customColor)) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(text = "News Categories", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            categories.forEach { category ->
                Text(
                    text = category,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("news_screen/$category")
                            onClose() // Close the drawer when a category is selected
                        }
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(text = "Logged in as: \n $username", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)


        }
    }
}

@Composable
fun NavGraph(navController: NavHostController,
             modifier: Modifier = Modifier,
             onCategorySelected: (String) -> Unit,
             onDetailedNewsSelected: (String) -> Unit) {
    val context = navController.context
    val viewModel: NewsViewModel = viewModel(factory = NewsViewModelFactory(RetrofitInstance.api, context))

    NavHost(navController = navController, startDestination = "news_screen/Top", Modifier.then(modifier)) {
        composable("news_screen/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "Top"
            onCategorySelected(category) // Updates the TopAppBar title
            NewsScreen(navController = navController, viewModel = viewModel, category = category)
        }
        composable("detailed_news_screen/{articleUrl}") { backStackEntry ->
            val articleUrl = backStackEntry.arguments?.getString("articleUrl") ?: ""
            val article = viewModel.getArticleById(articleUrl)
            onDetailedNewsSelected(article?.title ?: "Detailed News") // Updates the TopAppBar title
            DetailedNewsScreen(articleUrl, viewModel, navController)
        }
        composable("webview_screen/{url}") { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            WebViewScreen(url)
        }
    }
}

