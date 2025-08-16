package com.letsGodelivery.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.letsGodelivery.app.data.models.RequestViewModel
import com.letsGodelivery.app.data.models.UserType
import com.letsGodelivery.app.ui.auth.AuthScreen
import com.letsGodelivery.app.ui.auth.AuthUiState
import com.letsGodelivery.app.ui.auth.AuthViewModel
import com.letsGodelivery.app.ui.auth.LoginScreen
import com.letsGodelivery.app.ui.auth.SignUpScreen
import com.letsGodelivery.app.ui.auth.VerifyEmailScreen
import com.letsGodelivery.app.ui.home.CustomerHomeScreen
import com.letsGodelivery.app.ui.home.DriverHomeScreen
import com.letsGodelivery.app.ui.request.NewRequestScreen
import com.letsGodelivery.app.ui.request.RequestListScreen
import com.letsGodelivery.app.ui.theme.LetsGoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LetsGoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigationController()
                }
            }
        }
    }
}

object AppRoutes {
    const val AUTH_ROOT = "auth_root"
    const val AUTH_CHOICE = "auth_choice"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val CUSTOMER_HOME = "customer_home"
    const val DRIVER_HOME = "driver_home"
    const val NEW_REQUEST = "new_request"
    const val BROWSE_REQUESTS = "browse_requests"
    const val VERIFY_EMAIL = "verify_email"
}
@Composable
fun AppNavigationController(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
    requestViewModel: RequestViewModel = hiltViewModel() // Assuming you need this elsewhere
) {
    val authState by authViewModel.authUiState.collectAsState()
    val userProfile by authViewModel.currentUserProfile.collectAsState()

    // This LaunchedEffect is for reacting to login/logout events AFTER the initial app load
    // and when the user is actively on an auth screen (Login, SignUp, AuthChoice).
    LaunchedEffect(authState, userProfile) { // Also react to userProfile changes if authState is Success
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        when (val currentAuthState = authState) { // Use a local val for smart casting
            is AuthUiState.Success -> {
                if (!currentAuthState.isEmailVerified) {
                    if (currentRoute != AppRoutes.VERIFY_EMAIL) {
                        navController.navigate(AppRoutes.VERIFY_EMAIL) {
                            popUpTo(AppRoutes.AUTH_ROOT) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else {
                    if (currentRoute == AppRoutes.LOGIN ||
                        currentRoute == AppRoutes.SIGN_UP ||
                        currentRoute == AppRoutes.AUTH_CHOICE
                    ) {
                        if (userProfile != null) { // Ensure profile is loaded
                            val destination = if (userProfile?.userType == UserType.DRIVER) {
                                AppRoutes.DRIVER_HOME
                            } else {
                                AppRoutes.CUSTOMER_HOME
                            }
                            navController.navigate(destination) {
                                popUpTo(AppRoutes.AUTH_ROOT) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        // If userProfile is null here, but authState is Success,
                        // it means profile is still loading. The AUTH_ROOT's loading
                        // or the ViewModel's state should handle showing progress.
                    }
                }
            }
            is AuthUiState.Idle -> { // User signed out
                // If not already on an auth screen, navigate to auth choice.
                // Avoid navigating if already on AUTH_CHOICE to prevent loops.
                if (currentRoute != AppRoutes.AUTH_CHOICE && currentRoute != AppRoutes.LOGIN && currentRoute != AppRoutes.SIGN_UP) {
                    navController.navigate(AppRoutes.AUTH_CHOICE) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        // Or, if you want to clear everything and start fresh at auth:
                        // popUpTo(AppRoutes.AUTH_ROOT) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            else -> {}
        }
    }

    NavHost(navController = navController, startDestination = AppRoutes.AUTH_ROOT) {
        composable(AppRoutes.AUTH_ROOT) {
            // This is the initial decision point when the app starts.
            when (val currentAuthState = authState) { // Use a local val for smart casting
                is AuthUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is AuthUiState.Success -> {
                    // User is authenticated via Firebase. Now check if profile is loaded.
                    if (userProfile != null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }

                    }
                }
                is AuthUiState.Idle, is AuthUiState.Error -> {
                    // Not authenticated or error during initial check, navigate to auth choice.
                    // This LaunchedEffect ensures navigation happens after composition.
                    LaunchedEffect(Unit) { // Keyed to Unit
                        navController.navigate(AppRoutes.AUTH_CHOICE) {
                            popUpTo(AppRoutes.AUTH_ROOT) { inclusive = true }
                            launchSingleTop = true // Good practice here too
                        }
                    }
//                    // Show a brief spinner or blank screen while this navigation occurs.
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator() // Or just an empty Box
                    }
                }
                else -> {}
            }
        }

        composable(AppRoutes.AUTH_CHOICE) {
            AuthScreen(
                onLoginClicked = { navController.navigate(AppRoutes.LOGIN) },
                onSignUpClicked = { navController.navigate(AppRoutes.SIGN_UP) }
            )
        }

        composable(AppRoutes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = {
                    navController.navigate(AppRoutes.SIGN_UP) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.SIGN_UP) {
            SignUpScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.SIGN_UP) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoutes.VERIFY_EMAIL){
            VerifyEmailScreen(authViewModel = authViewModel, onEmailVerified = {},
                onNavigateBack = {
                    navController.navigate(AppRoutes.AUTH_CHOICE) {
                        popUpTo(AppRoutes.VERIFY_EMAIL) { inclusive = true }
                    }
                }
            )
        }

        // ... your other composable routes (CUSTOMER_HOME, DRIVER_HOME, etc.)
        composable(AppRoutes.CUSTOMER_HOME) {
            CustomerHomeScreen(authViewModel = authViewModel)
        }

        composable(AppRoutes.DRIVER_HOME) {
            DriverHomeScreen(authViewModel = authViewModel)
        }

        composable(AppRoutes.NEW_REQUEST) {
            NewRequestScreen(
                onDone = { navController.popBackStack() }
                // Make sure AuthViewModel is available here if needed,
                // or pass userId/displayName as arguments.
            )
        }

        composable(AppRoutes.BROWSE_REQUESTS) {
            RequestListScreen(onAccept = { requestId  ->
                val driverId = authViewModel.getCurrentFirebaseUser()?.uid ?: return@RequestListScreen
                requestViewModel.acceptRequest(requestId, driverId)
            })
        }
    }
}

