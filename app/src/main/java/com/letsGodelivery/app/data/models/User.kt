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
    fun toFormattedString(): String {
        val parts = listOfNotNull(
            streetLine1,
            streetLine2,
            city,
            stateOrProvince,
            postalCode,
            country
        )
        return parts.filter { it.isNotBlank() }.joinToString(", ")
    }
}
data class User(
    val uid: String = "",
    val email: String = "",
    val userType: UserType = UserType.CUSTOMER,
    val displayName: String = "",
    val phoneNumber: String = "",
    val address: Address? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val gpsLatitude: Double? = null,
    val gpsLongitude: Double? = null,

)

enum class UserType {
    CUSTOMER, DRIVER
}