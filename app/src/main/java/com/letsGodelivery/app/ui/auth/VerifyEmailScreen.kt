package com.letsGodelivery.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onEmailVerified: () -> Unit,
    onNavigateBack: () -> Unit
){
    val authState by authViewModel.authUiState.collectAsState()
    val context = LocalContext.current
    var isCheckingStatus by remember { mutableStateOf(false) }
    var actionMessage by remember { mutableStateOf<String?>("") }

    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success && (authState as AuthUiState.Success).firebaseUser.isEmailVerified){
            onEmailVerified()

        }
        if (authState is AuthUiState.VerificationEmailSent){
            actionMessage = "Verification email sent!"
        }
        if (authState is AuthUiState.Error){
            actionMessage = (authState as AuthUiState.Error).message
        }

    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Verify Email") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val currentUserEmail = authViewModel.getCurrentFirebaseUser()?.email ?: "your email"

            Text(
                text = "A verification link has been sent to:",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentUserEmail,
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Please check your inbox (and spam folder) and click the link to activate your account.",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (isCheckingStatus) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
            }

            actionMessage?.let {
                Text(it, color = if (authState is AuthUiState.Error) MaterialTheme.colorScheme.error else LocalContentColor.current)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    isCheckingStatus = true
                    authViewModel.checkEmailVerificationStatus { verified ->
                        isCheckingStatus = false
                        if (!verified) {
                            actionMessage = "Email not verified yet. Please check your email or resend."
                        }
                        // Navigation will be handled by the LaunchedEffect above if verified
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I've Verified / Refresh Status")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    authViewModel.sendVerificationEmail { success, message ->
                        actionMessage = message // Update message based on outcome
                        // Toast.makeText(context, message, Toast.LENGTH_LONG).show() // Or use Toast
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Resend Verification Email")
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = {
                authViewModel.signOut() // Sign out the user
                onNavigateBack()    // Navigate back to login/signup choice
            }) {
                Text("Sign Out & Go Back")
            }
        }
    }

}

