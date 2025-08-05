package com.letsGodelivery.app.data.models

import com.google.firebase.Timestamp


data class DeliveryRequest(
    val id: String = "",
    val requestType: RequestType = RequestType.PARCEL,
    val customerId: String = "",
    val userDisplayName: String = "",
    val dropoffAddress: String = "",
    val pickupLat: Double = 0.0,
    val pickupLng: Double = 0.0,
    val dropoffLat: Double = 0.0,
    val dropoffLng: Double = 0.0,
    val status: String = RequestStatus.OPEN.value,
    val assignedDriverId: String? = null,
    val fee: Double = 0.0,
    val description: String,
    val pickupAddress: String,
    val parcelSize: String,
    val createdAt: Timestamp? = null,
)