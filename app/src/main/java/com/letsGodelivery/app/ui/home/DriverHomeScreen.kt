
package com.letsGodelivery.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.letsGodelivery.app.ui.auth.AuthViewModel // Assuming AuthViewModel has signOut
// import androidx.hilt.navigation.compose.hiltViewModel // If you set up Hilt

/**
 * DriverHomeScreen: The main screen for logged-in drivers.
 *
 * @param authViewModel ViewModel to handle user authentication state and actions like sign out.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverHomeScreen(
    authViewModel: AuthViewModel // Pass the AuthViewModel
) {
    val userProfile by authViewModel.currentUserProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(16.dp), // Additional padding for content
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome, Driver!",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
            userProfile?.displayName?.let { name ->
                if (name.isNotBlank()) {
                    Text(text = name, fontSize = 20.sp)
                }
            }
            userProfile?.email?.let { email ->
                Text(text = email, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Placeholder for driver-specific features
            Text("Available delivery requests and your route information will appear here.")
            // Add switch for "Online/Offline" status later

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { authViewModel.signOut() }) {
                Text("Sign Out")
            }
        }
    }
}

// Similar to CustomerHomeScreen, preview might be tricky without a mock ViewModel
// @Preview(showBackground = true)
// @Composable
// fun DriverHomeScreenPreview() {
//    LetsGoTheme { // Your theme
//        DriverHomeScreen(authViewModel = AuthViewModel()) // This might crash
//    }
// }
