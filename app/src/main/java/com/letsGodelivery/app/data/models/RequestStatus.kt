package com.letsGodelivery.app.data.models

enum class RequestStatus(val value: String) {
    OPEN("OPEN"),
    ASSIGNED("ASSIGNED"),
    IN_TRANSIT("IN_TRANSIT"),
    COMPLETED("COMPLETED")
}