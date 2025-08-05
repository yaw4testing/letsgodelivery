// File: com/letsGodelivery/app/ui/main/CustomerHomeScreen.kt
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.letsGodelivery.app.ui.auth.AuthViewModel // Assuming AuthViewModel has signOut
import com.letsGodelivery.app.ui.theme.LetsGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    authViewModel: AuthViewModel // Pass the AuthViewModel
) {
    val userProfile by authViewModel.currentUserProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Dashboard") },
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
                text = "Welcome, Customer!",
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
            userProfile?.address?.let { address ->
                Text(text = address, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Placeholder for customer-specific features
            Text("Your upcoming deliveries and favorite services will appear here.")

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { authViewModel.signOut() }) {
                Text("Sign Out")
            }
        }
    }
}


 @Preview(showBackground = true)
 @Composable
 fun CustomerHomeScreenPreview() {
    LetsGoTheme { // Your theme
        // You'd need a mock AuthViewModel or a parameterless constructor for preview
        //CustomerHomeScreen() // This might crash if AuthViewModel has dependencies
    }
 }
