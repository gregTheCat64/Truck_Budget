package ru.javacat.domain.models

data class Staff (
    val fullName: String,
    val duty: String,
    val passportNumber: Int,
    val passportReceivedDate: String,
    val passportReceivedPlace: String,
    val driveLicenseNumber: String?,
    val placeOfRegistration: String?
)
