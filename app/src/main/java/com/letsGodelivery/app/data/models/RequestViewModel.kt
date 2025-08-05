package com.letsGodelivery.app.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private val requestsCollection = db.collection("requests")

    private val _openRequests = MutableStateFlow<List<DeliveryRequest>>(emptyList())
    val openRequests: StateFlow<List<DeliveryRequest>> = _openRequests.asStateFlow()

    private val _userRequests = MutableStateFlow<List<DeliveryRequest>>(emptyList())
    val userRequests: StateFlow<List<DeliveryRequest>> = _userRequests.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    /**
     * Creates a new delivery request in Firestore.
     */
    fun createRequest(request: DeliveryRequest) = viewModelScope.launch {
           try {
               val doc = db.collection("requests").document(request.id.ifBlank { db.collection("requests").document().id })
              doc.set(request.copy(id = doc.id)).await()
               } catch (e: Exception) {
                _error.value = e.message
              }
        }
    /**
     * Loads all open requests (status == OPEN).
     */
    fun loadOpenRequests() = viewModelScope.launch {
        try {
            _loading.value = true
            val snapshot = requestsCollection
                .whereEqualTo("status", RequestStatus.OPEN.value)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()
            val list = snapshot.documents.mapNotNull {
                it.toObject(DeliveryRequest::class.java)
            }
            _openRequests.value = list
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _loading.value = false
        }
    }

    /**
     * Customer or driver-specific: Loads requests for a given userId (as customer or assigned driver).
     */
    fun loadUserRequests(userId: String, asDriver: Boolean = false) = viewModelScope.launch {
        try {
            val query = if (asDriver) {
                requestsCollection.whereEqualTo("assignedDriverId", userId)
            } else {
                requestsCollection.whereEqualTo("customerId", userId)
            }

            val snapshot = query
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            _userRequests.value = snapshot.documents.mapNotNull {
                it.toObject(DeliveryRequest::class.java)
            }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    /**
     * Driver accepts an open request by setting assignedDriverId and status.
     */
    fun acceptRequest(request: String, driverId: String) = viewModelScope.launch {
        try {
            requestsCollection.document(request)
                .update(
                    mapOf(
                        "assignedDriverId" to driverId,
                        "status" to RequestStatus.ASSIGNED.value
                    )
                )
                .await()
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    /**
     * Update request status (e.g. IN_TRANSIT or COMPLETED)
     */
    fun updateStatus(requestId: String, status: RequestStatus) = viewModelScope.launch {
        try {
            requestsCollection.document(requestId)
                .update("status", status.value)
                .await()
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
}
