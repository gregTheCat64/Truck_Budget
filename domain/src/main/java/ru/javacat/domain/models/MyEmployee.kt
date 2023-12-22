package ru.javacat.domain.models

import java.time.LocalDate

data class MyEmployee (
    val fullName: String,
    val duty: String,
    val passportNumber: Int,
    val passportReceivedDate: String,
    val passportReceivedPlace: String,
    val driveLicenseNumber: String?,
    val placeOfRegistration: String?
)
