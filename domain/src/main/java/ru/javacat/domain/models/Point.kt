package ru.javacat.domain.models

import java.time.LocalDate

data class Point(
    val id: String,
    val location: String,
    val arrivalDate: LocalDate
)
