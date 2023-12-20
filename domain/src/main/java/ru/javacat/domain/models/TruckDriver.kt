package ru.javacat.domain.models

import java.time.LocalDate

data class TruckDriver (
    val fullName: String,
    val idNumber: Long,
    val idReceivedDate: LocalDate,
    val idReceivedPlace: String,
    val registeredAt: String
)
