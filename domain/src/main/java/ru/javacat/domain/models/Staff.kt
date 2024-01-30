package ru.javacat.domain.models

import java.time.LocalDate

data class Staff (
    val id: Int,
    val fullName: String,
    val passportSerial: String,
    val passportNumber: String,
    val passportReceivedDate: String?,
    val passportReceivedPlace: String?,
    val driveLicenseNumber: String?,
    val placeOfRegistration: String?,
    val phoneNumber: String?
)
