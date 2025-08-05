package com.letsGodelivery.app.data.models


data class User(
    val uid: String = "", // Firebase Auth User ID
    val email: String = "",
    val userType: UserType = UserType.CUSTOMER, // "CUSTOMER" or "DRIVER"
    val displayName: String = "", // Optional: for display name
    val phoneNumber: String = "", // Optional
    val createdAt: Long = System.currentTimeMillis(), // Timestamp
    val country: String = "Ghana",
    val city: String = "",
    val gpsLatitude: Double? = null,
    val gpsLongitude: Double? = null,
    val address: String = ""

)

enum class UserType {
    CUSTOMER, DRIVER
}