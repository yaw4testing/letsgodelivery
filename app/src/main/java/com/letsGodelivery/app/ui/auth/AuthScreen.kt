// File: com/letsGodelivery/app/ui/auth/AuthScreen.kt
package com.letsGodelivery.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.letsGodelivery.app.ui.theme.LetsGoTheme // Import your app's theme

/**
 * AuthScreen: Provides options to navigate to Login or Sign Up.
 * This screen itself does not perform navigation; it calls lambdas
 * provided by the navigation host.
 *
 * @param onLoginClicked Lambda to be invoked when the Login button is clicked.
 * @param onSignUpClicked Lambda to be invoked when the Sign Up button is clicked.
 */
@Composable
fun AuthScreen(
    onLoginClicked: () -> Unit,
    onSignUpClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Your Multi-Service Delivery Solution",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onLoginClicked, // Call the provided lambda
            modifier = Modifier.width(200.dp)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSignUpClicked, // Call the provided lambda
            modifier = Modifier.width(200.dp)
        ) {
            Text("Sign Up")
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun AuthScreenPreview() {
    LetsGoTheme { // Replace with your app's actual theme if different
        AuthScreen(onLoginClicked = {}, onSignUpClicked = {})
    }
}