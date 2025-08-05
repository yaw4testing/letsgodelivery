package com.letsGodelivery.app.ui.request

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.letsGodelivery.app.data.models.DeliveryRequest
import com.letsGodelivery.app.data.models.RequestStatus
import com.letsGodelivery.app.data.models.RequestType
import com.letsGodelivery.app.data.models.RequestViewModel
import com.letsGodelivery.app.ui.auth.AuthViewModel
import com.letsGodelivery.app.ui.theme.LetsGoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreen(
    onDone: () -> Unit,
    // Assuming RequestViewModel is Hilt-injected
    requestViewModel: RequestViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var description by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf("") }
    var dropoffAddress by remember { mutableStateOf("") }
    var selectedRequestType by remember { mutableStateOf(RequestType.PARCEL) } // Default
    var parcelSize by remember { mutableStateOf("") } // For parcel type
    var errorMessage by remember { mutableStateOf("") }
    var fee by remember { mutableDoubleStateOf(0.0) }

    val userProfile by authViewModel.currentUserProfile.collectAsState()


// Observe the UI state from your ViewModel
// val uiState by requestViewModel.newRequestUiState.collectAsState() // Example state

// Example: Navigate back on success
// LaunchedEffect(uiState) {
//     if (uiState is NewRequestUiState.Success) {
//         onNavigateBack()
//         // Optionally show a success message (e.g., using a Snackbar)
//         // requestViewModel.resetNewRequestUiState() // Reset state in ViewModel
//     }
// }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Request") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
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
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Make content scrollable
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Request Details", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // Request Type Picker (Example using Radio Buttons)
            Text("Select Request Type:")
            RequestType.entries.forEach { type ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedRequestType == type),
                        onClick = { selectedRequestType = type }
                    )
                    Text(
                        text = type.name.replaceFirstChar { it.titlecase() },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description of items/service") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pickupAddress,
                onValueChange = { pickupAddress = it },
                label = { Text("Pickup Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            // Consider adding a map icon to pick location from map
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dropoffAddress,
                onValueChange = { dropoffAddress = it },
                label = { Text("Drop-off Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            // Consider adding a map icon to pick location from map
            Spacer(modifier = Modifier.height(16.dp))

            if (selectedRequestType == RequestType.PARCEL) {
                OutlinedTextField(
                    value = parcelSize,
                    onValueChange = { parcelSize = it },
                    label = { Text("Parcel Size (e.g., Small, Envelope)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Add more parcel-specific fields like weight if needed
            }

            OutlinedTextField(
                value = fee.toString(),
                onValueChange = { fee = it.toDoubleOrNull() ?: 0.0 },
                label = { Text("Fee") },
                modifier = Modifier.fillMaxWidth()
            )

            // Add fields for preferred delivery time, etc.

            Spacer(modifier = Modifier.weight(1f)) // Push button to bottom

// if (uiState is NewRequestUiState.Error) {
//     Text(
//         text = (uiState as NewRequestUiState.Error).message,
//         color = MaterialTheme.colorScheme.error,
//         modifier = Modifier.padding(bottom = 8.dp)
//     )
// }
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Button(
                onClick = {
                    val currentProfile = userProfile
                    if (currentProfile == null) {
                        errorMessage = "User information not available. Please login again."
                        return@Button
                    }

                    if (description.isNotBlank() && pickupAddress.isNotBlank() && dropoffAddress.isNotBlank()) {
                        errorMessage = ""
                        val request = DeliveryRequest(
                            customerId = currentProfile.uid,
                            userDisplayName = currentProfile.displayName,
                            requestType = selectedRequestType,
                            description = description,
                            pickupAddress = pickupAddress,
                            dropoffAddress = dropoffAddress,
                            parcelSize = if (selectedRequestType == RequestType.PARCEL) parcelSize else "",
                            createdAt = Timestamp.now(),
                            status = RequestStatus.OPEN.name,
                            assignedDriverId = null,
                            fee = fee,
                            pickupLat = 0.0,
                            dropoffLat = 0.0,
                            pickupLng = 0.0,
                            dropoffLng = 0.0
                            //createdAt = androidx.compose.animation.graphics.vector.Timestamp.now()
                            // ... populate other fields
                        )
                        requestViewModel.createRequest(request)
                        onDone()
                    } else {
                        errorMessage = "Please fill in all fields"
                        return@Button
                    }
                },
                modifier = Modifier.fillMaxWidth()){
                Text("Submit Request")
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewRequestScreenPreview() {
    LetsGoTheme {
        NewRequestScreen(onDone = {})
    }
}
