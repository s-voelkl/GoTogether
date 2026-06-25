package gotogether.frontend.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import gotogether.frontend.android.ui.components.NeoCard
import gotogether.frontend.android.ui.screens.ChallengesScreen
import gotogether.frontend.android.ui.screens.LoginScreen
import gotogether.frontend.android.ui.screens.MapScreen
import gotogether.frontend.android.ui.screens.ProfileScreen
import gotogether.frontend.android.ui.screens.SignupScreen
import gotogether.frontend.android.ui.theme.Black
import gotogether.frontend.android.ui.theme.GoTogetherTheme
import gotogether.frontend.android.viewmodel.MainViewModel

/**
 * MainActivity is the core entry point of the GoTogether Android application.
 * It manages the root UI structure, including the bottom navigation and global overlays
 * like the AI Assistant suggestions.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoTogetherTheme {
                MainScreen()
            }
        }
    }
}

/**
 * Navigation routes.
 */
sealed class Screen(val route: String, val label: String = "", val icon: ImageVector? = null) {
    object Challenges : Screen("challenges", "Challenges", Icons.Default.Home)
    object Map : Screen("map", "Map", Icons.Default.LocationOn)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Login : Screen("login")
    object Signup : Screen("signup")
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val currentUser by viewModel.currentUser.collectAsState()
    
    val navigationItems = listOf(
        Screen.Challenges,
        Screen.Map,
        Screen.Profile,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = navigationItems.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Black
                ) {
                    navigationItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = null) },
                            label = { Text(screen.label, fontWeight = FontWeight.Bold) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Black,
                                selectedTextColor = Black,
                                indicatorColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = if (currentUser == null) Screen.Login.route else Screen.Challenges.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        viewModel = viewModel,
                        onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                        onLoginSuccess = { 
                            navController.navigate(Screen.Challenges.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.Signup.route) {
                    SignupScreen(
                        viewModel = viewModel,
                        onNavigateToLogin = { navController.popBackStack() },
                        onSignupSuccess = {
                            navController.navigate(Screen.Challenges.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.Challenges.route) {
                    ChallengesScreen(viewModel)
                }
                composable(Screen.Map.route) {
                    MapScreen(viewModel)
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(viewModel)
                }
            }

            // AI Assistant Overlay
            val aiMessage by viewModel.aiMessage.collectAsState()
            if (currentUser != null) {
                aiMessage?.let { message ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        NeoCard(
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 32.dp)
                        ) {
                            Text(
                                text = "💡 AI ASSISTANT",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            TextButton(
                                onClick = { viewModel.dismissAiMessage() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(
                                    text = "GOT IT",
                                    color = Black,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            // Error Message Overlay
            val errorMessage by viewModel.errorMessage.collectAsState()
            errorMessage?.let { message ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    NeoCard(
                        backgroundColor = Color(0xFFFF5252), // A bright red for errors
                        modifier = Modifier.padding(bottom = 80.dp)
                    ) {
                        Text(
                            text = "⚠️ ERROR",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        TextButton(
                            onClick = { viewModel.dismissErrorMessage() },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "DISMISS",
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
