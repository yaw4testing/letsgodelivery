package com.letsGodelivery.app.ui.request

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.letsGodelivery.app.data.models.RequestViewModel

@Composable
fun RequestListScreen(
    onAccept: (String) -> Unit,
    requestViewModel: RequestViewModel = hiltViewModel()
) {
    val requests by requestViewModel.openRequests.collectAsState()
    val loading by requestViewModel.loading.collectAsState()
    val error by requestViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        requestViewModel.loadOpenRequests()
    }
    when {
        loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error loading requests:\n$error", color = MaterialTheme.colorScheme.error)
            }
        }
        requests.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No open requests available at the moment")
            }
        }
        else -> {
            LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
                items(requests) { req ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onAccept(req.id) }
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("Type: ${req.requestType}")
                            Text("From: (${req.pickupLat}, ${req.pickupLng})")
                            Text("To:   (${req.dropoffLat}, ${req.dropoffLng})")
                            Text("Fee: ${req.fee}")
                        }
                    }
                }
            }
        }
    }



}
