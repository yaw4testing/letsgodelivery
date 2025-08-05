package com.letsGodelivery.app.data.models

import com.google.firebase.database.PropertyName

data class Address(
    val streetLine1: String? = null,
    val streetLine2: String? = null,
    val city: String? = null,
    @get:PropertyName("stateOrProvince") @set:PropertyName("stateOrProvince")
    var stateOrProvince: String? = null, // Naming convention consistency
    val postalCode: String? = null,
    val country: String? = null,
    val countryCode: String? = null // e.g., "GH", "US"
) {
    constructor() : this(null, null, null, null, null, null, null) // For Firestore
}
data class User(
    val uid: String = "", // Firebase Auth User ID
    val email: String = "",
    val userType: UserType = UserType.CUSTOMER, // "CUSTOMER" or "DRIVER"
    val displayName: String = "", // Optional: for display name
    val phoneNumber: String = "", // Optional
    val address: Address? = null,
    val createdAt: Long = System.currentTimeMillis(), // Timestamp
    val gpsLatitude: Double? = null,
    val gpsLongitude: Double? = null,

)

enum class UserType {
    CUSTOMER, DRIVER
}